package com.java.common.functional.lambda;

import java.util.function.Function;

@FunctionalInterface
public interface FunctionThrowable<T,R> extends Function<T, R> {

    @Override
    default R apply(final T t){
        try {
            return applyThrows(t);
        } catch (Exception e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    R applyThrows(final T t) throws Exception;
}
