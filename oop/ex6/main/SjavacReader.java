package oop.ex6.main;

import oop.ex6.parsesjava.GlobalVariableParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SjavacReader {

	public Method readLine(BufferedReader reader, String lineToRead) throws IllegalLineException,
																			IOException {
		Method method=null;
		if (isEmptyLine(lineToRead)) {
		} else if (isGlobalVariable(lineToRead)) {
			GlobalVariableParser globalVariableParser = new GlobalVariableParser(lineToRead);
			globalVariableParser.parse();
		} else if (isMethod(lineToRead)) {
			method = new Method(copyMethodIntoArray(reader, lineToRead));
		} else {
			throw new IllegalLineException();
		}
		return method;
	}

	//possessive??
	private boolean isEmptyLine(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile("/s*+");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isGlobalVariable(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile(";$/s*+");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private boolean isMethod(String lineToRead) {
		Pattern globalVariablePattern = Pattern.compile("\\{$/s*+");
		Matcher matcher = globalVariablePattern.matcher(lineToRead);
		return (matcher.matches());
	}

	private ArrayList<String> copyMethodIntoArray(BufferedReader reader, String lineToRead)
			throws IOException {
		ArrayList<String> methodsLinesArray = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null && !isEndOfMethod(line)) {
			methodsLinesArray.add(line);
		}
		return methodsLinesArray;
	}

	private boolean isEndOfMethod(String line) {
		return true;
	}
}


