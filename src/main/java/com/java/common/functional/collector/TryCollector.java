package com.java.common.functional.collector;

import com.java.common.functional.Try;
import com.java.common.functional.enums.TryType;
import com.java.common.functional.impl.Failure;
import com.java.common.functional.impl.Success;
import com.java.common.functional.impl.TryImpl;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * A Collector implementation processing elements as long as results are success
 * @param <E>
 */
public class TryCollector<E> implements Collector<Supplier<Try<E>>, Map<TryType, LinkedList<Try<E>>>, Try<List<E>>> {

    @Override
    public Supplier<Map<TryType, LinkedList<Try<E>>>> supplier() {
        return () -> {
            HashMap<TryType, LinkedList<Try<E>>> map = new HashMap<>();
            map.put(TryType.SUCCESS, new LinkedList<>());
            map.put(TryType.FAILURE, new LinkedList<>());
            return map;
        };
    }

    @Override
    public BiConsumer<Map<TryType, LinkedList<Try<E>>>, Supplier<Try<E>>> accumulator() {
        return (results, supplier) -> {
            if(results.get(TryType.FAILURE).isEmpty()) {
                Try<E> result = supplier.get();
                results.get(result.getType()).add(result);
            }
        };
    }

    @Override
    public BinaryOperator<Map<TryType, LinkedList<Try<E>>>> combiner() {
        return (left, right) -> {
            if(!left.get(TryType.FAILURE).isEmpty()) {
                return left;
            }

            if(!right.get(TryType.FAILURE).isEmpty()) {
                return right;
            }

            left.get(TryType.SUCCESS).addAll(right.get(TryType.SUCCESS));
            return left;
        };
    }

	@Override
	@SuppressWarnings("unchecked")
    public Function<Map<TryType, LinkedList<Try<E>>>, Try<List<E>>> finisher() {
        return results -> {
            if(results.get(TryType.FAILURE).isEmpty()) {
                List<E> collect = results.get(TryType.SUCCESS).stream()
            		.map(t -> t.asSuccess().getResult())
            		.collect(Collectors.toList());
                
                return new Success<>(collect);
            } else {
                return (Failure<List<E>>) results.get(TryType.FAILURE).pop();
            }
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>(0);
    }
}