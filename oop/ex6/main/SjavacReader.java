package oop.ex6.main;

import oop.ex6.parsesjava.GlobalVariableParser;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SjavacReader {

	Stack<Character> bracketStack = new Stack<Character>();

	public Method readLine(Scanner scannedCode, String lineToRead) throws IllegalLineException {
		Method method = null;
		if (isEmptyLine(lineToRead)) {
		} else if (isGlobalVariable(lineToRead)) {
			GlobalVariableParser globalVariableParser = new GlobalVariableParser(lineToRead);
			globalVariableParser.parse();
		} else if (isMethod(lineToRead)) {
			method = new Method(copyMethodIntoArray(scannedCode, lineToRead));
		} else {
			throw new IllegalLineException();
		}
		return method;
	}

	//possessive??
	private boolean isEmptyLine(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(" *");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isGlobalVariable(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile("; *$");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.find());
	}

	private boolean isMethod(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile("\\{ *$");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.find());
	}

	private ArrayList<String> copyMethodIntoArray(Scanner scannedCode, String lastLine) {
		bracketStack.push('{');
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
		resetStack();
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


