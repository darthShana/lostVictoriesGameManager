package com.lostVictories.resources.exceptions;

public class GameRequestFailureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameRequestFailureException(String message) {
		super(message);
	}

}
