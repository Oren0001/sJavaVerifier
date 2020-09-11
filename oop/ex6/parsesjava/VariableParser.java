package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableParser implements ParseSjava {

	private Map<String, Variable> variableMap;
	private boolean isInitialization;
	private String lineToRead;
	private boolean isFinal;
	private ArrayList<Variable> variablesArray;
	private int variableNumber;
	private String[] splitLine;

	public VariableParser(String lineToRead, Map<String, Variable> variableMap) throws IllegalLineException {
		this.lineToRead = lineToRead;
		String lineWithOutSpaces = lineToRead.replaceAll(" +", " ");
		this.splitLine = lineToRead.split(" ");
		this.variablesArray = new ArrayList<Variable>();
		this.variableMap = variableMap;
	}

	@Override
	public void parse() throws IllegalLineException {
		checkIfFinal();
		checkType();
		checkName();
		if (checkIfAssignment()) {
			checkValue();
			if (isInitialization) {
				checkTypeValueCompatibility(variableMap.get(splitLine[0]).getValue());
			} else {
				checkTypeValueCompatibility(variablesArray.get(variableNumber).getValue());
			}
		}
		while (!isTheEnd()) {
			checkMultipleVariables();
		}
		if (!isInitialization) {
			addVariablesIntoMap(variableMap);
		}
	}

	private void checkIfFinal() {
		Pattern pattern = Pattern.compile(" *final +");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			isFinal = true;
			lineToRead = lineToRead.substring(matcher.end());
			variablesArray.get(variableNumber).setFinal(true); ////???
		}
	}

	private void checkType() throws IllegalLineException {
		Pattern pattern = Pattern.compile("(int|String|double|char|boolean) +");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			checkIfExistSuchVariable();
			isInitialization = true;
			return;
		}
		variablesArray.add(new Variable());
		lineToRead = lineToRead.substring(matcher.end());
		if (isFinal) {
			variablesArray.get(variableNumber).setFinal(true);
			variablesArray.get(variableNumber).setType(splitLine[1]);
		} else {
			variablesArray.get(variableNumber).setType(splitLine[0]);
		}
	}

	private void checkName() throws IllegalLineException {
		Pattern pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9]+[\\w]*|[a-zA-Z][\\w]*)");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		} else {
			String name = lineToRead.substring(matcher.start(), matcher.end());
			for (int i = 0; i < variableNumber - 1; i++) {
				if (variablesArray.get(i) != null || variablesArray.get(i).getName().equals(name)) {
					throw new IllegalLineException();
				}
			}
			variablesArray.get(variableNumber).setName(name);
			lineToRead = lineToRead.substring(matcher.end());
		}
	}

	private boolean checkIfAssignment() throws IllegalLineException {
		Pattern pattern = Pattern.compile(" *= *");
		Matcher matcher = pattern.matcher(lineToRead);
		if (matcher.lookingAt()) {
			lineToRead = lineToRead.substring(matcher.end());
			variablesArray.get(variableNumber).setInitialized(true);
			return true;
		} else if (isFinal) {
			throw new IllegalLineException();
		} else {
			return false;
		}
	}

	private void checkValue() throws IllegalLineException {
		Pattern pattern = Pattern.compile("\\S* +");
		Matcher matcher = pattern.matcher(lineToRead);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		}
		String value = lineToRead.substring(matcher.start(), matcher.end());

		if (isInitialization) {
			variableMap.get(splitLine[0]).setValue(value);
		} else {
			variablesArray.get(variableNumber).setValue(value);
		}
		lineToRead = lineToRead.substring(matcher.end());
	}

	private void checkTypeValueCompatibility(String value) throws IllegalLineException {
		Pattern pattern = Pattern.compile("");
		Matcher matcher = pattern.matcher("");
		switch (value) {
		case ("int"):
			Pattern.compile("-?\\d+");
			pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
		case "double":
			Pattern.compile("-?(\\d*.\\d+|\\d+.\\d*)");
			pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
		case "boolean":
			boolean trueOrFalse = (value.equals("true") || value.equals("false"));
			Pattern.compile("-?(\\d*.\\d+|\\d+.\\d*)");
			pattern.matcher(value);
			Pattern pattern2 = Pattern.compile("-?\\d+");
			Matcher matcher2 = pattern.matcher(value);
			if (!matcher2.matches() && !matcher.matches() && !trueOrFalse) {
				throw new IllegalLineException();
			}
		case "char":
			Pattern.compile("'.'");
			pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
		case "String":
			Pattern.compile("\".*\"");
			pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalLineException();
			}
		}
	}

	private boolean isTheEnd() {
		Pattern pattern = Pattern.compile("; *");
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
		variableNumber++;
		variablesArray.add(new Variable());
		if (isFinal) {
			variablesArray.get(variableNumber).setType(splitLine[1]);
		} else {
			variablesArray.get(variableNumber).setType(splitLine[0]);
		}
		checkName();
		if (checkIfAssignment()) {
			checkValue();
			checkTypeValueCompatibility(variablesArray.get(variableNumber).getValue());
		}
	}

	private void addVariablesIntoMap(Map<String, Variable> variableMap) throws IllegalLineException {
		for (Variable variable : variablesArray) {
			if (variable != null && !variableMap.containsKey(variable.getName())) {
				variableMap.put(variable.getName(), variable);
			} else {
				throw new IllegalLineException();
			}
		}
	}

	private void checkIfExistSuchVariable() throws IllegalLineException {
		if (!variableMap.containsKey(splitLine[0]) || variableMap.get(splitLine[0]).isFinal()) {
			throw new IllegalLineException();
		}
		Pattern pattern = Pattern.compile("( *[a-zA-Z_][a-zA-Z0-9]+[\\w]*|[a-zA-Z][\\w]*)");
		Matcher matcher = pattern.matcher(lineToRead);
		matcher.lookingAt();
		lineToRead = lineToRead.substring(matcher.end());
	}
}

