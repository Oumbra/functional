package com.java.common.functional.lambda;

import java.util.function.Consumer;

@FunctionalInterface
public interface ConsumerThrowable<T> extends Consumer<T>{

	@Override
	default void accept(T t) {
		try {
			acceptTrows(t);
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw re;
		}
	}
	
	void acceptTrows(T t) throws Exception;
}
