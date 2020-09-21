package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;


/**
 * Represents a sjava parser which is able to parse different components of the sjava file.
 */
public abstract class SjavaParser {

	private static final String LEGAL_INT = "-?\\d+";
	private static final String LEGAL_DOUBLE = "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)";
	protected static final String LEGAL_TYPE = "[ \t]*+(?:int|double|String|boolean|char)[ \t]++";
	protected static final String INT = "int";
	protected static final String DOUBLE = "double";
	protected static final String BOOLEAN = "boolean";
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
	 *
	 * @param variableName
	 * @return
	 * @throws IllegalLineException
	 */
	protected abstract Variable getReference(String variableName) throws IllegalLineException;

	/**
	 *
	 * @param variableType
	 * @param type
	 * @return
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
