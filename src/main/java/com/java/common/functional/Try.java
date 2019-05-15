package com.java.common.functional;

import com.java.common.functional.enums.TryType;
import com.java.common.functional.impl.Failure;
import com.java.common.functional.impl.Success;
import com.java.common.functional.lambda.FunctionThrowable;
import com.java.common.functional.lambda.SupplierThrowable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Try<E> {

    /**
     * Retourne le type de l'execution
     *
     * @return {@link com.java.common.functional.enums.TryType#SUCCESS} ou {@link com.java.common.functional.enums.TryType#FAILURE}
     */
    TryType getType();

    /**
     * Vérifie si le résultat ne contient aucune donnée
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Verifie si l'execution s'est déroulé sans erreur
     *
     * @return
     */
    boolean isSuccess();

    /**
     * Verifie si l'execution a rencontrer une erreur
     *
     * @return
     */
    boolean isFailure();

    /**
     * Cast l'instance Try en instance de {@link Success}
     *
     * @return
     */
    Success<E> asSuccess();

    /**
     * Cast l'instance Try en instance de {@link Failure}
     *
     * @return
     */
    Failure<E> asFailure();

    /**
     * Retourne la valeur potentielle ou Option.empty()
     *
     * @return
     */
    Optional<E> toOption();

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou la valeur par defaut
     *
     * @param defaultValue Valeur par defaut
     *
     * @return
     */
    E getOrElse(E defaultValue);

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou souleve l'erreur
     *
     * @return
     *
     * @throws Exception
     */
    E getOrThrow() throws Exception;

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou souleve l'erreur
     *
     * @param transformer Transforme l'exception en un type spécifié
     *
     * @return
     *
     * @throws Exception
     */
    <ET extends Throwable> E getOrThrow(Function<Exception, ET> transformer) throws ET;

    /**
     * Retourne la valeur potentielle si aucune erreur n'a été rencontré ou souleve l'erreur
     *
     * @return
     *
     * @throws Exception
     */
    Optional<E> getOptionOrThrow() throws Exception;

    /**
     * Retourne la valeur potentielle si aucune erreur n'a été rencontré ou souleve l'erreur
     *
     * @param transformer Transforme l'exception en un type spécifié
     *
     * @return
     *
     * @throws ET
     */
    <ET extends Throwable> Optional<E> getOptionOrThrow(Function<Exception, ET> transformer) throws ET;

    /**
     * Si l'instance est de type SUCCESS: execute une lambda avec la valeur de l'instance
     *
     * @param consumer Lambda de type Consumer
     */
    void ifPresent(Consumer<E> consumer);

    /**
     * Si l'instance est de type SUCCESS: execute une lambda avec la valeur de l'instance avant de renvoyer l'instance courante
     *
     * @param consumer Lambda de type Consumer
     *
     * @return Instance courante
     */
    Try<E> peekIfPresent(Consumer<E> consumer);

    /**
     * Si l'instance est de type FAILURE: execute une lambda avec l'erreur de l'instance
     *
     * @param consumer Lambda de type Consumer
     */
    void ifAbsent(Consumer<Exception> consumer);

    /**
     * Si l'instance est de type FAILURE: execute une lambda avec la valeur de l'instance avant de renvoyer l'instance courante
     *
     * @param consumer Lambda de type Consumer
     *
     * @return Instance courante
     */
    Try<E> peekIfAbsent(Consumer<Exception> consumer);

    /**
     * Applique un filtre sur la valeur encapsulee
     *
     * @param predicate Contrainte à appliquer
     *
     * @return Retourne l'instance en cours si la contrainte est valide ou si l'instance est une Failure; dans le cas contraire, renvoi une instance vierge
     */
    Try<E> filter(Predicate<E> predicate);

    /**
     * Applique un filtre sur l'instance et si celui-ci est valide; execute et retourne la valeur résultant de la transformation
     *
     * @param predicate   Contrainte à appliquer sur l'instance courante
     * @param transformer Lambda de transformation, les exceptions sont gérées
     *
     * @return le résultat de la lambda de transformation (valeur ou erreur) ou bien l'instance courante
     */
    Try<E> when(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, E> transformer);

    /**
     * Applique un filtre sur l'instance et si celui-ci est valide; execute et retourne la valeur résultant de la transformation
     *
     * @param predicate   Contrainte à appliquer sur l'instance courante
     * @param transformer Lambda de transformation (retourne une instance de ITRY), les exceptions sont gérées
     *
     * @return le résultat de la lambda de transformation (valeur ou erreur) ou bien l'instance courante
     */
    Try<E> flatWhen(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, Try<E>> transformer);

    /**
     * Applique un filtre sur l'instance et si celui-ci est valide; execute et retourne la valeur résultant de la transformation.
     * <br>
     * Si l'instance ne correspond pas au filtre, l'instance retournée est soit vide, soit l'erreur
     *
     * @param predicate   Contrainte à appliquer sur l'instance courante
     * @param transformer Lambda de transformation, les exceptions sont gérées
     *
     * @return le résultat de la lambda de transformation (valeur ou erreur) ou bien l'instance calculée
     */
    <O> Try<O> mapWhen(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, O> transformer);


    /**
     * Applique un filtre sur l'instance et si celui-ci est valide; execute et retourne la valeur résultant de la transformation
     * <br>
     * Si l'instance ne correspond pas au filtre, l'instance retournée est soit vide, soit l'erreur
     *
     * @param predicate   Contrainte à appliquer sur l'instance courante
     * @param transformer Lambda de transformation (retourne une instance de ITRY), les exceptions sont gérées
     *
     * @return le résultat de la lambda de transformation (valeur ou erreur) ou bien l'instance calculée
     */
    <O> Try<O> mapFlatWhen(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, Try<O>> transformer);
}
