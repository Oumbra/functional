package com.java.common.functional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import com.java.common.functional.factory.TryFactory;
import com.java.common.functional.lambda.FunctionThrowable;

public class Validator {

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

    public static <O,T> Predicate<O> isEquals(FunctionThrowable<O,T> transformer, T comparator) {
        return obj -> TryFactory.of(transformer).apply(obj).toOption().map(comparator::equals).orElse(false);
    }

    public static <T> Predicate<T> isEquals(T comparator) {
        return isEquals(e -> e, comparator);
    }

    public static <O,T> Predicate<O> isNotEquals(FunctionThrowable<O,T> transformer, T comparator) {
        return obj -> !TryFactory.of(transformer).apply(obj).toOption().map(comparator::equals).orElse(false);
    }

    public static <T> Predicate<T> isNotEquals(T comparator) {
        return isNotEquals(e -> e, comparator);
    }
}
