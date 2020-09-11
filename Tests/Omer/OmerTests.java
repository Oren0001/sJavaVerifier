package Tests.Omer;

import oop.ex6.main.IllegalLineException;
import oop.ex6.main.SjavacReader;
import oop.ex6.parsesjava.Variable;
import oop.ex6.parsesjava.VariableParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OmerTests {
	public static void main(String args[]) throws FileNotFoundException, IllegalLineException {
		File test= new File("src/Tests/Omer/FileTest");
		Scanner scanner=new Scanner(test);
		SjavacReader readerTest= new SjavacReader();
		readerTest.readLine(scanner," private isOmer(){ ");

	}
}