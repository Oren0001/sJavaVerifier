package oop.ex6.main;

/**
 * This class represents an invalid usage exception, e.g. Sjavac receives more than one argument.
 */
public class InvalidUsageException extends Exception {
	private static final long SerialVersionUID = 1L;

	public InvalidUsageException() {
		super("ERROR: Wrong usage. Should receive only one argument\n");
	}
}
