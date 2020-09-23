package Tests.Oren;//TODO Replace this import with your manager.

import oop.ex6.main.Sjavac;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for EX6 oop
 * @author liorkesten
 */
public class Tester_Lior {
	public static void main(String[] args) throws Exception {
		// Load school output files.
		List<String> school_output = Files.readAllLines(Paths.get("Ex6/Tests/Oren/school_output"));
		Map<String, Integer> school_output_map = new HashMap<>();
		for (int i = 1; i < school_output.size(); i += 2) {
			school_output_map.put(school_output.get(i - 1), Integer.parseInt(school_output.get(i)));
		}
		System.out.println(school_output_map);

		// Create array of files and sort it by their the length
		File[] testFiles = (new File("Ex6/Tests/Oren/TestFiles")).listFiles();
		// Fix the order - linux.
		assert testFiles != null;
		if (testFiles[33].getName().equals("CharAsValue")) {
			File temp = testFiles[32];
			testFiles[32] = testFiles[33];
			testFiles[33] = temp;
		}

		// Save the origin output and err streams.
		PrintStream org_out = System.out;
		// Init counters for tests.
		int counter_fail_of_tests = 0;
		int i = 1;

		for (File file : testFiles) {
			ByteArrayOutputStream bytes_out = new ByteArrayOutputStream();
			PrintStream p_out = new PrintStream(bytes_out, true, "UTF-8");
			System.setOut(p_out);
			// -------------------------- Analyze file ------------------------------------------
			String[] newArgs = {"Ex6/Tests/Oren/TestFiles/" + file.getName()};
			Sjavac.main(newArgs);
			// --------------- Release the out and err streams -------------------
			System.setOut(org_out);

			// --------------------- Print ------------------------------
			String output = bytes_out.toString("UTF-8");
			if (Character.getNumericValue(output.charAt(0)) != school_output_map.get(file.getName())) {
				System.err.println(file.getName() + ": Failed :(\t expected: " + school_output_map.get(file.getName()) +
								   ", Got: " + output.charAt(0));
				counter_fail_of_tests += 1;
			} else {
				System.out.println(file.getName() + ": Succeed  :)");
			}
			// Increase index of school output
			i += 2;
		}
		Thread.sleep(1000);
		System.out.println("----------------- Summary -------------------------------------------");
		if (counter_fail_of_tests != 0) {
			System.err.println("Number of files failed : " + counter_fail_of_tests + " from :" +
							   String.valueOf(testFiles.length));
		}
		System.out.println(
				"Number of files Succeed : " + String.valueOf(testFiles.length - counter_fail_of_tests) +
				" from: " + String.valueOf(testFiles.length));
	}
}


