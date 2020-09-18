package oop.ex6.main;

import oop.ex6.parsesjava.MethodsParser;
import oop.ex6.parsesjava.Variable;

import java.io.*;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
=======
import java.util.*;

>>>>>>> 8d9b9cd8d20acbdb627865e67688bfc593ebc8e4

public class Sjavac {

	private static final int LEGAL_CODE = 0;
	private static final int ILLEGAL_CODE = 1;
	private static final int IO_ERROR = 2;
	private static final int INVALID_USAGE = 2;

	public static void main(String[] args) {
		if (!inputValidity(args)) {
			return;
		}
		File sjavaCode = new File(args[0]);
		try (Scanner scannedCode = new Scanner(sjavaCode);) {
			SjavacReader sjavacReader = new SjavacReader();
			List<Method> methodsList = new ArrayList<Method>();
			while (scannedCode.hasNextLine()) {
				Method method = sjavacReader.readLine(scannedCode, scannedCode.nextLine());
				if (method != null) {
					methodsList.add(method);
				}
<<<<<<< HEAD
			}
=======
			MethodsParser methodsParser = new MethodsParser(methodsArray, sjavacReader.globalVariableMap);
			methodsParser.parse();
>>>>>>> 8d9b9cd8d20acbdb627865e67688bfc593ebc8e4
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println(IO_ERROR);
			System.err.println("ERROR: File not found\n");
			return;
		} catch (IllegalLineException illegalLineException) {
<<<<<<< HEAD
			System.out.println(ILLEGAL_CODE);
			return;
		}
		System.out.println(LEGAL_CODE);
=======
			System.out.println(1);
			return;
		}
		System.out.println(0);
>>>>>>> 8d9b9cd8d20acbdb627865e67688bfc593ebc8e4
	}

	private static boolean inputValidity(String[] args) {
		if (args.length != 1) {
			System.out.println(INVALID_USAGE);
			System.err.print("ERROR: Wrong usage. Should receive only one argument\n");
			return false;
		}
		if (!args[0].endsWith(".sjava")) {
			System.out.println(INVALID_USAGE);
			System.err.print("ERROR: Not a sjava file\n");
			return false;
		}
		return true;
	}


}
