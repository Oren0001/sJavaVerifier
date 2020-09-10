package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalVariableParser implements ParseSjava {

	private String lineToRead;
	private boolean isFinal;

	private String type;
	private ArrayList<String> names = new ArrayList<String>();
	private String value;

	private static Map<String, String> globalVariable = new HashMap<String, String>();

	public GlobalVariableParser(String lineToRead) {
		this.lineToRead = lineToRead;
	}

	@Override
	public void parse() throws IllegalLineException {
		String[] splitLine = lineToRead.split(" ");
		if (splitLine[0].equals("final")) {
			isFinal = true;
			type = splitLine[1];
			String nameAndAssignment = checkType(lineToRead);
			String assignment = checkName(nameAndAssignment);
			String rest = checkValue(assignment);
			checkTypeValueCompatibility();
			while (!isTheEnd(rest)) {
				rest = checkMultipleVariables(rest);
			}
		} else {
			type = splitLine[0];
			String nameAndRest = checkType(lineToRead);
			String assignmentOrRest = checkName(nameAndRest);
			while (!isTheEnd(assignmentOrRest)) {
				if (isAssignment(assignmentOrRest)) {
					String rest = checkValue(assignmentOrRest);
					checkTypeValueCompatibility();
					assignmentOrRest = checkMultipleVariables(rest);
				} else {
					assignmentOrRest = checkMultipleVariables(assignmentOrRest);
				}
			}
		}
		for (String name : names) {
			globalVariable.put(type, name);
		}
	}

	private String checkType(String lineToRead) throws IllegalLineException {
		Pattern pattern = Pattern.compile("\b(int|String|double|char|boolean)\b");
		Matcher matcher = pattern.matcher(type);
		if (!matcher.matches()) {
			throw new IllegalLineException();
		}
		pattern = Pattern.compile("(int|String|double|char|boolean)");
		matcher = pattern.matcher(lineToRead);
		matcher.find();
		return (lineToRead.substring(matcher.end()));
	}


	private String checkName(String nameAndRest) throws IllegalLineException {
		Pattern pattern = Pattern.compile("\b([a-zA-Z_][a-zA-Z0-9]+[\\w]*|[a-zA-Z][\\w]*)");
		Matcher matcher = pattern.matcher(nameAndRest);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		} else {
			names.add(nameAndRest.substring(matcher.start(), matcher.end()));
			return (nameAndRest.substring(matcher.end()));
		}
	}

	private String checkValue(String assignment) throws IllegalLineException {
		Pattern pattern = Pattern.compile(" *= * \\S*");
		Matcher matcher = pattern.matcher(assignment);
		if (!matcher.lookingAt()) {
			throw new IllegalLineException();
		} else {
			value = assignment.substring(matcher.start(), matcher.end());
		}
		return (assignment.substring(matcher.end()));
	}

	private void checkTypeValueCompatibility() throws IllegalLineException {
		Pattern pattern = Pattern.compile("");
		Matcher matcher = pattern.matcher("");
		switch (type) {
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
			if (!matcher.matches() && !trueOrFalse) {
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

	private String checkMultipleVariables(String rest) throws IllegalLineException {
		Pattern pattern = Pattern.compile(" *, +");
		Matcher matcher = pattern.matcher(rest);
		matcher.lookingAt();
		if (isFinal) {
			String nameAndAssignment = rest.substring(matcher.end());
			String assignment = checkName(nameAndAssignment);
			String newRest = checkValue(assignment);
			checkTypeValueCompatibility();
			return newRest;
		} else {
			String nameAndRest = rest.substring(matcher.end());
			String assignmentOrRest = checkName(nameAndRest);
			if (isAssignment(assignmentOrRest)) {
				String newRest = checkValue(assignmentOrRest);
				checkTypeValueCompatibility();
				return newRest;
			} else {
				return checkMultipleVariables(assignmentOrRest);
			}
		}
	}

	private boolean isAssignment(String assignmentOrRest) {
		Pattern pattern = Pattern.compile("= *");
		Matcher matcher = pattern.matcher(assignmentOrRest);
		return matcher.lookingAt();
	}

	private boolean isTheEnd(String rest) {
		Pattern pattern = Pattern.compile(" *;");
		Matcher matcher = pattern.matcher(rest);
		return matcher.lookingAt();
	}

	public Map<String, String> getGlobalVariable() {
		return globalVariable;
	}

}

