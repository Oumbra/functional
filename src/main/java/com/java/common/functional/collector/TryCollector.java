package com.java.common.functional.collector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.java.common.functional.Try;
import com.java.common.functional.impl.Failure;
import com.java.common.functional.impl.Success;

/**
 * A Collector implementation processing elements as long as results are success
 * @param <E>
 */
public class TryCollector<E> implements Collector<Supplier<Try<E>>, Map<Try.Type, LinkedList<Try<E>>>, Try<List<E>>> {

    @Override
    public Supplier<Map<Try.Type, LinkedList<Try<E>>>> supplier() {
        return () -> {
            HashMap<Try.Type, LinkedList<Try<E>>> map = new HashMap<>();
            map.put(Try.Type.SUCCESS, new LinkedList<>());
            map.put(Try.Type.FAILURE, new LinkedList<>());
            return map;
        };
    }

    @Override
    public BiConsumer<Map<Try.Type, LinkedList<Try<E>>>, Supplier<Try<E>>> accumulator() {
        return (results, supplier) -> {
            if(results.get(Try.Type.FAILURE).isEmpty()) {
                Try<E> result = supplier.get();
                results.get(result.getType()).add(result);
            }
        };
    }

    @Override
    public BinaryOperator<Map<Try.Type, LinkedList<Try<E>>>> combiner() {
        return (left, right) -> {
            if(!left.get(Try.Type.FAILURE).isEmpty()) {
                return left;
            }

            if(!right.get(Try.Type.FAILURE).isEmpty()) {
                return right;
            }

            left.get(Try.Type.SUCCESS).addAll(right.get(Try.Type.SUCCESS));
            return left;
        };
    }

	@Override
	@SuppressWarnings("unchecked")
    public Function<Map<Try.Type, LinkedList<Try<E>>>, Try<List<E>>> finisher() {
        return results -> {
            if(results.get(Try.Type.FAILURE).isEmpty()) {
                List<E> collect = results.get(Try.Type.SUCCESS).stream()
            		.map(Try::getResult)
            		.collect(Collectors.toList());
                
                return new Success<>(collect);
            } else {
                return (Failure<List<E>>) results.get(Try.Type.FAILURE).pop();
            }
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>(0);
    }
}