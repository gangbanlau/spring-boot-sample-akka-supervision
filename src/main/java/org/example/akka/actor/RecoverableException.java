package org.example.akka.actor;

public class RecoverableException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecoverableException() {
		super("RetryableException");
	}
}
