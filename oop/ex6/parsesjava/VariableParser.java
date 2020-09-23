package oop.ex6.parsesjava;

import oop.ex6.main.Variable;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class receives a variable line, parses it and extricates from her the variables.
 */
public class VariableParser extends SjavaParser {

	private static final int TYPE_WITH_OUT_FINAL = 0;
	private static final int TYPE_AFTER_FINAL = 1;
	private static final String LEGAL_NAME = "[ \t]*+(?:[a-zA-Z_][a-zA-Z0-9]++|[a-zA-Z])[\\w]*+";
	private static final String LEGAL_TYPE = "[ \t]*+(?:int|double|String|boolean|char)[ \t]++";
	private int currentVariableNumber;
	private boolean isFinal;
	private boolean isOnlyInitialization;
	private String assignmentVariableName;
	private String lineToParse;
	private String[] splitLineArray;
	private List<Variable> variablesList; //A list of the variables in the given line.

	/**
	 * Constructor.
	 * @param lineToParse The line to parse and extricate from her the variables.
	 */
	public VariableParser(String lineToParse) {
		this.lineToParse = lineToParse;
		this.variablesList = new LinkedList<>();
	}

	/**
	 * This method parse the variables from the given line.
	 * @throws IllegalLineException If the line is invalid.
	 */
	@Override
	public void parse() throws IllegalLineException {
		restart();
		checkAndSetFinal();
		setType();
		if (!isOnlyInitialization) {
			setName();
		}
		if (!thereIsAssignment(true)) {
			if (theCurrentVariable().isFinal()) {
				throw new IllegalLineException();
			}
		} else {
			setValue();
			checkValue();
		}
		while (!isTheEnd()) {
			multipleVariables();
		}
		if (!isOnlyInitialization) {
			addVariableIntoMap();
		}
	}

	/*
	 * Restart the parser.
	 */
	private void restart() {
		removeAllRedundantSpaces();
		this.splitLineArray = lineToParse.split(" ");
		this.variablesList.add(new Variable());
	}

	/*
	 * This method removes all redundant spaces from the given line.
	 */
	private void removeAllRedundantSpaces() {
		lineToParse = lineToParse.replaceAll("[ |\t]+", " ");
		lineToParse = lineToParse.trim();
	}

	/*
	 * This method check if a variable is final, and if so, set him as final.
	 */
	private void checkAndSetFinal() {
		Pattern pattern = Pattern.compile("[ \t]*final[ \t]+");
		Matcher matcher = pattern.matcher(lineToParse);
		if (matcher.lookingAt()) {
			isFinal = true;
			lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
			variablesList.get(currentVariableNumber).setFinal(true);
		}
	}

