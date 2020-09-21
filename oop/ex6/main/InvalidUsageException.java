package oop.ex6.main;

public class InvalidUsageException extends Exception{
	private static final long SerialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "ERROR: Wrong usage. Should receive only one argument\n";
	}
}