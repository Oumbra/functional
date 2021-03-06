package com.java.common.functional.impl;

import com.java.common.functional.enums.TryType;

import lombok.Getter;

/**
 * An implementation of Try representing a failure.
 * @param <E>
 */
public final class Failure<E> extends TryImpl<E> {

    @Getter private final Exception exception;

    /**
     * Wrap the given exception in an instance of Failure.
     * @param exception
     */
    public Failure(Exception exception) {
        assert exception != null;
        this.exception = exception;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TryType getType() {
        return TryType.FAILURE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Failure<?> failure = (Failure<?>) o;

        if (!exception.equals(failure.exception)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return exception.hashCode();
    }
}