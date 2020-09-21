package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	private Map<String, Variable> variablesMap;
	private List<Variable> variablesList; //A list of the variables in the line.

	/**
	 * Constructor.
	 * @param lineToParse The variable line to parse.
	 * @param variablesMap The map to insert the variables into it.
	 */
	public VariableParser(String lineToParse, Map<String, Variable> variablesMap) {
		this.lineToParse = lineToParse;
		this.splitLineArray = splitBetweenSpaces();
		this.variablesMap = variablesMap;
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
			addVariableIntoMap(variablesMap);
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
			isAlreadyDeclared();
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
			if (getVariable(value) == null ||
				!isTypeMatch(theCurrentVariable().getType(),
							 getVariable(value).getType()) || !getVariable(value).wasAssignment()) {
				throw new IllegalLineException();
			}
		}
	}


	/*
	 * This method is given the name of the variable and returns the corresponding reference to it.
	 */
	protected Variable getVariable(String variableName) {
		if (variablesMap.containsKey(variableName) && isValidReference(variableName)) {
			return variablesMap.get(variableName);
		} else {
			return null;
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
			isAlreadyDeclared();
		}
		if (thereIsAssignment()) {
			setValue();
			checkValue();
		}
	}

	/*
	 * This method checks if variable name is already declared.
	 */
	private void isAlreadyDeclared() throws IllegalLineException {
		Pattern pattern = Pattern.compile(LEGAL_NAME);
		Matcher matcher = pattern.matcher(lineToParse);
		if (matcher.lookingAt()) {
			//Extraction of the name from the line.
			assignmentVariableName = lineToParse.substring(matcher.start(), matcher.end());
			if (!variablesMap.containsKey(assignmentVariableName) ||
				variablesMap.get(assignmentVariableName).isFinal()) {
				throw new IllegalLineException();
			}
			lineToParse = lineToParse.substring(matcher.end()); //Shortcut the line.
		} else {
			throw new IllegalLineException();
		}
	}

	/*
	 * This method checks if a reference is valid.
	 */
	private boolean isValidReference(String value) {
		String variableType;
		String valueType = variablesMap.get(value).getType();
		if (variablesMap.containsKey(value) && variablesMap.get(value).wasAssignment()) {
			variableType = theCurrentVariable().getType();
			if (!isTypeMatch(variableType, valueType)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * This method add variable into the given map (if the variable is not already contained).
	 */
	private void addVariableIntoMap(Map<String, Variable> variableMap) throws IllegalLineException {
		for (Variable variable : variablesList) {
			if (variable != null && !variableMap.containsKey(variable.getName())) {
				variableMap.put(variable.getName(), variable);
			} else if (isOnlyInitialization) {
				return;
			} else {
				throw new IllegalLineException();
			}
		}
	}

	/*
	 * This method return the current and relevant variable.
	 */
	private Variable theCurrentVariable() {
		if (isOnlyInitialization) {
			return variablesMap.get(assignmentVariableName);
		} else {
			return variablesList.get(currentVariableNumber);
		}
	}
}

