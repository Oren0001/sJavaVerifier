package Tests.Oren;

import oop.ex6.main.Sjavac;

import org.junit.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * It compares your outputs, from the given tests directory from the Supplied Material, with the school
 * output.
 * And it also prints to the output/MyOutput.txt file your outputs.
 * You can find the school outputs for the tests in the output/SchoolOutput.txt file.
 * If you find any problem with the tests you can ask me in the tests forum.
 * I also put the update sjavac_tests.txt file with all the tests (the one from the Supplied Material was
 * missing some).
 *
 * I took the school output from the sjavac_tests.txt file and complete it with the missing test from the
 * school solution. So I assumed that the sjavac_tests.txt file results are the good ones. I also add 6 of
 * my tests.
 *
 * Instructions :
 * Just add the tests folder (its the same than the one given in the Supplied Material from the Moodle) and
 * the output folder in your project directory outside of the src directory. And add the Tester.java file in
 * the src directory. (You can see how it should be in the screenshot I joined to the post).
 * And then, you just have to run it from Intellij or the text editor you use.
 *
 * If you want to add your tests :
 * Write your tests files put them in the tests folder and then write the result from the school
 * solution in the output/SchoolOutput.txt file using this format test_name : result.
 * And then the Tester will compare your program result with the school result with the old and new tests.
 * I sort for the output/MyOutput.txt file, the tests alphabetically. So make sure to do so when adding
 * your test result with the according format like I said before.
 *
 * @author andybenichou
 */
public class Tester_Andy
{
	/** The number of tests. */
	private int testsNumber;

	/**
	 * Prints to the out stream the final results, the number of tests you failed or if you passed everything.
	 *
	 * @param passedTestsNumber The number of passed tests.
	 */
	private void finalPrint(int passedTestsNumber)
	{
		if (passedTestsNumber == testsNumber)
			System.out.println("\nCongratulation!! You passed all the school tests and like they said it " +
							   "is 90% of the automatic tests they will run.");

		else
			System.out.printf("\nYou failed %d of %d of the tests.\nCheck the output in the " +
							  "output/MyOutput.txt file and compare it wit the school output in the " +
							  "output/SchoolOutput.txt file.\"", testsNumber - passedTestsNumber,
							  testsNumber);
	}

	/**
	 * Gets the tests files names list from the tests directory.
	 *
	 * @param filePath The project path.
	 * @return The tests files names list from the tests directory.
	 */
	private String[] getTestsList(String filePath)
	{
		File testDirectory = new File(filePath + "/tests");
		String[] tests = testDirectory.list();

		if (tests != null)
			Arrays.sort(tests);
		else
			tests = new String[]{};

		return tests;
	}

	/**
	 * Writes your tests output to the output/MyOutput.txt file.
	 *
	 * @param filePath The project path.
	 * @throws IOException Throws when an error occurred with one of the reading or writing file.
	 */
	private void getOutput(String filePath) throws IOException
	{
		String[] tests = getTestsList(filePath);

		new FileOutputStream(filePath + "/output/MyOutput.txt", false).close();

		File output = new File(filePath + "/output/MyOutput.txt");
		PrintStream stream = new PrintStream(output);

		System.setOut(stream);

		for(String test : tests)
		{
			if (!test.equals(".DS_Store"))
			{
				System.out.print(test + " : ");
				Sjavac.main(new String[] {filePath + "/tests/" + test});
				testsNumber++;
			}
		}
		stream.close();
	}

	/**
	 * Compare your tests output with the school output in the output/SchoolOutput.txt file..
	 *
	 * @param filePath The project path.
	 * @throws FileNotFoundException Throws when an error occurred when opening one of the file.
	 */
	private void checkOutput(String filePath) throws FileNotFoundException
	{
		File outputFile = new File(filePath + "/output/MyOutput.txt");
		File schoolOutputFile = new File(filePath + "/output/SchoolOutput.txt");

		Scanner outputScan = new Scanner(outputFile);
		Scanner schoolOutputScan = new Scanner(schoolOutputFile);

		int passedTestNumber = 0;

		while(schoolOutputScan.hasNextLine())
		{
			String output = outputScan.nextLine(), schoolOutput = schoolOutputScan.nextLine();

			String test = schoolOutput.split(" ")[0];

			if (!output.equals(schoolOutput))
				System.out.println(test + " failed.");

			else
				passedTestNumber++;
		}

		finalPrint(passedTestNumber);

		outputScan.close();
		schoolOutputScan.close();
	}

	/**
	 * The main test.
	 *
	 * @throws IOException Throws when an error occurred with one of the reading or writing file.
	 */
	@Test
	public void test() throws IOException
	{
		String filePath = new File("Ex6/Tests/Oren").getAbsolutePath();

		PrintStream out = System.out;
		getOutput(filePath);

		System.setOut(out);
		checkOutput(filePath);
	}
}
