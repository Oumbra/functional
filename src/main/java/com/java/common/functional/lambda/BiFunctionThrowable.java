package com.java.common.functional.lambda;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiFunctionThrowable<T,U,R> extends BiFunction<T, U, R> {

	@Override
	default R apply(T t, U u) {
		try {
			return applyThrows(t, u);
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw re;
		}
	}
	
    R applyThrows(T t, U u) throws Exception;
}
