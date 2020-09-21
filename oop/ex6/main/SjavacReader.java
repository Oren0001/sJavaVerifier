package oop.ex6.main;

import oop.ex6.parsesjava.VariableParser;
import oop.ex6.parsesjava.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads a line of sjava file and redirects it to the appropriate parser.
 */
public class SjavacReader {

	private List<Method> methodsList;
	private Map<String, Variable> globalVariablesMap;
	private VariableParser variableParser;
	private Stack<Character> bracketStack;
	private String lineToRead;

	/**
	 * Constructor.
	 */
	public SjavacReader() {
		this.methodsList = new LinkedList<Method>();
		this.globalVariablesMap = new HashMap<String, Variable>();
		this.bracketStack=new Stack<Character>();
	}

	/**
	 * This methods gets a line of sjava file and redirects it to the appropriate parser.
	 * @param scannedCode The file to read from.
	 * @param lineToRead The line to decipher.
	 * @throws IllegalLineException If the sjava file is invalid.
	 */
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

	/**
	 * @return The global variable map of the sjava file.
	 */
	public Map<String, Variable> getGlobalVariablesMap() {
		return globalVariablesMap;
	}

	/**
	 * @return the methods list of the sjava file.
	 */
	public List<Method> getMethodsList() {
		return methodsList;
	}

	/*
	 * @return true if the current line is an empty line.
	 */
	private boolean isEmptyLine() {
		return lineToRead.matches("[ \t]*");
	}

	/*
	 * @return true if the current line is a comment line.
	 */
	private boolean isCommentLine() {
		return lineToRead.matches("//.*");
	}

	/*
	 * @return true if the current line is a line of global variable.
	 */
	private boolean isGlobalVariable() {
		Pattern globalVariablePattern = Pattern.compile(".*;[ \t]*");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	/*
	 * @return true if the current line is a start of a method.
	 */
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

	/*
	 * @return true if the current line is the last line of the method.
	 */
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

	/*
	 * This method reset the bracket stack.
	 */
	private void resetStack() {
		bracketStack.clear();
		bracketStack.push('{');
	}
}


