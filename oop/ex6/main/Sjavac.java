package oop.ex6.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
			SjavacReader sjavacReader = SjavacReader.getInstance();
			while (scannedCode.hasNextLine()) {
				sjavacReader.readLine(scannedCode, scannedCode.nextLine());
			}
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
