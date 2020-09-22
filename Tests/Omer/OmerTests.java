package Tests.Omer;

import oop.ex6.main.IllegalLineException;
import oop.ex6.main.Sjavac;
import oop.ex6.main.SjavacReader;
import oop.ex6.parsesjava.Variable;
import oop.ex6.parsesjava.VariableParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class OmerTests {

	public static void main(String args[]) throws IllegalLineException, FileNotFoundException {
		String[] test = new String[1];
		Sjavac sjavac = new Sjavac();

		test[0] = "src/Tests/Omer/test.sjava";
		sjavac.main(test);

//		for (int i=1;i<10;i++) {
//			test[0] = "src/Tests/Omer/stam/totest0"+i;
//			sjavac.main(test);
//		}

//		test[0] = "src/Tests/Omer/stam/02.sjava";
//		sjavac.main(test);
	}
}