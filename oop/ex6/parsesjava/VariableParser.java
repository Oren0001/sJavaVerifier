package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableParser implements ParseSjava {

	public static final int TYPE_WITHOUT_FINAL = 0;
	public static final int TYPE_AFTER_FINAL = 1;
	public static final String INT = "int";
	public static final String DOUBLE = "double";
	public static final String STRING = "string";
	public static final String CHAR = "char";
	public static final String BOOLEAN = "boolean";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	private int currentVariableNumber;
	private boolean isFinal;
	private boolean isOnlyAssignment;
	private String assignmentVariableName;
	private String lineToRead;
	private String[] splitLineArray;
	private Map<String, Variable> variablesMap;
	private ArrayList<Variable> variablesArray = new ArrayList<Variable>();

	public VariableParser(String lineToRead, Map<String, Variable> variableMap) throws IllegalLineException {
		this.lineToRead = lineToRead;
		String lineWithOutSpaces = lineToRead.replaceAll(" +", " ");
		if (lineWithOutSpaces.startsWith(" ")) {
			lineWithOutSpaces = lineWithOutSpaces.substring(1);
		}
		this.splitLineArray = lineWithOutSpaces.split(" ");
		this.variablesMap = variableMap;
	}

	@Override
	public void parse() throws IllegalLineException {
		checkIfFinal();
		checkType();
		if (!isOnlyAssignment) {
			checkName();
		}
		if (checkIfAssignment()) {
			checkValue();
			if (isOnlyAssignment) {
				checkTypeValueCompatibility(variablesMap.get(assignmentVariableName).getType(),
											variablesMap.get(assignmentVariableName).getValue());
			} else {
				checkTypeValueCompatibility(variablesArray.get(currentVariableNumber).getType(),
											variablesArray.get(currentVariableNumber).getValue());
			}
		}
		while (!isTheEnd()) {
			checkMultipleVariables();
		}
		if (!isOnlyAssignment) {
			addVariablesIntoMap(variablesMap);
		}
	}

	private void checkIfFinal() {
		Pattern pattern = Pattern.compile(" *final +");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			isFinal = true;
			lineToRead = lineToRead.substring(matcher.end());
			variablesArray.get(currentVariableNumber).setFinal(true);
		}
	}

	private void checkType() throws IllegalLineException {
		Pattern pattern1 = Pattern.compile(" *(int|String|double|char|boolean) +");
		Matcher matcher1 = pattern1.matcher(lineToRead);
		if (!matcher1.lookingAt()) {
			isOnlyAssignment = true;
			checkIfExistSuchVariable();
			return;
		}
		variablesArray.add(new Variable());
		lineToRead = lineToRead.substring(matcher1.end());
		if (isFinal) {
			variablesArray.get(currentVariableNumber).setFinal(true);
			variablesArray.get(currentVariableNumber).setType(splitLineArray[TYPE_AFTER_FINAL]);
		} else {
			variablesArray.get(currentVariableNumber).setType(splitLineArray[TYPE_WITHOUT_FINAL]);
		}
	}

	private void checkName() throws IllegalLineException {
		if (!isOnlyAssignment) {
			Pattern pattern = Pattern.compile(" *[a-zA-Z_][a-zA-Z0-9]+[\\w]*| *[a-zA-Z][\\w]*");
			Matcher matcher = pattern.matcher(lineToRead);
			if (!matcher.lookingAt()) {
				throw new IllegalLineException();
			}
			String name = lineToRead.substring(matcher.start(), matcher.end());
			for (int i = 0; i < currentVariableNumber - 1; i++) {
				if (variablesArray.get(i) == null || variablesArray.get(i).getName().equals(name)) {
					throw new IllegalLineException();
				}
			}
			variablesArray.get(currentVariableNumber).setName(name);
			lineToRead = lineToRead.substring(matcher.end());
		}
	}

	private boolean checkIfAssignment() throws IllegalLineException {
		Pattern pattern = Pattern.compile(" *= *");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			lineToRead = lineToRead.substring(matcher.end());
			return true;
		} else if (isFinal) {
			throw new IllegalLineException();
		} else {
			return false;
		}
	}

	private void checkValue() throws IllegalLineException {
		Pattern pattern = Pattern.compile("[\\S]*[^;&\\s&,]");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		String value = lineToRead.substring(matcher.start(), matcher.end());
		if (isOnlyAssignment) {
			variablesMap.get(assignmentVariableName).setValue(value);
		} else {
			variablesArray.get(currentVariableNumber).setValue(value);
		}
		lineToRead = lineToRead.substring(matcher.end());
	}

	private void checkTypeValueCompatibility(String type, String value) throws IllegalLineException {
		if (variablesMap.containsKey(value)) {
			checkIfReferenceValid(value);
			return;
		}
		switch (type) {
		case INT:
			Pattern intPattern = Pattern.compile("-?\\d+");
			Matcher intMatcher = intPattern.matcher(value);
			if (!intMatcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		case DOUBLE:
			Pattern doublePattern1 = Pattern.compile("-?(\\d*.\\d+|\\d+.\\d*)");
			Matcher doubleMatcher1 = doublePattern1.matcher(value);
			Pattern doublePattern2 = Pattern.compile("-?\\d+");
			Matcher doubleMatcher2 = doublePattern2.matcher(value);
			if (!doubleMatcher1.matches() && !doubleMatcher2.matches()) {
				throw new IllegalLineException();
			}
			break;
		case BOOLEAN:
			boolean trueOrFalse = (value.equals(TRUE) || value.equals(FALSE));
			Pattern booleanPattern1 = Pattern.compile("-?\\d+");
			Matcher booleanMatcher1 = booleanPattern1.matcher(value);
			Pattern booleanPattern2 = Pattern.compile("-?(\\d*.\\d+|\\d+.\\d*)");
			Matcher booleanMatcher2 = booleanPattern2.matcher(value);
			if (!booleanMatcher2.matches() && !booleanMatcher1.matches() && !trueOrFalse) {
				throw new IllegalLineException();
			}
			break;
		case CHAR:
			Pattern charPattern = Pattern.compile("'.'");
			Matcher charMatcher = charPattern.matcher(value);
			if (!charMatcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		case STRING:
			Pattern stringPattern = Pattern.compile("\".*\"");
			Matcher stringMatcher = stringPattern.matcher(value);
			if (!stringMatcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		}
	}

	private boolean isTheEnd() {
		Pattern pattern = Pattern.compile(" *; *");
		Matcher matcher = pattern.matcher(lineToRead);
		return matcher.lookingAt();
	}

	private void checkMultipleVariables() throws IllegalLineException {
		Pattern pattern = Pattern.compile(" *, +");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		lineToRead = lineToRead.substring(matcher.end());
		if (!isOnlyAssignment) {
			currentVariableNumber++;
			variablesArray.add(new Variable());
			if (isFinal) {
				variablesArray.get(currentVariableNumber).setType(splitLineArray[TYPE_AFTER_FINAL]);
			} else {
				variablesArray.get(currentVariableNumber).setType(splitLineArray[TYPE_WITHOUT_FINAL]);
			}
			checkName();
		} else {
			checkIfExistSuchVariable();
		}
		if (checkIfAssignment()) {
			checkValue();
			if (!isOnlyAssignment) {
				checkTypeValueCompatibility(variablesArray.get(currentVariableNumber).getType(),
											variablesArray.get(currentVariableNumber).getValue());
			} else {
				checkTypeValueCompatibility(variablesMap.get(assignmentVariableName).getType(),
											variablesMap.get(assignmentVariableName).getValue());
			}
		}
	}

	private void addVariablesIntoMap(Map<String, Variable> variableMap) throws IllegalLineException {
		for (Variable variable : variablesArray) {
			if (variable != null && !variableMap.containsKey(variable.getName())) {
				variableMap.put(variable.getName(), variable);
			} else if (isOnlyAssignment) {
				return;
			} else {
				throw new IllegalLineException();
			}
		}
	}

	private void checkIfExistSuchVariable() throws IllegalLineException {
		Pattern pattern = Pattern.compile(" *([a-zA-Z_][a-zA-Z0-9]+[\\w]*| *[a-zA-Z][\\w]*)");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			assignmentVariableName = matcher.group(1);
			if (!variablesMap.containsKey(assignmentVariableName) ||
				variablesMap.get(assignmentVariableName).isFinal()) {
				throw new IllegalLineException();
			}
			lineToRead = lineToRead.substring(matcher.end());
		} else {
			throw new IllegalLineException();
		}
	}

	private void checkIfReferenceValid(String value) throws IllegalLineException {
		String variableType;
		String valueType = variablesMap.get(value).getType();
		if (variablesMap.containsKey(value)) {
			if (!isOnlyAssignment) {
				variableType = variablesArray.get(currentVariableNumber).getType();
			} else {
				variableType = variablesMap.get(assignmentVariableName).getType();
			}
			if (variableType.equals(valueType)) {
				return;
			} else if (variableType.equals(DOUBLE)) {
				if (valueType.equals(INT) || valueType.equals(DOUBLE)) {
					return;
				}
			} else if (variableType.equals(BOOLEAN)) {
				if (valueType.equals(INT) || valueType.equals(DOUBLE) ||
					valueType.equals(BOOLEAN)) {
					return;
				}
			}
		}
		throw new IllegalLineException();
	}
}

