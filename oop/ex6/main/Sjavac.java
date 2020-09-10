package oop.ex6.main;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Sjavac {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(2);
			System.err.print("ERROR: Wrong usage. Should receive only one argument\n");
			return;
		}
		if (!args[0].endsWith(".sjava")) {
			System.out.println(2);
			System.err.print("ERROR: Not a sjava file\n");
			return;
		}
		File sjavaCode = new File(args[0]);
		try {
			Scanner scannedCode = new Scanner(sjavaCode);
			SjavacReader sjavacReader = new SjavacReader();
			ArrayList<Method> methodsArray = new ArrayList<Method>();
			while (scannedCode.hasNextLine()) {
					Method method = sjavacReader.readLine(scannedCode, scannedCode.nextLine());
					if (method != null) {
						methodsArray.add(method);
					}
				}
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println(2);
			System.err.println("ERROR: File not found\n");
			return;
		} catch (IllegalLineException illegalLineException) {
			System.out.println("1");
			return;
		}
	}





}
