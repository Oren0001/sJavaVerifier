package oop.ex6.parsesjava;

import java.util.*;


public class GlobalVariableParser implements ParseSjava {

	private String line;

	public GlobalVariableParser(String lineToParse) {
		line = lineToParse;
	}


	@Override
	public void parse() throws IllegalGlobalVariableException {
		throw new IllegalGlobalVariableException();
	}
}

