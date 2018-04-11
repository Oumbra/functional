package com.java.common.functional.lambda;

@FunctionalInterface
public interface RunnableThrowable {

	default void run() {
		try {
			runThrows();
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw re;
		}
	}
	
	void runThrows() throws Exception;

}
