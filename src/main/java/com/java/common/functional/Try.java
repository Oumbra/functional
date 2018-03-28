package com.java.common.functional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.java.common.functional.collector.TryCollector;
import com.java.common.functional.impl.Failure;
import com.java.common.functional.impl.Success;
import com.java.common.functional.lambda.ThrowFunction;
import com.java.common.functional.lambda.ThrowRunnable;
import com.java.common.functional.lambda.ThrowSupplier;
import com.java.common.structure.FunctionalList;


/**
 * A instance of a Try represents an attempt to compute a value. A Try is either a success, either a failure.
 * @param <E>
 */
public abstract class Try<E> {

    /**
     * Enumerate the different types of a Try
     */
    public enum Type {
        SUCCESS, FAILURE
    }

    /**
     * Retourne un objet Try vide (Success avec une valeur nulle)
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Try<E> empty() {
    	return success(null);
    }

    /**
     * Créé un objet Try de type FAILURE
     * @param e Exception de l'objet Try
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Try<E> failure(Exception e) {
    	return new Failure<E>(e);
    }

    /**
     * Créé un objet Try de type SUCCESS
     * @param value Valeur de l'objet Try
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Try<E> success(E value) {
        return new Success<>(value);
    }

    /**
     * Récupère la valeur d'un objet Try
     * @param tryObj Objet duquel la valeur doit être extraite
     * @param <T> Type de l'objet Try
     * @exception ClassCastException
     * @return
     */
	public static <T> T getResult(Try<T> tryObj) {
		return tryObj.asSuccess().getResult();
	}
    
    /**
     * Transforme une lambda de type Function&#60;I,O&#62; en une Function&#60;I,Try&#60;O&#62;&#62;.
     * <br><br>
     * <b><u>Usage :</u></b><br>
     * <code>
     *  Try&#60;Double&#62; result = Stream.of(1,2,3)<br>
     *  &#32;&#32;&#32;&#32;.map(Try.of(i -> i / (i - 1)))<br>
     *  &#32;&#32;&#32;&#32;.reduce(Try.reduce())<br>
     *  # result.isSuccess() renvera 'false' car une division par zero aura été provoquée
     * </code>
     *
     * @param function Fonction à transformer
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <I, O> ThrowFunction<I, Try<O>> of(ThrowFunction<I, O> function) {
    	return of(function, (e, item) -> e);
    }

    /**
     * See usage {@link #of(ThrowFunction)}
     * @param function Fonction à transformer
     * @param error Fonction à deux entrées en cas d'erreur<br>
     *              &#32;&#32;&#32;&#32;- entrée 1 : erreur soulevée<br>
     *              &#32;&#32;&#32;&#32;- entrée 2 : instance de l'objet sur laquelle l'erreur est provoquée
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <I, O> ThrowFunction<I, Try<O>> of(ThrowFunction<I, O> function, BiFunction<Exception, I, Exception> error) {
    	return input -> {
    		try {
    			O result = function.applyThrows(input);
    			return new Success<>(result);
    		} catch (Exception e) {
    			Exception e2 = error.apply(e, input);
    			return new Failure<>(e2);
    		}
    	};
    }

    /**
     * Transforme une lambda de type Supplier&#60;O&#62; en Supplier&#60;Try&#60;O&#62;&#62;.
     * <br><br>
     * <b><u>Usage :</u></b><br>
     * <code>
     *  Try&#60;Double&#62; result = Try.of(() -> 100 / 0).get()
     *  # result.getOrThrow() provoquera une erreure
     *  <br><br>
     *  Try&#60;Boolean&#62; result = Try.of(() -> 100 / 1).get()<br>
     *  # result.getOrThrow() renvera '100'
     * </code>
     *
     * @param function Fonction à transformer
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    private static <O> Supplier<Try<O>> of(ThrowSupplier<O> supplier) {
        return of(supplier, e -> e);
    }

    /**
     * See usage {@link #of(ThrowSupplier)}
     * @param supplier Fonction à transformer
     * @param error Fonction en cas d'erreur<br>
     * @param <O> Type de l'objet de sortie
     * @return
     */
    private static <O> Supplier<Try<O>> of(ThrowSupplier<O> supplier, Function<Exception, Exception> error) {
    	return () -> {
            try {
                O result = supplier.getThrows();
                return new Success<>(result);
            } catch (Exception e) {
            	Exception e2 = error.apply(e);
                return new Failure<>(e2);
            }
        };
    }
    
