package oop.ex6.main;

import oop.ex6.parsesjava.MethodsParser;
import oop.ex6.parsesjava.SjavaParser;

import java.io.*;
import java.util.Scanner;

/**
 *
 */
public class Sjavac {

	private static final int LEGAL_CODE = 0;
	private static final int ILLEGAL_CODE = 1;
	private static final int IO_ERROR = 2;

	/**
	 * @param args
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
	 *
	 */
	private static void inputValidityCheck(String[] args) throws InvalidUsageException {
		if (args.length != 1) {
			throw new InvalidUsageException();
		}
	}
}
