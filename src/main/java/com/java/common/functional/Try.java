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
     * @return {@link com.java.common.functional.enums.TryType#SUCCESS} ou {@link com.java.common.functional.enums.TryType#FAILURE}
     */
    TryType getType();

    /**
     * Vérifie si le résultat ne contient aucune donnée
     * @return
     */
    boolean isEmpty();

    /**
     * Verifie si l'execution s'est déroulé sans erreur
     * @return
     */
    boolean isSuccess();

    /**
     * Verifie si l'execution a rencontrer une erreur
     * @return
     */
    boolean isFailure();

    /**
     * Cast l'instance Try en instance de {@link Success}
     * @return
     */
    Success<E> asSuccess();

    /**
     * Cast l'instance Try en instance de {@link Failure}
     * @return
     */
    Failure<E> asFailure();

    /**
     * Retourne la valeur potentielle ou Option.empty()
     * @return
     */
    Optional<E> toOption();

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou la valeur par defaut
     * @param defaultValue Valeur par defaut
     * @return
     */
    E getOrElse(E defaultValue);

    /**
     * Retourne la valeur si aucune erreur n'a été rencontré ou souleve l'erreur
     * @return
     * @throws Exception
     */
    E getOrThrow() throws Exception;

    /**
     * Retourne la valeur potentielle si aucune erreur n'a été rencontré ou souleve l'erreur
     * @return
     * @throws Exception
     */
    Optional<E> getOptionOrThrow() throws Exception;

    /**
     * Execute une lambda de type Consumer avec la valeur en entrée si aucune erreur n'a été rencontré
     * @param consumer Lambda de type Consumer
     */
    void ifPresent(Consumer<E> consumer);

    Try<E> peekIfPresent(Consumer<E> consumer);

    void ifAbsent(Consumer<Exception> consumer);

    Try<E> peekIfAbsent(Consumer<Exception> consumer);

    /**
     * Applique un filtre sur la valeur encapsulee
     * @param predicate Contrainte à appliquer
     * @return Retourne l'instance en cours si la contrainte est valide ou si l'instance est une Failure; dans le cas contraire, renvoi une instance vierge
     */
    Try<E> filter(Predicate<E> predicate);

    /**
     * Applique un filtre sur la valeur encapsulee et si celui-ci est valide; execute et retourne le résultat du transformateur
     * @param predicate Contrainte à appliquer sur la valeur de l'instance courante
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @return Valeur encapsulé de la lambda de transformation, l'instance courante qui est une erreur ou bien l'erreur encontree au sein de la lambda de transformation
     */
    Try<E> when(Predicate<E> predicate, FunctionThrowable<E, E> transformer);

    /**
     * Applique un filtre sur la valeur encapsulee et si celui-ci est valide; execute et retourne le résultat du transformateur
     * @param predicate Contrainte à appliquer sur la valeur de l'instance courante
     * @param transformer Lambda de transformation (retourne une instance de TRY), les exceptions sont gérées
     * @return Valeur encapsulé de la lambda de transformation, l'instance courante qui est une erreur ou bien l'erreur encontree au sein de la lambda de transformation
     */
    Try<E> flatWhen(Predicate<E> predicate, FunctionThrowable<E, Try<E>> transformer);

    /**
     * Exécute une lambda, seulement s'il s'agit d'un Try vide
     * @param getter Lambda de récupération, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> whenIsEmpty(SupplierThrowable<O> getter);

    /**
     * Exécute une lambda, seulement s'il s'agit d'un Try vide
     * @param getter Lambda de récupération retournant un Try, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> flatWhenIsEmpty(SupplierThrowable<Try<O>> getter);

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> whenIsSuccess(FunctionThrowable<E, O> transformer);

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS
     * @param transformer Lambda de transformation retournant elle même un Try, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> flatWhenIsSuccess(FunctionThrowable<E, Try<O>> transformer);

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS /!\ ET NON EMPTY /!\
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> whenIsSuccessStrict(FunctionThrowable<E, O> transformer);

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type SUCCESS /!\ ET NON EMPTY /!\
     * @param transformer Lambda de transformation retournant elle même un Try, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> flatWhenIsSuccessStrict(FunctionThrowable<E, Try<O>> transformer);

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type FAILURE
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> whenIsFailure(Function<Exception, O> transformer);

    /**
     * Applique une transformation à la valeur encapsulé par l'objet Try, seulement s'il s'agit d'un Try de type FAILURE
     * @param transformer Lambda de transformation, les exceptions sont gérées
     * @param <O> Type de sortie
     * @return
     */
    <O> Try<O> flatWhenIsFailure(Function<Exception, Try<O>> transformer);

}
