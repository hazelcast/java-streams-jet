package org.hazelcast.blog;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.pipeline.*;

import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        var stream = Stream.iterate(1, i -> i + 1);
        var pipeline = Pipeline.create();
        var batch = SourceBuilder
                .batch("java-8-stream", new Java8StreamSource<>(stream))
                .fillBufferFn(new Java8StreamFiller<>())
                .build();
        pipeline.drawFrom(batch)
                .drainTo(Sinks.logger());
        var jet = Jet.newJetInstance(new JetConfig());
        try {
            jet.newJob(pipeline).join();
        } finally {
            jet.shutdown();
        }
    }
}
