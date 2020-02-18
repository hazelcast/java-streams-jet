package org.hazelcast.blog.rsocket;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        RSocketFactory.receive().acceptor(new SocketAcceptorImpl()).transport(TcpServerTransport.create("localhost", 7000))
                      .start().subscribe();

        var pipeline = Pipeline.create();

        StreamSource<?> source = SourceBuilder.stream("rsocket",
                context -> RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7000)).start().block())
                                              .fillBufferFn((s, buf) -> s.requestStream(DefaultPayload.create("Hello"))
                                                                         .map(Payload::getDataUtf8).take(Byte.MAX_VALUE)
                                                                         .subscribe(buf::add))
                                              .destroyFn(Disposable::dispose)
                                              .build();

        pipeline.drawFrom(source).withoutTimestamps().filter(Objects::nonNull).drainTo(Sinks.logger());
        var jet = Jet.newJetInstance(new JetConfig());
        try {
            jet.newJob(pipeline).join();
        } finally {
            jet.shutdown();
        }
    }

    private static class SocketAcceptorImpl
            implements SocketAcceptor {
        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket reactiveSocket) {
            return Mono.just(new AbstractRSocket() {
                @Override
                public Flux<Payload> requestStream(Payload payload) {
                    return Flux.fromStream(Stream.iterate(1, i -> i + 1).limit(Byte.MAX_VALUE))
                               .map(aLong -> DefaultPayload.create("Interval: " + aLong)).take(Byte.MAX_VALUE);
                }
            });
        }
    }
}
