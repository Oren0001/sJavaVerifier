package oop.ex6.parsesjava;

import oop.ex6.main.Variable;

import java.util.*;


/**
 * This class represents a sjava parser which is able to parse different components of the sjava file.
 */
public abstract class SjavaParser {

	private static final String LEGAL_INT = "-?\\d+";
	private static final String LEGAL_DOUBLE = "-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)";
	private static final String INT = "int";
	private static final String DOUBLE = "double";
	private static final String BOOLEAN = "boolean";
	private static final String STRING = "String";
	private static final String CHAR = "char";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	/**
	 * A stack of maps from a variable's name to it's class. Each map represents an independent scope.
	 */
	protected static Deque<Map<String, Variable>> variablesStack = new ArrayDeque<>();

	/**
	 * This method reset the variable stack.
	 */
	public static void resetVariableStack() {
		variablesStack = new ArrayDeque<>();
		variablesStack.add(new HashMap<>());
	}

	/**
	 * Constructor which initializes the variables stack with a map (global variables map).
	 */
	public SjavaParser() {
		if (variablesStack.isEmpty()) {
			variablesStack.add(new HashMap<>());
		}
	}

	/**
	 * Parses different components of the sjava file.
	 * @throws IllegalLineException If a line of the sjava file is illegal.
	 */
	protected abstract void parse() throws IllegalLineException;

	/**
	 * This method receives name of a variable and returns it's corresponding Variable object.
	 * @param variableName The name of the variable.
	 * @return The Variable class which matches the variable name.
	 */
	protected Variable getVariable(String variableName) {
		for (Map<String, Variable> variables : variablesStack) {
			if (variables.containsKey(variableName)) {
				return variables.get(variableName);
			}
		}
		return null;
	}

	/**
	 * This method checks whether two types are suitable. I.e. the second can be initialized in the first.
	 * @param referenceType The first variable type to check.
	 * @param contentType The second variable type to check.
	 * @return true if both types are suitable. I.e. the second can be initialized in the first.
	 */
	protected boolean isTypeMatch(String referenceType, String contentType) {
		if (INT.equals(contentType) && (INT.equals(referenceType) ||
										DOUBLE.equals(referenceType) || BOOLEAN.equals(referenceType))) {
			return true;
		} else if (DOUBLE.equals(contentType) &&
				   (DOUBLE.equals(referenceType) || BOOLEAN.equals(referenceType))) {
			return true;
		} else if (STRING.equals(contentType) && STRING.equals(referenceType)) {
			return true;
		} else if (BOOLEAN.equals(contentType) && BOOLEAN.equals(referenceType)) {
			return true;
		} else {
			return CHAR.equals(contentType) && CHAR.equals(referenceType);
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


	/**
	 * @param type A string to check.
	 * @return true if the given type is boolean, false otherwise.
	 */
	protected boolean isBoolean(String type) {
		return !BOOLEAN.equals(type) && !INT.equals(type) && !DOUBLE.equals(type);
	}
}
