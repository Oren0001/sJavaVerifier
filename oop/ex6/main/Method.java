package oop.ex6.main;

import java.util.ArrayList;
import java.util.Map;

public class Method {

	private ArrayList<String> methodLinesArray;

	//private Map<String,String> localVariableMap;

	public Method(ArrayList<String> methodLinesArray){
		this.methodLinesArray = methodLinesArray;
	}

	public ArrayList<String> getMethodLinesArray() {
		return methodLinesArray;
	}
}
