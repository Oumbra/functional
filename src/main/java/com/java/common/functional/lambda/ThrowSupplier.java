package com.java.common.functional.lambda;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowSupplier<T> extends Supplier<T> {

	@Override
	default T get() {
		try {
			return getThrows();
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw re;
		}
	}
	
	T getThrows() throws Exception;
}
