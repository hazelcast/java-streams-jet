package org.hazelcast.blog;

import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.function.FunctionEx;
import com.hazelcast.jet.function.SupplierEx;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java8StreamSource<T> implements FunctionEx<Processor.Context, Iterator<T>> {

    private final SupplierEx<Stream<T>> supplier;

    public Java8StreamSource(SupplierEx<Stream<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Iterator<T> applyEx(Processor.Context context) {
        return supplier.get().iterator();
    }
}