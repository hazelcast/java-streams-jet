package org.hazelcast.blog;

import com.hazelcast.jet.function.BiConsumerEx;
import com.hazelcast.jet.pipeline.SourceBuilder.SourceBuffer;

import java.util.Iterator;

public class Java8StreamFiller<T> implements BiConsumerEx<Iterator<T>, SourceBuffer<T>> {

    @Override
    public void acceptEx(Iterator<T> iterator, SourceBuffer<T> buffer) {
        for (var i = 0; i < Byte.MAX_VALUE && iterator.hasNext(); i++) {
            buffer.add(iterator.next());
        }
    }
}
