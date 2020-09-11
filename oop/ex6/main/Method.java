package oop.ex6.main;

import oop.ex6.parsesjava.Variable;

import java.util.ArrayList;

public class Method {

	private ArrayList<String> methodLinesArray;
	private ArrayList<Variable> methodParameters = new ArrayList<>();

	public Method(ArrayList<String> methodLinesArray) {
		this.methodLinesArray = methodLinesArray;
	}

	public ArrayList<String> getLines() {
		return methodLinesArray;
	}

	public ArrayList<Variable> getParameters() {
		return methodParameters;
	}

}