    /**
     * Execute une lambda de type Supplier&#60;O&#62; pouvant provoquer une erreure.
     * <br><br>
     * <b><u>Usage :</u></b><br>
     * <code>
     *  Try&#60;Double&#62; result = Try.run(() -> 100 / 0)
     *  # result.getOrThrow() provoquera une erreure
     *  <br><br>
     *  Try&#60;Boolean&#62; result = Try.run(() -> 100 / 1)<br>
     *  # result.getOrThrow() renvera '100'
     * </code>
     *
     * @param supplier Lambda à executer
     * @param <O> Type de l'objet de sortie
     * @return Retourne le résultat de l'execution
     */
    public static <O> Try<O> run(ThrowSupplier<O> supplier) {
    	return of(supplier).get();
    }

    public static <O> Try<O> flatRun(ThrowSupplier<Try<O>> supplier) {
        return of(() -> supplier.getThrows().getOrThrow()).get();
    }

    /**
     * Execute une lambda de type Runnable pouvant provoquer une erreure.
     * <br><br>
     * <b><u>Usage :</u></b><br>
     * <code>
     *  Try&#60;Boolean&#62; result = Try.run(() -> 100 / 0, true)<br>
     *  # result.getOrThrow() provoquera une erreure
     *  <br><br>
     *  Try&#60;Boolean&#62; result = Try.run(() -> 100 / 1, true)<br>
     *  # result.getOrThrow() renvera 'true'
     * </code>
     *
     * @param runnable Lambda à executer
     * @param defaultValue Valeur par défaut en cas de succès
     * @param <O> Type de l'objet de sortie
     * @return Retourne le résultat de l'execution
     */
    public static <O> Try<O> run(ThrowRunnable runnable, O defaultValue) {
    	return run(() -> {
    		runnable.runThrows();
    		return defaultValue;
    	});
    }
    
    /**
     * Transforme une lambda de type Function&#60;I,O&#62; en une Function&#60;I, Supplier&#60;Try&#60;O&#62;&#62;&#62;.<br>
     * Contrairement à la méthode {@link #of(ThrowFunction)} qui est indépendante, celle-ci doit être utilisé avec la fonction {@link java.util.stream.Stream#collect(Collector)}.<br>
     * Effectivement la différence réside dans le fait que le traitement s'achevra dès la première erreure.
     * Il est possible d'utiliser les deux reducteurs suivant afin de déclencher le traitement :
     * <br/><br/>
     * <b>Try.collect()</b> ainsi que <b>Try.lazyReduce()</b>
     * <br><br>
     * <b><u>Usage :</u></b><br>
     * <code>
     *  Try&#60;List&#60;Double&#62;&#62; result = Stream.of(1,2,3)<br>
     *  &#32;&#32;&#32;&#32;.map(Try.lazyOf(i -> i / (i - 1)))<br>
     *  &#32;&#32;&#32;&#32;.collect(Try.collect())<br><br>
     *  # result.isSuccess() renvera 'false' car une division par zero aura été provoquée
     *  <br><br>
     *  Try&#60;Integer&#62; result = Stream.of(1,2,3)<br>
     *  &#32;&#32;&#32;&#32;.map(Try.lazyOf(i -> i +1))<br>
     *  &#32;&#32;&#32;&#32;.collect(Try.lazyReduce())<br><br>
     *  # result.isSuccess() renvera 'true' car aucune erreur aura été provoquée<br>
     *  # result.asSuccess().getResult() renvera 4, cette valeur étant le dernier resultat sans erreur
     * </code>
     * @param function Lambda à transformer
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <I, O> Function<I, Supplier<Try<O>>> lazyOf(ThrowFunction<I, O> function) {
    	return lazyOf(function, (e,i) -> e);
    }

    /**
     * See usage {@link #lazyOf(ThrowSupplier)}
     * @param function Lambda à transformer
     * @param error Fonction à deux entrées en cas d'erreur<br>
     *              &#32;&#32;&#32;&#32;- entrée 1 : erreur soulevée<br>
     *              &#32;&#32;&#32;&#32;- entrée 2 : instance de l'objet sur laquelle l'erreur est provoquée
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <I, O> Function<I, Supplier<Try<O>>> lazyOf(ThrowFunction<I, O> function, BiFunction<Exception, I, Exception> error) {
        Function<I, Try<O>> of = of(function, error);
        return input -> {
            return () -> of.apply(input);
        };
    }

    /**
     * Regroupe les résultats par type {@link Try.Type#SUCCESS} {@link Try.Type#FAILURE}
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
    public static <E> Collector<Try<E>, ?, Map<Type, List<Try<E>>>> groupingBySuccess() {
        return Collectors.groupingBy(t -> t.getType());
    }

    /**
     * See usage {@link #lazyOf(ThrowSupplier)}
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Collector<Supplier<Try<E>>, Map<Try.Type, LinkedList<Try<E>>>, Try<List<E>>> collect() {
        return new TryCollector<>();
    }

    /**
     * Permet d'effectuer une reduction afin de récupérer la première erreur potentiellement apparue
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Collector<Try<E>, ?, Try<E>> reduce() {
    	return Collectors.reducing(Try.empty(), (previous, current) -> previous.isFailure()? previous: current);
    }

    /**
     * Permet d'effectuer une reduction afin de récupérer la première erreur potentiellement apparue
     * See usage {@link #lazyOf(ThrowSupplier)}
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> Collector<Supplier<Try<E>>, FunctionalList<Try<E>>, Try<E>> lazyReduce() {
        return Collector.of(
            () -> new FunctionalList<Try<E>>(),
            (acc, fn) -> acc.add(fn.get()),
            (left, right) -> left.get().isFailure()? left: right,
            acc -> acc.isEmpty()? Try.<E>empty(): acc.get()
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
            	Try<E> maybeResult = acc.isEmpty()? Try.success(defaultValue): acc.parallelStream()
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
     * Retourne le type de l'execution
     * @return {@link #Type.SUCCESS} ou {@link #Type.FAILURE}
     */
    public abstract Type getType();

