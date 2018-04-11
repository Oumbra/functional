package com.java.common.functional.factory;

import com.java.common.functional.Try;
import com.java.common.functional.impl.TryImpl;
import com.java.common.functional.impl.Failure;
import com.java.common.functional.impl.Success;
import com.java.common.functional.lambda.FunctionThrowable;
import com.java.common.functional.lambda.RunnableThrowable;
import com.java.common.functional.lambda.SupplierThrowable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class TryFactory {
    /**
     * Retourne un objet Try vide (Success avec une valeur nulle)
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> TryImpl<E> empty() {
        return success(null);
    }

    /**
     * Créé un objet Try de type FAILURE
     * @param e Exception de l'objet Try
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> TryImpl<E> failure(Exception e) {
        return new Failure<E>(e);
    }

    /**
     * Créé un objet Try de type SUCCESS
     * @param value Valeur de l'objet Try
     * @param <E> Type de l'objet Try
     * @return
     */
    public static <E> TryImpl<E> success(E value) {
        return new Success<>(value);
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
    public static <I, O> FunctionThrowable<I, Try<O>> of(FunctionThrowable<I, O> function) {
        return of(function, (e, item) -> e);
    }

    /**
     * See usage {@link #of(FunctionThrowable)}
     * @param function Fonction à transformer
     * @param error Fonction à deux entrées en cas d'erreur<br>
     *              &#32;&#32;&#32;&#32;- entrée 1 : erreur soulevée<br>
     *              &#32;&#32;&#32;&#32;- entrée 2 : instance de l'objet sur laquelle l'erreur est provoquée
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <I, O> FunctionThrowable<I, Try<O>> of(FunctionThrowable<I, O> function, BiFunction<Exception, I, Exception> error) {
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
     * @param supplier Lambda à transformer
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <O> Supplier<Try<O>> of(SupplierThrowable<O> supplier) {
        return of(supplier, e -> e);
    }

    /**
     * See usage {@link #of(SupplierThrowable)}
     * @param supplier Fonction à transformer
     * @param error Fonction en cas d'erreur<br>
     * @param <O> Type de l'objet de sortie
     * @return
     */
    private static <O> Supplier<Try<O>> of(SupplierThrowable<O> supplier, Function<Exception, Exception> error) {
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
     * Transforme une lambda de type Function&#60;I,O&#62; en une Function&#60;I, Supplier&#60;Try&#60;O&#62;&#62;&#62;.<br>
     * Contrairement à la méthode {@link #of(FunctionThrowable)} qui est indépendante, celle-ci doit être utilisé avec la fonction {@link java.util.stream.Stream#collect(Collector)}.<br>
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
    public static <I, O> Function<I, Supplier<Try<O>>> lazyOf(FunctionThrowable<I, O> function) {
        return lazyOf(function, (e,i) -> e);
    }

    /**
     * See usage {@link TryFactory#lazyOf(com.java.common.functional.lambda.FunctionThrowable)}
     * @param function Lambda à transformer
     * @param error Fonction à deux entrées en cas d'erreur<br>
     *              &#32;&#32;&#32;&#32;- entrée 1 : erreur soulevée<br>
     *              &#32;&#32;&#32;&#32;- entrée 2 : instance de l'objet sur laquelle l'erreur est provoquée
     * @param <I> Type de l'objet en entré
     * @param <O> Type de l'objet de sortie
     * @return
     */
    public static <I, O> Function<I, Supplier<Try<O>>> lazyOf(FunctionThrowable<I, O> function, BiFunction<Exception, I, Exception> error) {
        Function<I, Try<O>> of = of(function, error);
        return input -> () -> of.apply(input);
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
    public static <O> Try<O> run(SupplierThrowable<O> supplier) {
        return of(supplier).get();
    }

    public static <O> Try<O> flatRun(SupplierThrowable<Try<O>> supplier) {
        return run(() -> supplier.getThrows().getOrThrow());
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
    public static <O> Try<O> run(RunnableThrowable runnable, O defaultValue) {
        return run(() -> {
            runnable.runThrows();
            return defaultValue;
        });
    }
}