	/*
	 * This method set the type of a variable.
	 */
	private void setType() throws IllegalLineException {
		Pattern pattern = Pattern.compile(LEGAL_TYPE);
		Matcher matcher = pattern.matcher(lineToParse);
		if (!matcher.lookingAt()) {
			isOnlyInitialization = true;
			setVariableNameForAssignment();
			//Conditions to continue if it is only initialization.
			if (theCurrentVariable() == null || theCurrentVariable().isFinal() || isTheEnd() ||
				!thereIsAssignment(false)) {
				throw new IllegalLineException();
			}
			return;
		}
		lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
		if (isFinal) {
			variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_AFTER_FINAL]);
		} else {
			variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_WITH_OUT_FINAL]);
		}
	}

	/*
	 * This method set the name of a variable.
	 */
	private void setName() throws IllegalLineException {
		Pattern pattern = Pattern.compile(LEGAL_NAME);
		Matcher matcher = pattern.matcher(lineToParse);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		//Extraction of the name from the line.
		String name = lineToParse.substring(matcher.start(), matcher.end());
		// Check if the same name has already been declared in the same line.
		for (int i = 0; i < currentVariableNumber; i++) {
			if (variablesList.get(i) == null || variablesList.get(i).getName().equals(name)) {
				throw new IllegalLineException();
			}
		}
		variablesList.get(currentVariableNumber).setName(name);
		lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
	}

	/*
	 * This method check if there is assignment.
	 */
	private boolean thereIsAssignment(boolean shortcut) {
		Pattern pattern = Pattern.compile("[ \t]*=[ \t]*");
		Matcher matcher = pattern.matcher(lineToParse);
		if (matcher.lookingAt()) {
			if (shortcut == true) {
				lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
			}
			return true;
			//A final variable must be initialized in the same line.
			//In addition there must be a declaration or initialization.
		} else {
			return false;
		}
	}

	/*
	 * This method set the value of a variable.
	 */
	private void setValue() throws IllegalLineException {
		Pattern pattern = Pattern.compile("\".*\"|[^;&\\s&,]+");
		Matcher matcher = pattern.matcher(lineToParse);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		//Extraction of the value from the line.
		String value = lineToParse.substring(matcher.start(), matcher.end());
		theCurrentVariable().setValue(value);
		theCurrentVariable().setIsAssigned(true);
		lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
	}

	/*
	 * This method check if a value is valid.
	 */
	private void checkValue() throws IllegalLineException {
		String value = theCurrentVariable().getValue();
		String contentType = getType(value);
		if (contentType != null) { //The type of the value is valid and not reference to another variable.
			if (!isTypeMatch(theCurrentVariable().getType(), contentType)) {
				throw new IllegalLineException();
			}
		} else { //Check if the value is a reference to another valid variable.
			if (!isValidVariableForAssignment(getVariable(value))) {
				throw new IllegalLineException();
			}
		}
	}

	/*
	 * This method checks if is the end of the line;
	 */
	private boolean isTheEnd() {
		return lineToParse.matches("[ \t]*;[ \t]*");
	}

	/*
	 *This method handles a situation where there are several variables in line.
	 */
	private void multipleVariables() throws IllegalLineException {
		Pattern pattern = Pattern.compile("[ \t]*,[ \t]*");
		Matcher matcher = pattern.matcher(lineToParse);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
		if (!isOnlyInitialization) {
			currentVariableNumber++;
			variablesList.add(new Variable());
			if (isFinal) {
				theCurrentVariable().setType(splitLineArray[TYPE_AFTER_FINAL]);
			} else {
				theCurrentVariable().setType(splitLineArray[TYPE_WITH_OUT_FINAL]);
			}
			setName();
			if (suchVariableAlreadyDeclared(theCurrentVariable())) {
				throw new IllegalLineException();
			}
		} else {
			setVariableNameForAssignment();
			if (!suchVariableAlreadyDeclared(theCurrentVariable()) || !thereIsAssignment(false)) {
				throw new IllegalLineException();
			}
		}
		if (thereIsAssignment(true)) {
			setValue();
			checkValue();
		} else {
			if (isFinal) { //Final must be initialized.
				throw new IllegalLineException();
			}
		}
	}

	/*
	 * This method set the variable name for the coming assignment.
	 */
	private boolean setVariableNameForAssignment() throws IllegalLineException {
		Pattern pattern = Pattern.compile(LEGAL_NAME);
		Matcher matcher = pattern.matcher(lineToParse);
		if (matcher.lookingAt()) {
			//Extraction of the name from the line.
			assignmentVariableName = lineToParse.substring(matcher.start(), matcher.end());
			lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
			return true;
		}
		throw new IllegalLineException();
	}

	/*
	 * This method checks if a variable is valid for assignment.
	 */
	private boolean isValidVariableForAssignment(Variable variableToCheck) {
		String referenceType = theCurrentVariable().getType();
		String contentType;
		if (variableToCheck != null && variableToCheck.isAssigned()) {
			contentType = variableToCheck.getType();
			if (isTypeMatch(referenceType, contentType)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Check if the same variable name has already been declared in the same scope.
	 */
	private boolean suchVariableAlreadyDeclared(Variable variableToCheck) {
		if (variablesStack.peek().containsKey(variableToCheck.getName())) {
			return true;
		}
		return false;
	}

	/*
	 * This method return the current variable.
	 */
	private Variable theCurrentVariable() {
		if (isOnlyInitialization) {
			return getVariable(assignmentVariableName);
		} else {
			return variablesList.get(currentVariableNumber);
		}
	}

	/*
	 * This method add variable into the most inner map(same scope) in condition the variable is not already
	 * contained in it.
	 */
	private void addVariableIntoMap() throws IllegalLineException {
		for (Variable variable : variablesList) {
			if (!suchVariableAlreadyDeclared(variable)) {
				variablesStack.peek().put(variable.getName(), variable);
			} else {
				throw new IllegalLineException();
			}
		}
	}
}

