package oop.ex6.main;

import oop.ex6.parsesjava.VariableParser;
import oop.ex6.parsesjava.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SjavacReader {

	private List<Method> methodsList = new LinkedList<Method>();
	private Map<String, Variable> globalVariableMap = new HashMap<String, Variable>();
	private VariableParser variableParser;
	private Stack<Character> bracketStack;

	public void readLine(Scanner scannedCode, String lineToRead) throws IllegalLineException {
		if (isEmptyLine(lineToRead)) {
		} else if (isGlobalVariable(lineToRead)) {
			variableParser = new VariableParser(lineToRead, globalVariableMap);
			variableParser.parse();
		} else if (isMethod(lineToRead)) {
			methodsList.add(new Method(copyMethodIntoList(scannedCode, lineToRead)));
		} else {
			throw new IllegalLineException();
		}
	}

	private boolean isEmptyLine(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(" *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isGlobalVariable(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(".*; *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isMethod(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(".*\\{ *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private List<String> copyMethodIntoList(Scanner scannedCode, String lastLine) {
		resetStack();
		List<String> methodsLinesList = new ArrayList<String>();
		methodsLinesList.add(lastLine);
		while (scannedCode.hasNextLine()) {
			lastLine = scannedCode.nextLine();
			if (!isEndOfMethod(lastLine)) {
				methodsLinesList.add(lastLine);
			} else {
				break;
			}
		}
		methodsLinesList.add("}");
		return methodsLinesList;
	}

	private boolean isEndOfMethod(String lastLine) {
		char[] charArray = lastLine.toCharArray();
		for (char currentCharacter : charArray) {
			if (currentCharacter == '{') {
				bracketStack.push(currentCharacter);
			} else if (currentCharacter == '}') {
				bracketStack.pop();
			}
			if (bracketStack.empty()) {
				return true;
			}
		}
		return false;
	}

	private void resetStack() {
		bracketStack = new Stack<Character>();
		bracketStack.push('{');
	}

	public Map<String, Variable> getGlobalVariableMap() {
		return globalVariableMap;
	}

	public List<Method> getMethodsList() {
		return methodsList;
	}
}


