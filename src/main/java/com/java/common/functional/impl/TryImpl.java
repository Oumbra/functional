package com.java.common.functional.impl;

import com.java.common.functional.Try;
import com.java.common.functional.enums.TryType;
import com.java.common.functional.factory.TryFactory;
import com.java.common.functional.lambda.FunctionThrowable;
import com.java.common.functional.lambda.SupplierThrowable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * A instance of a Try represents an attempt to compute a value. A Try is either a success, either a failure.
 * @param <E>
 */
public abstract class TryImpl<E> implements Try<E> {


    @Override
    public boolean isEmpty() {
        return TryFactory.empty().equals(this);
    }

    @Override
    public boolean isSuccess() {
        return getType() == TryType.SUCCESS;
    }

    @Override
    public boolean isFailure() {
        return !isSuccess();
    }

    @Override
    public Success<E> asSuccess() {
        return (Success<E>) this;
    }

    @Override
    public Failure<E> asFailure() {
        return (Failure<E>) this;
    }

    @Override
    public Try<E> filter(Predicate<E> predicate) {
        return isFailure() || predicate.test(asSuccess().getResult())
            ? this
            : TryFactory.empty();
    }

    @Override
    public Try<E> when(Predicate<E> predicate, FunctionThrowable<E, E> transformer) {
        return isSuccess() && predicate.test(asSuccess().getResult())?
            TryFactory.of(transformer::apply).apply(asSuccess().getResult()):
            this;
    }

    @Override
    public Try<E> flatWhen(Predicate<E> predicate, FunctionThrowable<E, Try<E>> transformer) {
        Try<Try<E>> result = isSuccess() && predicate.test(asSuccess().getResult())?
            TryFactory.of(transformer::apply).apply(asSuccess().getResult()):
            (Try<Try<E>>) this;
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
            ? result.asSuccess().getResult()
            : (Try<E>) result;
    }

    @Override
    public <O> Try<O> whenIsEmpty(SupplierThrowable<O> getter) {
        return isEmpty()? TryFactory.of(getter).get() : (Try<O>) this;
    }

    @Override
    public <O> Try<O> flatWhenIsEmpty(SupplierThrowable<Try<O>> getter) {
    	if (!isEmpty()) return (Try<O>) this;
    	Try<Try<O>> result = whenIsEmpty(getter);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    @Override
    public <O> Try<O> whenIsSuccess(FunctionThrowable<E, O> transformer) {
        return isSuccess()?
            TryFactory.of(transformer::apply).apply(asSuccess().getResult()):
            (Try<O>) this;
    }

    @Override
    public <O> Try<O> flatWhenIsSuccess(FunctionThrowable<E, Try<O>> transformer) {
        Try<Try<O>> result = whenIsSuccess(transformer);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    @Override
    public <O> Try<O> whenIsSuccessStrict(FunctionThrowable<E, O> transformer) {
        return isSuccess() && !isEmpty()? TryFactory.of(transformer::apply).apply(asSuccess().getResult()) : (Try<O>) this;
    }

    @Override
    public <O> Try<O> flatWhenIsSuccessStrict(FunctionThrowable<E, Try<O>> transformer) {
        Try<Try<O>> result = whenIsSuccessStrict(transformer);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    @Override
    public <O> Try<O> whenIsFailure(Function<Exception, O> transformer) {
    	return isFailure() ? TryFactory.of(transformer::apply).apply(asFailure().getException()) : (Try<O>) this;
    }
    
    @Override
    public <O> Try<O> flatWhenIsFailure(Function<Exception, Try<O>> transformer) {
        Try<Try<O>> result = whenIsFailure(transformer);
        // fix pour les retours Try.empty()
        return result.isSuccess() && result.asSuccess().isTryResult()
    		? result.asSuccess().getResult()
    		: (Try<O>) result;
    }

    @Override
    public Optional<E> toOption() {
        return isSuccess() ? Optional.ofNullable(asSuccess().getResult()) : Optional.empty();
    }

    @Override
    public E getOrThrow() throws Exception {
        if(isFailure()) throw asFailure().getException();
        return asSuccess().getResult();
    }

    @Override
    public Optional<E> getOptionOrThrow() throws Exception {
        return Optional.ofNullable(getOrThrow());
    }

    @Override
    public E getOrElse(E defaultValue) {
    	return isSuccess()? asSuccess().getResult(): defaultValue; 
    }
    
    @Override
    public void ifPresent(Consumer<E> consumer) {
        if(isSuccess()) consumer.accept(asSuccess().getResult());
    }
    
    @Override
    public Try<E> peekIfPresent(Consumer<E> consumer) {
        ifPresent(consumer);
        return this;
    }

    @Override
    public void ifAbsent(Consumer<Exception> consumer) {
        if (isFailure()) consumer.accept(asFailure().getException());
    }

    @Override
    public Try<E> peekIfAbsent(Consumer<Exception> consumer) {
        ifAbsent(consumer);
        return this;
    }

}