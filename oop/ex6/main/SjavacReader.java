package oop.ex6.main;

import oop.ex6.parsesjava.VariableParser;
import oop.ex6.parsesjava.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SjavacReader {

	private static Map<String, Variable> globalVariableMap = new HashMap<String, Variable>();
	private VariableParser variableParser;
	private Stack<Character> bracketStack;
	private Method method;

	public Method readLine(Scanner scannedCode, String lineToRead) throws IllegalLineException {
		if (isEmptyLine(lineToRead)) {
		} else if (isGlobalVariable(lineToRead)) {
			variableParser = new VariableParser(lineToRead, globalVariableMap);
			variableParser.parse();
		} else if (isMethod(lineToRead)) {
			method = new Method(copyMethodIntoArray(scannedCode, lineToRead));
		} else {
			throw new IllegalLineException();
		}
		return method;
	}

	private boolean isEmptyLine(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(" *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isGlobalVariable(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(".*; *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.find());
	}

	private boolean isMethod(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(".*\\{ *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private ArrayList<String> copyMethodIntoArray(Scanner scannedCode, String lastLine) {
		resetStack();
		ArrayList<String> methodsLinesArray = new ArrayList<String>();
		methodsLinesArray.add(lastLine);
		while (scannedCode.hasNextLine()) {
			lastLine = scannedCode.nextLine();
			if (!isEndOfMethod(lastLine)) {
				methodsLinesArray.add(lastLine);
			} else {
				break;
			}
		}
		methodsLinesArray.add("}");
		return methodsLinesArray;
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
}


