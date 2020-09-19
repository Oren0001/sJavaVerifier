package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableParser implements ParseSjava {

	private static final int TYPE_WITHOUT_FINAL = 0;
	private static final int TYPE_AFTER_FINAL = 1;
	private static final String INT = "int";
	private static final String DOUBLE = "double";
	private static final String STRING = "String";
	private static final String CHAR = "char";
	private static final String BOOLEAN = "boolean";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String LEGAL_NAME= "[ \t]*[a-zA-Z_][a-zA-Z0-9]+[\\w]*|[ \t]*[a-zA-Z][\\w]*";
	private static final String LEGAL_TYPE="[ \t]*(int|String|double|char|boolean) +";
	private static final String LEGAL_INT="-?\\d+";
	private static final String LEGAL_DOUBLE="-?(\\d*\\.\\d+|\\d+\\.\\d*)|"+LEGAL_INT;

	private int currentVariableNumber;
	private boolean isFinal;
	private boolean isOnlyAssignment;
	private String assignmentVariableName;
	private String lineToRead;
	private String[] splitLineArray;
	private Map<String, Variable> variablesMap;
	private List<Variable> variablesList = new ArrayList<Variable>();

	public VariableParser(String lineToRead, Map<String, Variable> variablesMap) throws IllegalLineException {
		this.lineToRead = lineToRead;
		String lineWithOutSpaces = lineToRead.replaceAll(" +", " ");
		if (lineWithOutSpaces.startsWith(" ")) {
			lineWithOutSpaces = lineWithOutSpaces.substring(1);
		}
		this.splitLineArray = lineWithOutSpaces.split(" ");
		this.variablesMap = variablesMap;
	}

	@Override
	public void parse() throws IllegalLineException {
		variablesList.add(new Variable());
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
				checkTypeValueCompatibility(variablesList.get(currentVariableNumber).getType(),
											variablesList.get(currentVariableNumber).getValue());
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
		Pattern pattern = Pattern.compile("[ \t]*final +");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			isFinal = true;
			lineToRead = lineToRead.substring(matcher.end());
			variablesList.get(currentVariableNumber).setFinal(true);
		}
	}

	private void checkType() throws IllegalLineException {
		Pattern pattern1 = Pattern.compile(LEGAL_TYPE);
		Matcher matcher1 = pattern1.matcher(lineToRead);
		if (!matcher1.lookingAt()) {
			isOnlyAssignment = true;
			checkIfExistSuchVariable();
			return;
		}
		lineToRead = lineToRead.substring(matcher1.end());
		if (isFinal) {
			variablesList.get(currentVariableNumber).setFinal(true);
			variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_AFTER_FINAL]);
		} else {
			variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_WITHOUT_FINAL]);
		}
	}

	private void checkName() throws IllegalLineException {
		if (!isOnlyAssignment) {
			Pattern pattern = Pattern.compile(LEGAL_NAME);
			Matcher matcher = pattern.matcher(lineToRead);
			if (!matcher.lookingAt()) {
				throw new IllegalLineException();
			}
			String name = lineToRead.substring(matcher.start(), matcher.end());
			for (int i = 0; i < currentVariableNumber - 1; i++) {
				if (variablesList.get(i) == null || variablesList.get(i).getName().equals(name)) {
					throw new IllegalLineException();
				}
			}
			variablesList.get(currentVariableNumber).setName(name);
			lineToRead = lineToRead.substring(matcher.end());
		}
	}

	private boolean checkIfAssignment() throws IllegalLineException {
		Pattern pattern = Pattern.compile("[ \t]*=[ \t]*");
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
		Pattern pattern = Pattern.compile("[^;&\\s&,]+");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		String value = lineToRead.substring(matcher.start(), matcher.end());
		if (isOnlyAssignment) {
			variablesMap.get(assignmentVariableName).setValue(value);
		} else {
			variablesList.get(currentVariableNumber).setValue(value);
		}
		lineToRead = lineToRead.substring(matcher.end());
	}

	private void checkTypeValueCompatibility(String type, String value) throws IllegalLineException {
		if (variablesMap.containsKey(value)) {
			checkIfReferenceValid(value);
			return;
		}
		Pattern pattern;
		Matcher matcher;
		switch (type) {
		case INT:
			pattern = Pattern.compile(LEGAL_INT);
			matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		case DOUBLE:
			pattern = Pattern.compile(LEGAL_DOUBLE);
			matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		case BOOLEAN:
			boolean trueOrFalse = (value.equals(TRUE) || value.equals(FALSE));
			pattern = Pattern.compile(LEGAL_DOUBLE);
			matcher = pattern.matcher(value);
			if (!matcher.matches() && !trueOrFalse) {
				throw new IllegalLineException();
			}
			break;
		case CHAR:
			pattern = Pattern.compile("'.'");
			matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		case STRING:
			pattern = Pattern.compile("\".*\"");
			matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
			break;
		default:
			throw new IllegalLineException();
		}
	}

	private boolean isTheEnd() {
		Pattern pattern = Pattern.compile("[ \t]*;[ \t]*");
		Matcher matcher = pattern.matcher(lineToRead);
		return matcher.lookingAt();
	}

	private void checkMultipleVariables() throws IllegalLineException {
		Pattern pattern = Pattern.compile("[ \t]*,[ \t]*");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		lineToRead = lineToRead.substring(matcher.end());
		if (!isOnlyAssignment) {
			currentVariableNumber++;
			variablesList.add(new Variable());
			if (isFinal) {
				variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_AFTER_FINAL]);
			} else {
				variablesList.get(currentVariableNumber).setType(splitLineArray[TYPE_WITHOUT_FINAL]);
			}
			checkName();
		} else {
			checkIfExistSuchVariable();
		}
		if (checkIfAssignment()) {
			checkValue();
			if (!isOnlyAssignment) {
				checkTypeValueCompatibility(variablesList.get(currentVariableNumber).getType(),
											variablesList.get(currentVariableNumber).getValue());
			} else {
				checkTypeValueCompatibility(variablesMap.get(assignmentVariableName).getType(),
											variablesMap.get(assignmentVariableName).getValue());
			}
		}
	}

	private void addVariablesIntoMap(Map<String, Variable> variableMap) throws IllegalLineException {
		for (Variable variable : variablesList) {
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
		Pattern pattern = Pattern.compile(LEGAL_NAME);
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			assignmentVariableName = lineToRead.substring(matcher.start(), matcher.end());
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
		if (variablesMap.containsKey(value) && variablesMap.get(value).getValue()!=null) {
			if (!isOnlyAssignment) {
				variableType = variablesList.get(currentVariableNumber).getType();
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