    /**
     * Vérifie si le résultat ne contient aucune donnée
     * @return
     */
    public boolean isEmpty() {
        return Try.empty().equals(this);
    }

    /**
     * Verifie si l'execution s'est déroulé sans erreur
     * @return
     */
    public boolean isSuccess() {
        return getType() == Type.SUCCESS;
    }

    /**
     * Verifie si l'execution a rencontrer une erreur
     * @return
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Applique un filtre sur la valeur encapsulee
     * @param predicate Contrainte à appliquer
     * @return Retourne l'instance en cours si la contrainte est valide ou si l'instance est une Failure; dans le cas contraire, renvoi une instance vierge
     */
    public Try<E> filter(Predicate<E> predicate) {
        return isFailure() || predicate.test(asSuccess().getResult())
            ? this
            : Try.empty();
    }

    /**
     * Applique un filtre sur la valeur encapsulee et si celui-ci est valide; execute et retourne le résultat du transformateur
     * @param predicate Contrainte à appliquer sur la valeur de l'instance courante
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <T> Type de sortie de la lambda de transformation
     * @return Valeur encapsulé de la lambda de transformation, l'instance courante qui est une erreur ou bien l'erreur encontree au sein de la lambda de transformation
     */
    public Try<E> when(Predicate<E> predicate, ThrowFunction<E, E> transformer) {
        return isSuccess() && predicate.test(asSuccess().getResult())?
            of(transformer::apply).apply(asSuccess().getResult()):
            (Try<E>) this;
    }

    /**
     * Applique un filtre sur la valeur encapsulee et si celui-ci est valide; execute et retourne le résultat du transformateur
     * @param predicate Contrainte à appliquer sur la valeur de l'instance courante
     * @param transformer Lambda de transformation (retourne une instance de TRY), les exceptions sont gérées
     * @param <T> Type de sortie de la lambda de transformation
     * @return Valeur encapsulé de la lambda de transformation, l'instance courante qui est une erreur ou bien l'erreur encontree au sein de la lambda de transformation
     */
    public Try<E> flatWhen(Predicate<E> predicate, ThrowFunction<E, Try<E>> transformer) {
        Try<Try<E>> result = isSuccess() && predicate.test(asSuccess().getResult())?
            of(transformer::apply).apply(asSuccess().getResult()):
            (Try<Try<E>>) this;
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
            ? result.asSuccess().getResult()
            : (Try<E>) result;
    }

