package oop.ex6.main;

import java.util.ArrayList;

public class Method {

	private ArrayList<String> methodLinesArray;

	public Method(ArrayList<String> methodLinesArray) {
		this.methodLinesArray = methodLinesArray;
	}

	public ArrayList<String> getLines() {
		return methodLinesArray;
	}

}
