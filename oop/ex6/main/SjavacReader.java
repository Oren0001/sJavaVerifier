package oop.ex6.main;

import oop.ex6.parsesjava.VariableParser;
import oop.ex6.parsesjava.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SjavacReader {

	private List<Method> methodsList;
	private Map<String, Variable> globalVariablesMap;
	private VariableParser variableParser;
	private Stack<Character> bracketStack;
	private String lineToRead;

	public SjavacReader() {
		this.methodsList = new LinkedList<Method>();
		this.globalVariablesMap = new HashMap<String, Variable>();
	}

	public void readLine(Scanner scannedCode, String lineToRead) throws IllegalLineException {
		this.lineToRead = lineToRead;
		if (isEmptyLine() || isCommentLine()) {
		} else if (isGlobalVariable()) {
			variableParser = new VariableParser(lineToRead, globalVariablesMap);
			variableParser.parse();
		} else if (isMethod()) {
			methodsList.add(new Method(copyMethodIntoList(scannedCode)));
		} else {
			throw new IllegalLineException();
		}
	}

	private boolean isEmptyLine() {
		Pattern emptyLinePattern = Pattern.compile("[ \t]*");
		Matcher matcher = emptyLinePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isCommentLine() {
		Pattern commentPattern = Pattern.compile("//.*");
		Matcher matcher = commentPattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isGlobalVariable() {
		Pattern globalVariablePattern = Pattern.compile(".*;[ \t]*");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isMethod() {
		Pattern methodPattern = Pattern.compile(".*\\{[ \t]*");
		Matcher matcher = methodPattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private List<String> copyMethodIntoList(Scanner scannedCode) {
		resetStack();
		List<String> methodsLinesList = new ArrayList<String>();
		methodsLinesList.add(lineToRead);
		while (scannedCode.hasNextLine()) {
			lineToRead = scannedCode.nextLine();
			if (!isEndOfMethod()) {
				if (!isEmptyLine() && !isCommentLine()) {
					methodsLinesList.add(lineToRead);
				}
			} else {
				break;
			}
		}
		if (bracketStack.empty()) {
			methodsLinesList.add("}");
		}
		return methodsLinesList;
	}

	private boolean isEndOfMethod() {
		char[] charArray = lineToRead.toCharArray();
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

	public Map<String, Variable> getGlobalVariablesMap() {
		return globalVariablesMap;
	}

	public List<Method> getMethodsList() {
		return methodsList;
	}
}


