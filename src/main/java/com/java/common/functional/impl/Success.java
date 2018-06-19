package com.java.common.functional.impl;

import com.java.common.functional.enums.TryType;

import lombok.Getter;

/**
 * An implementation of Try representing a success.
 * @param <E>
 */
public final class Success<E> extends TryImpl<E> {

    @Getter private final E result;

    /**
     * Wrap the given value in an instance of Success.
     * @param result
     */
    public Success(E result) {
        this.result = result;
    }
    
    public boolean isTryResult() {
    	return this.result instanceof TryImpl;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TryType getType() {
        return TryType.SUCCESS;
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