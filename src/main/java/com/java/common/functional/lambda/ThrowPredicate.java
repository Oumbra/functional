package com.java.common.functional.lambda;

import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowPredicate<T> extends Predicate<T> {
	
	@Override
	default boolean test(T t) {
		try {
			return testThrows(t);
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw re;
		}
	}

	Boolean testThrows(T t) throws Exception;
}
