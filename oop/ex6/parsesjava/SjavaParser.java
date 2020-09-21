package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;


/**
 * This class represents a sjava parser which is able to parse different components of the sjava file.
 */
public abstract class SjavaParser {

	private static final String LEGAL_INT = "-?\\d+";
	private static final String LEGAL_DOUBLE = "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)";
	/**
	 * This string symbolizes regex which identifies a correct type.
	 */
	protected static final String LEGAL_TYPE = "[ \t]*+(?:int|double|String|boolean|char)[ \t]++";
	private static final String INT = "int";
	private static final String DOUBLE = "double";
	private static final String BOOLEAN = "boolean";
	private static final String STRING = "String";
	private static final String CHAR = "char";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	/**
	 * Parses different components of the sjava file.
	 * @throws IllegalLineException If a line of the sjava file is illegal.
	 */
	protected abstract void parse() throws IllegalLineException;

	/**
	 * This method is given the name of a variable and returns the corresponding reference to it.
	 * @param variableName The name of the variable.
	 * @return The appropriate reference to the variable name.
	 */
	protected abstract Variable getReference(String variableName);

	/**
	 * This method checks whether two types are suitable. I.e. the first can be initialized in the second.
	 * @param variableType The first variable type to check.
	 * @param type The second variable type to check.
	 * @return true if both types are suitable. I.e. the first can be initialized in the second.
	 */
	protected boolean isTypeMatch(String variableType, String type) {
		if (type.equals(INT) && (variableType.equals(INT) ||
								 variableType.equals(DOUBLE) || variableType.equals(BOOLEAN))) {
			return true;
		} else if (type.equals(DOUBLE) &&
				   (variableType.equals(DOUBLE) || variableType.equals(BOOLEAN))) {
			return true;
		} else if (type.equals(STRING) && variableType.equals(STRING)) {
			return true;
		} else if (type.equals(BOOLEAN) && variableType.equals(BOOLEAN)) {
			return true;
		} else {
			return type.equals(CHAR) && variableType.equals(CHAR);
		}
	}

	/**
	 * This method gets a variable value and returns its type.
	 * @param value The value of the variable to be tested.
	 * @return The type of value given. If the value is not valid, null will be returned.
	 */
	protected String getType(String value) {
		if (value.matches(LEGAL_INT)) {
			return INT;
		} else if (value.matches(LEGAL_DOUBLE)) {
			return DOUBLE;
		} else if (value.matches("\".*\"")) {
			return STRING;
		} else if (value.equals(TRUE) || value.equals(FALSE)) {
			return BOOLEAN;
		} else if (value.matches("'.'")) {
			return CHAR;
		} else {
			return null;
		}
	}
}
