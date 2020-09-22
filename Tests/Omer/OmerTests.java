package Tests.Omer;

import oop.ex6.parsesjava.IllegalLineException;
import oop.ex6.main.Sjavac;

import java.io.FileNotFoundException;

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