package Tests.Omer;

import oop.ex6.main.IllegalLineException;
import oop.ex6.main.Sjavac;
import oop.ex6.main.SjavacReader;
import oop.ex6.parsesjava.Variable;
import oop.ex6.parsesjava.VariableParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OmerTests {

	public static void main(String args[]) throws IllegalLineException, FileNotFoundException {
		String[] test = new String[1];
		Sjavac sjavac = new Sjavac();

			test[0] = "src/Tests/tests/test001.sjava";
			sjavac.main(test);
		test[0] = "src/Tests/tests/test002.sjava";
		sjavac.main(test);

	}
}