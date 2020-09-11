package Tests.Omer;

import oop.ex6.main.IllegalLineException;
import oop.ex6.parsesjava.Variable;
import oop.ex6.parsesjava.VariableParser;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class OmerTests {
	public static void main(String args[]) throws FileNotFoundException, IllegalLineException {
		Map<String, Variable> testMap= new HashMap<String, Variable>();

		VariableParser test= new VariableParser("  char b='g' ;", testMap);
		test.parse();
//		VariableParser test1= new VariableParser("  double c=6, a=c  ;", testMap);
//		test1.parse();
//		VariableParser test2= new VariableParser("   a=b;", testMap);
//		test2.parse();

	}
}