package com.lostVictories.resources.exceptions;

public class InvalidRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRequestException(String string) {
		super(string);
	}

}
