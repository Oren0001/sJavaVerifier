package oop.ex6.main;

import oop.ex6.parsesjava.Variable;

import java.util.*;

public class Method {

	private ArrayList<String> methodLinesArray;
	private List<Variable> methodParameters = new ArrayList<>();

	public Method(ArrayList<String> methodLinesArray) {
		this.methodLinesArray = methodLinesArray;
	}

	public ArrayList<String> getLines() {
		return methodLinesArray;
	}

	public List<Variable> getParameters() {
		return methodParameters;
	}

}
