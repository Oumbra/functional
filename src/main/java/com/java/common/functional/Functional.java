package com.java.common.functional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.java.common.functional.lambda.ThrowFunction;
import com.java.common.structure.CastableList;
import com.java.common.structure.FunctionalList;

public class Functional {

    public static class Validator {

        public static <T> Predicate<T> distinctBy(Function<? super T, Object> keyExtractor) {
            Map<Object, Boolean> cache = new ConcurrentHashMap<>();
            return obj -> cache.putIfAbsent(keyExtractor.apply(obj), Boolean.TRUE) == null;
        }

        public static <T> Predicate<T> isNull() {
            return obj -> obj == null;
        }

        public static <T> Predicate<T> isNotNull() {
            return obj -> obj != null;
        }

        public static Predicate<Collection<?>> isEmpty() {
            return obj -> obj.isEmpty();
        }

        public static Predicate<Collection<?>> isNotEmpty() {
            return obj -> !obj.isEmpty();
        }

        @SafeVarargs
        public static <O,T> Predicate<O> isIn(Function<O,T> transformer, T...comparators) {
            return obj -> Arrays.asList(comparators).contains(transformer.apply(obj));
        }

        public static <O,T> Predicate<O> isIn(Function<O,T> transformer, Collection<T> comparators) {
            return obj -> comparators.contains(transformer.apply(obj));
        }

        /**
         * Retourne un predicate permettant de verifier si une valeur fait partie d'un ensemble
         * @param comparators Ensemble de valeur de comparaison
         * @param <T>
         * @return
         */
        @SafeVarargs
        public static <T> Predicate<T> isIn(T...comparators) {
            return isIn(e -> e, comparators);
        }
        
        public static <T> Predicate<T> isIn(Collection<T> comparators) {
            return isIn(e -> e, comparators);
        }

        public static <O,T> Predicate<O> isNotIn(Function<O,T> transformer, T...comparators) {
            return obj -> !Arrays.asList(comparators).contains(transformer.apply(obj));
        }
        
        public static <O,T> Predicate<O> isNotIn(Function<O,T> transformer, Collection<T> comparators) {
            return obj -> !comparators.contains(transformer.apply(obj));
        }

        public static <T> Predicate<T> isNotIn(T...comparators) {
            return isNotIn(e -> e, comparators);
        }
        
        public static <T> Predicate<T> isNotIn(Collection<T> comparators) {
            return isNotIn(e -> e, comparators);
        }

        public static <O,T> Predicate<O> isEquals(ThrowFunction<O,T> transformer, T comparator) {
            return obj -> Try.of(transformer).apply(obj).toOption().map(comparator::equals).orElse(false);
        }

        public static <T> Predicate<T> isEquals(T comparator) {
            return isEquals(e -> e, comparator);
        }

        public static <O,T> Predicate<O> isNotEquals(ThrowFunction<O,T> transformer, T comparator) {
            return obj -> !Try.of(transformer).apply(obj).toOption().map(comparator::equals).orElse(false);
        }

        public static <T> Predicate<T> isNotEquals(T comparator) {
            return isNotEquals(e -> e, comparator);
        }
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
