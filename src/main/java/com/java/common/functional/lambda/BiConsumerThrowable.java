package com.java.common.functional.lambda;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface BiConsumerThrowable<T,U> extends BiConsumer<T,U> {

	@Override
	default void accept(T t, U u) {
		try {
			acceptTrows(t,u);
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw re;
		}
	}
	
	void acceptTrows(T t, U u) throws Exception;
}