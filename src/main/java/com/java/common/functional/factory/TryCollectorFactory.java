package com.java.common.functional.factory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.java.common.functional.Try;
import com.java.common.functional.collector.TryCollector;
import com.java.common.functional.enums.TryType;
import com.java.common.structure.FunctionalList;

public class TryCollectorFactory {

    /**
     * See usage {@link TryFactory#lazyOf(com.java.common.functional.lambda.FunctionThrowable)}
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Collector<Supplier<Try<E>>, Map<TryType, LinkedList<Try<E>>>, Try<List<E>>> collect() {
        return new TryCollector<>();
    }

    /**
     * Permet d'effectuer une reduction afin de récupérer la première erreur potentiellement apparue
     * See usage {@link TryFactory#lazyOf(com.java.common.functional.lambda.FunctionThrowable)}
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Collector<Supplier<Try<E>>, FunctionalList<Try<E>>, Try<E>> lazyReduce() {
        return Collector.of(
            () -> new FunctionalList<Try<E>>(),
            (acc, fn) -> acc.add(fn.get()),
            (left, right) -> left.get().isFailure()? left: right,
            acc -> acc.isEmpty()? TryFactory.<E>empty(): acc.get()
        );
    }

    public static <E> Collector<Supplier<Try<Try<E>>>, FunctionalList<Try<E>>, Try<E>> lazyFlatReduce(E defaultValue) {
        return Collector.of(
            () -> new FunctionalList<Try<E>>(),
            (acc, fn) -> {
                Try<Try<E>> r = fn.get();
                acc.add(r.isSuccess()? r.asSuccess().getResult(): (Try<E>) r);
            },
            (left, right) -> left.get().isFailure()? left: right,
            acc -> {
                Try<E> maybeResult = acc.isEmpty()? TryFactory.success(defaultValue): acc.parallelStream()
                    .filter(Try::isFailure)
                    .findFirst()
                    .orElseGet(() -> acc.get());
                return maybeResult;
            }
        );
    }

    public static <E> Collector<Supplier<Try<Try<E>>>, FunctionalList<Try<E>>, Try<E>> lazyFlatReduce() {
        return lazyFlatReduce(null);
    }

    /**
     * Regroupe les résultats par type {@link TryType#SUCCESS} {@link TryType#FAILURE}
     * <br><br>
     * <b><u>Usage :</u></b><br>
     * <code>
     *  Map&#60;Type, List&#60;Try&#60;Double&#62;&#62; result = Stream.of(1,2,3)<br>
     *  &#32;&#32;&#32;&#32;.map(Try.of(i -> i / (i - 1)))<br>
     *  &#32;&#32;&#32;&#32;.collect(Try.groupBySuccess())<br>
     *  # result.get(Type.SUCCESS) renvera une liste de deux elements : [2, 1.5]<br>
     *  # result.get(Type.FAILURE) renvera une liste d'un element : [Try]
     * </code>
     * @param <E> Type des objets Try
     * @return
     */
    public static <E> Collector<Try<E>, ?, Map<TryType, List<Try<E>>>> groupingBySuccess() {
        return Collectors.groupingBy(t -> t.getType());
    }

    /**
     * Permet d'effectuer une reduction afin de récupérer la première erreur potentiellement apparue
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Collector<Try<E>, ?, Try<E>> reduce() {
        return Collectors.reducing(TryFactory.empty(), (previous, current) -> previous.isFailure()? previous: current);
    }
    
    /**
     * Permet d'effectuer une reduction afin de ne récupèrer uniquement les résultats des fonctions n'ayant eu aucune erreur
     * @return
     */
    public static <E> Collector<Try<E>, ?, FunctionalList<E>> successOnly() {
    	return Collector.of(
			() -> new FunctionalList<E>(),
			(acc, tryValue) -> {
				if (tryValue.isSuccess()) 
					acc.add(tryValue.asSuccess().getResult());
			},
			(left, right) -> left.addAllChain(right)
		);
    }
    
}
