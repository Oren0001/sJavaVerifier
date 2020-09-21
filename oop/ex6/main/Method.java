package oop.ex6.main;

import oop.ex6.parsesjava.Variable;

import java.util.*;

/**
 * This class represent a sjava method.
 */
public class Method {

	private List<String> methodLinesArray;
	private List<Variable> methodParameters = new ArrayList<>();

	/**
	 * Constructor.
	 * @param methodLinesArray An array of method's line.
	 */
	public Method(List<String> methodLinesArray) {
		this.methodLinesArray = methodLinesArray;
	}

	/**
	 * @return The method lines array (An array of method's line).
	 */
	public List<String> getLines() {
		return methodLinesArray;
	}

	/**
	 * @return
	 */
	public List<Variable> getParameters() {
		return methodParameters;
	}

}
