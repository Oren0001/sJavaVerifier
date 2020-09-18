package oop.ex6.main;

import oop.ex6.parsesjava.Variable;

import java.util.ArrayList;
import java.util.List;

public class Method {

	private List<String> methodLinesArray;
	private List<Variable> methodParameters = new ArrayList<Variable>();

	public Method(List<String> methodLinesArray) {
		this.methodLinesArray = methodLinesArray;
	}

	public List<String> getLines() {
		return methodLinesArray;
	}

	public List<Variable> getParameters() {
		return methodParameters;
	}

}
