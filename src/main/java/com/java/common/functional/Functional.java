package com.java.common.functional;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.java.common.structure.CastableList;
import com.java.common.structure.FunctionalList;

public class Functional {

	public static <T> Stream<T> iteratorToStream(Iterator<T> iterator) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
	}
	
    public static Optional<CastableList<?>> allPresent(Object...objects) {
        return allPresent(
            Stream.of(objects)
                .map(Optional::ofNullable)
                .toArray(Optional[]::new)
        );
    }

    public static Optional<CastableList<?>> allPresent(Optional<?>...options) {
        Supplier<FunctionalList> fn = () -> Stream.of(options)
                .map(Optional::get)
                .collect(FunctionalList::new, (acc, e) -> acc.addChain(e), (l1, l2) -> l1.addAllChain(l2));

        return Stream.of(options).allMatch(Optional::isPresent)? Optional.of(fn.get()): Optional.empty();
    }

    public static <T> T create(Supplier<? extends T> constructor, Consumer<? super T> setter) {
        T obj = constructor.get();
        setter.accept(obj);
        return obj;
    }

    public static <T,V> Function<V,T> create(Supplier<? extends T> constructor, BiConsumer<? super T, ? super V> setter) {
        return v -> {
            T obj = constructor.get();
            setter.accept(obj, v);
            return obj;
        };
    }
}