    /**
     * Exécute une lambda, seulement s'il s'agit d'un Try vide
     * @param getter Lambda de récupération, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> whenIsEmpty(ThrowSupplier<O> getter) {
        return isEmpty()? of(getter).get() : (Try<O>) this;
    }

    /**
     * Exécute une lambda, seulement s'il s'agit d'un Try vide
     * @param getter Lambda de récupération retournant un Try, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> flatWhenIsEmpty(ThrowSupplier<Try<O>> getter) {
    	if (!isEmpty()) return (Try<O>) this;
    	Try<Try<O>> result = whenIsEmpty(getter);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> whenIsSuccess(ThrowFunction<E, O> transformer) {
        return isSuccess()?
            of(transformer::apply).apply(asSuccess().getResult()):
            (Try<O>) this;
    }

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS
     * @param transformer Lambda de transformation retournant elle même un Try, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> flatWhenIsSuccess(ThrowFunction<E, Try<O>> transformer) {
        Try<Try<O>> result = whenIsSuccess(transformer);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS /!\ ET NON EMPTY /!\
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> whenIsSuccessStrict(ThrowFunction<E, O> transformer) {
        return isSuccess() && !isEmpty()? of(transformer::apply).apply(asSuccess().getResult()) : (Try<O>) this;
    }

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS /!\ ET NON EMPTY /!\
     * @param transformer Lambda de transformation retournant elle même un Try, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> flatWhenIsSuccessStrict(ThrowFunction<E, Try<O>> transformer) {
        Try<Try<O>> result = whenIsSuccessStrict(transformer);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type FAILURE
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> whenIsFailure(Function<Exception, O> transformer) {
    	return isFailure() ? of(transformer::apply).apply(asFailure().getException()) : (Try<O>) this;
    }
    
    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type FAILURE
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    public <O> Try<O> flatWhenIsFailure(Function<Exception, Try<O>> transformer) {
        Try<Try<O>> result = whenIsFailure(transformer);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    /**
     * Retourne la valeur potentielle ou Option.empty()
     * @return
     */
    public Optional<E> toOption() {
        return isSuccess() ? Optional.ofNullable(asSuccess().getResult()) : Optional.<E>empty();
    }

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou souleve l'erreur
     * @return
     * @throws Exception
     */
    public E getOrThrow() throws Exception {
        if(isFailure())
            throw asFailure().getException();

        return asSuccess().getResult();
    }

    /**
     * Retourne la valeur potentielle si aucune erreur n'a été rencontré ou souleve l'erreur
     * @return
     * @throws Exception
     */
    public Optional<E> getOptionOrThrow() throws Exception {
        return Optional.ofNullable(getOrThrow());
    }

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou la valeur par defaut
     * @param defaultValue Valeur par defaut
     * @return
     */
    public E getOrElse(E defaultValue) {
    	return isSuccess()? asSuccess().getResult(): defaultValue; 
    }
    
    /**
     * Execute une lambda de type Consumer avec la valeur en entrée si aucune erreur n'a été rencontré
     * @param consumer Lambda de type Consumer
     */
    public void ifPresent(Consumer<E> consumer) {
        if(isSuccess()) consumer.accept(asSuccess().getResult());
    }
    

    public Try<E> peekIfPresent(Consumer<E> consumer) {
        ifPresent(consumer);
        return this;
    }

    public void ifAbsent(Consumer<Exception> consumer) {
        if (isFailure()) consumer.accept(asFailure().getException());
    }
    
    public Try<E> peekIfAbsent(Consumer<Exception> consumer) {
        ifAbsent(consumer);
        return this;
    }

    /**
     * Cast l'instance Try en instance de {@link Success}
     * @return
     */
    public Success<E> asSuccess() {
        return (Success<E>) this;
    }

    /**
     * Cast l'instance Try en instance de {@link Failure}
     * @return
     */
    public Failure<E> asFailure() {
        return (Failure<E>) this;
    }
}