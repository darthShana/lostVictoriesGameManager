package com.lostVictories.resources.exceptions;

public class InvalidUserException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidUserException(String string) {
		super(string);
	}

}
