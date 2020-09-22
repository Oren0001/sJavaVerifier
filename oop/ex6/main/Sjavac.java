package oop.ex6.main;

import oop.ex6.parsesjava.IllegalLineException;
import oop.ex6.parsesjava.MethodsParser;
import oop.ex6.parsesjava.SjavaParser;

import java.io.*;
import java.util.Scanner;

/**
 * This class is a s-Java verifier - a tool able to verify the validity of s-Java code.
 */
public class Sjavac {

	private static final int LEGAL_CODE = 0;
	private static final int ILLEGAL_CODE = 1;
	private static final int IO_ERROR = 2;

	/**
	 * This method receives a sjava file and check its validity.
	 * @param args Array which contained the sjava file.
	 */
	public static void main(String[] args) {
		try {
			inputValidityCheck(args);
		} catch (InvalidUsageException invalidUsageException) {
			System.out.println(invalidUsageException.getMessage());
		}
		File sjavaCode = new File(args[0]);
		try (Scanner scannedCode = new Scanner(sjavaCode);) {
			SjavacReader sjavacReader = new SjavacReader();
			SjavaParser.resetVariableStack();
			while (scannedCode.hasNextLine()) {
				sjavacReader.readLine(scannedCode, scannedCode.nextLine());
			}
			MethodsParser methodsParser = new MethodsParser(sjavacReader.getMethodsList());
			methodsParser.parse();
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println(IO_ERROR);
			System.err.println("ERROR: File not found\n");
			return;
		} catch (IllegalLineException illegalLineException) {
			System.out.println(ILLEGAL_CODE);
			return;
		}
		System.out.println(LEGAL_CODE);
	}

	/*
	 * This method check if the input is valid.
	 */
	private static void inputValidityCheck(String[] args) throws InvalidUsageException {
		if (args.length != 1) {
			throw new InvalidUsageException();
		}
	}
}
