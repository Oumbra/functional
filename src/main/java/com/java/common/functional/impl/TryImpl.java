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
         return getType() == TryType.FAILURE;
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
     public Try<E> when(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, E> transformer) {
         return predicate.test(this)?
             TryFactory.of(transformer::apply).apply(this):
             this;
     }

     @Override
     public Try<E> flatWhen(final Predicate<Try<E>> predicate, final FunctionThrowable<Try<E>, Try<E>> transformer) {
         return internalFlatWhen(predicate, transformer);
    }

     @Override
     public <O> Try<O> mapWhen(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, O> transformer) {
         return predicate.test(this)?
             TryFactory.of(transformer::apply).apply(this):
             // fix: on evite le ClassCastException
             !this.isSuccess()? (Try<O>) this: TryFactory.empty();
     }

     @Override
     public <O> Try<O> mapFlatWhen(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, Try<O>> transformer) {
         return internalFlatWhen(predicate, transformer);
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
     public <ET extends Throwable> E getOrThrow(final Function<Exception, ET> transformer) throws ET {
         if(isFailure()) throw transformer.apply(asFailure().getException());
         return asSuccess().getResult();
     }

     @Override
     public Optional<E> getOptionOrThrow() throws Exception {
         return Optional.ofNullable(getOrThrow());
     }

     @Override
     public <ET extends Throwable> Optional<E> getOptionOrThrow(final Function<Exception, ET> transformer) throws ET {
         return Optional.ofNullable(getOrThrow(transformer));
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

     private <O> Try<O> internalFlatWhen(Predicate<Try<E>> predicate, FunctionThrowable<Try<E>, Try<O>> transformer) {
         Try<Try<O>> result = predicate.test(this)?
             TryFactory.of(transformer::apply).apply(this):
             // fix: on evite le ClassCastException
             !this.isSuccess()? (Try<Try<O>>) this: TryFactory.empty();
         // fix pour les retours Try.empty()
         return result.isSuccess() && result.asSuccess().isTryResult()
             ? result.asSuccess().getResult()
             : (Try<O>) result;
     }
 }
