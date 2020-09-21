package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class receives a variable line and parse it.
 */
public class VariableParser extends SjavaParser {

	private static final int TYPE_WITH_OUT_FINAL = 0;
	private static final int TYPE_AFTER_FINAL = 1;
	private static final String LEGAL_NAME = "[ \t]*[a-zA-Z_][a-zA-Z0-9]+[\\w]*|[ \t]*[a-zA-Z][\\w]*";
	private int currentVariableNumber;
	private boolean isFinal;
	private boolean isOnlyInitialization;
	private String assignmentVariableName;
	private String lineToParse;
	private String[] splitLineArray;
	private List<Variable> variablesList; //A list of the variables in the line.

	/**
	 * Constructor.
	 * @param lineToParse The variable line to parse.
	 */
	public VariableParser(String lineToParse) {
		this.lineToParse = lineToParse;
		this.splitLineArray = splitBetweenSpaces();
		this.variablesList = new ArrayList<Variable>();
	}

	/**
	 * This method parse the given line.
	 * @throws IllegalLineException If the line is invalid.
	 */
	@Override
	public void parse() throws IllegalLineException {
		variablesList.add(new Variable());
		checkIfFinal();
		setType();
		if (!isOnlyInitialization) {
			setName();
		}
		if (thereIsAssignment()) {
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
	 * This method returns an array of all the words in a line (no spaces).
	 */
	private String[] splitBetweenSpaces() {
		String lineWithOutSpaces = lineToParse.replaceAll("[ |\t]+", " ");
		lineWithOutSpaces = lineWithOutSpaces.trim();
		return lineWithOutSpaces.split(" ");
	}

	/*
	 * This method check if a variable is final, and if so, set him as final.
	 */
	private void checkIfFinal() {
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
			isValidVariableForAssignment(getVariable(assignmentVariableName));
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
		if (!isOnlyInitialization) {
			Pattern pattern = Pattern.compile(LEGAL_NAME);
			Matcher matcher = pattern.matcher(lineToParse);
			if (!matcher.lookingAt()) {
				throw new IllegalLineException();
			}
			//Extraction of the name from the line.
			String name = lineToParse.substring(matcher.start(), matcher.end());
			// Check if the same name has already been declared in the same line
			for (int i = 0; i < currentVariableNumber - 1; i++) {
				if (variablesList.get(i) == null || variablesList.get(i).getName().equals(name)) {
					throw new IllegalLineException();
				}
			}
			variablesList.get(currentVariableNumber).setName(name);
			lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
		}
	}

	/*
	 * This method check if there is assignment.
	 */
	private boolean thereIsAssignment() throws IllegalLineException {
		Pattern pattern = Pattern.compile("[ \t]*=[ \t]*");
		Matcher matcher = pattern.matcher(lineToParse);
		if (matcher.lookingAt()) {
			lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
			return true;
			//A final variable must be initialized in the same line.
			//In addition there must be a declaration or initialization.
		} else if (isFinal || isOnlyInitialization) {
			throw new IllegalLineException();
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
		theCurrentVariable().setWasAssignment(true);
		lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
	}

	/*
	 * This method check if value is valid.
	 */
	private void checkValue() throws IllegalLineException {
		String value = theCurrentVariable().getValue();
		String type = getType(value);
		if (type != null) { //The type of the value is valid and not reference to another variable.
			if (!isTypeMatch(theCurrentVariable().getType(), type)) {
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
	 * This method Continues the parsing in the case of multiple variables in one line.
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
				variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_AFTER_FINAL]);
			} else {
				variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_WITH_OUT_FINAL]);
			}
			setName();
		} else {
			setVariableNameForAssignment();
			isValidVariableForAssignment(getVariable(assignmentVariableName));
		}
		if (thereIsAssignment()) {
			setValue();
			checkValue();
		}
	}

	/*
	 * This method checks if a variable is already declared.
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
	 * This method checks if a variable is valid.
	 */
	private boolean isValidVariableForAssignment(Variable variableToCheck) throws IllegalLineException {
		String valueType = theCurrentVariable().getType();
		String variableToCheckType;
		if (variableToCheck != null && variableToCheck.wasAssignment()) {
			variableToCheckType = variableToCheck.getType();
			if (isTypeMatch(variableToCheckType, valueType)) {
				return true;
			}
		}
		throw new IllegalLineException();
	}


	/*
	 * This method add variable into the most inner map which the variable is not already contained.
	 */
	private void addVariableIntoMap() {
		for (Variable variable : variablesList) {
			if (variable != null && !getVariablesStack().peek().containsKey(variable.getName())) {
				getVariablesStack().peek().put(variable.getName(), variable);
			}
		}
	}

	/*
	 * This method return the current and relevant variable.
	 */
	private Variable theCurrentVariable() {
		if (isOnlyInitialization) {
			return getVariable(assignmentVariableName);
		} else {
			return variablesList.get(currentVariableNumber);
		}
	}
}

