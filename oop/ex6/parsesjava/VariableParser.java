package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class VariableParser extends SjavaParser {

	private static final int TYPE_WITHOUT_FINAL = 0;
	private static final int TYPE_AFTER_FINAL = 1;
	private static final String LEGAL_NAME = "[ \t]*[a-zA-Z_][a-zA-Z0-9]+[\\w]*|[ \t]*[a-zA-Z][\\w]*";
	private int currentVariableNumber;
	private boolean isFinal;
	private boolean isOnlyAssignment;
	private String assignmentVariableName;
	private String lineToRead;
	private String[] splitLineArray;
	private Map<String, Variable> variablesMap;
	private List<Variable> variablesList;

	/**
	 * @param lineToRead
	 * @param variablesMap
	 * @throws IllegalLineException
	 */
	public VariableParser(String lineToRead, Map<String, Variable> variablesMap) throws IllegalLineException {
		this.lineToRead = lineToRead;
		this.splitLineArray = splitSpaces(lineToRead);
		this.variablesMap = variablesMap;
		this.variablesList = new ArrayList<Variable>();
		this.currentVariableNumber = 0;
	}

	/**
	 * @throws IllegalLineException
	 */
	@Override
	public void parse() throws IllegalLineException {
		variablesList.add(new Variable());
		checkIfFinal();
		setType();
		if (!isOnlyAssignment) {
			setName();
		}
		if (checkIfAssignment()) {
			setValue();
			checkValue();
		}
		while (!isTheEnd()) {
			checkMultipleVariables();
		}
		if (!isOnlyAssignment) {
			addVariablesIntoMap(variablesMap);
		}
	}

	private String[] splitSpaces(String lineToSplit) {
		String lineWithOutSpaces = lineToRead.replaceAll("[ |\t]+", " ");
		Pattern spacesPattern = Pattern.compile(" |\t");
		Matcher spacesMatcher = spacesPattern.matcher(lineWithOutSpaces);
		if (spacesMatcher.lookingAt()) {
			lineWithOutSpaces = lineWithOutSpaces.substring(spacesMatcher.end());
		}
		return lineWithOutSpaces.split(" ");
	}

	private void checkIfFinal() {
		Pattern pattern = Pattern.compile("[ \t]*final[ \t]+");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			isFinal = true;
			lineToRead = lineToRead.substring(matcher.end());
			variablesList.get(currentVariableNumber).setFinal(true);
		}
	}

	private void setType() throws IllegalLineException {
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

	private void setName() throws IllegalLineException {
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

	private void setValue() throws IllegalLineException {
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
			variablesList.get(currentVariableNumber).setAssignment(true);
		}
		lineToRead = lineToRead.substring(matcher.end());
	}

	private void checkValue() throws IllegalLineException {
		String value;
		String type;
		if (isOnlyAssignment) {
			value = variablesMap.get(assignmentVariableName).getValue();
			type = getType(value);
			if (type != null) {
				if (!isTypeMatch(variablesMap.get(assignmentVariableName).getType(), type)) {
					throw new IllegalLineException();
				}
			} else {
				if (!isTypeMatch(variablesMap.get(assignmentVariableName).getType(),
								 getReference(value).getType())) {
					throw new IllegalLineException();
				}
			}
		} else {
			value = variablesList.get(currentVariableNumber).getValue();
			type = getType(value);
			if (type != null) {
				if (!isTypeMatch(variablesList.get(currentVariableNumber).getType(), type)) {
					throw new IllegalLineException();
				}
			} else {
				if (!isTypeMatch(variablesList.get(currentVariableNumber).getType(),
								 getReference(value).getType())) {
					throw new IllegalLineException();
				}
			}
		}

	}

	protected Variable getReference(String variableName) throws IllegalLineException {
		if (variablesMap.containsKey(variableName)) {
			checkIfReferenceValid(variableName);
			return variablesMap.get(variableName);
		} else {
			throw new IllegalLineException();
		}
	}

	private boolean isTheEnd() {
		Pattern pattern = Pattern.compile("[ \t]*;[ \t]*");
		Matcher matcher = pattern.matcher(lineToRead);
		return matcher.matches();
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
			setName();
		} else {
			checkIfExistSuchVariable();
		}
		if (checkIfAssignment()) {
			setValue();
			checkValue();
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
		if (variablesMap.containsKey(value) && variablesMap.get(value).isAssignment()) {
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
}

