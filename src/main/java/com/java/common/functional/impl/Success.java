package com.java.common.functional.impl;

import com.java.common.functional.Try;

import lombok.Getter;

/**
 * An implementation of Try representing a success.
 * @param <E>
 */
public final class Success<E> extends Try<E> {

    @Getter private final E result;

    /**
     * Wrap the given value in an instance of Success.
     * @param result
     */
    public Success(E result) {
        this.result = result;
    }
    
    public boolean isTryResult() {
    	return this.result instanceof Try;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Type getType() {
        return Type.SUCCESS;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Success<?> success = (Success<?>) that;

        if (result != null ? !result.equals(success.result) : success.result != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return result != null ? result.hashCode() : 0;
    }
}