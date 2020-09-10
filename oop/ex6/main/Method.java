package oop.ex6.main;

import java.util.ArrayList;

public class Method {

	private ArrayList<String> methodLines;

	public Method(ArrayList<String> methodLines){
		this.methodLines = methodLines;
	}

	public ArrayList<String> getLines() {
		return methodLines;
	}

}
