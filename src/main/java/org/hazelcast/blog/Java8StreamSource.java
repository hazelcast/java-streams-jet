package org.hazelcast.blog;

import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.function.FunctionEx;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java8StreamSource<T> implements FunctionEx<Processor.Context, Iterator<T>> {

    private final List<T> elements;

    public Java8StreamSource(Stream<T> stream, int limit) {
        this.elements = stream.limit(limit).collect(Collectors.toList());
    }

    @Override
    public Iterator<T> applyEx(Processor.Context context) {
        return elements.iterator();
    }
}