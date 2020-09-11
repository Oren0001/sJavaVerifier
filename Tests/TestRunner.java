package Tests;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRunner {

    private static final String EMPTY_STRING = " ";

    private static final String ERR_MSG_START = "Failed in test: <";

    private static final String ERR_MSG_MID = ">\nThis test's explanation is -> ";

    private static final String WRONG_TEST_MSG = "Test did NOT run since the number test was not " +
            "entered in the right format.\n Test number MUST be three digits in between 001 and 506" +
            " (including).\n Examples: 001, 011, 106";

    private static final String TEST_PREFIX = "src/Tests/tests/";

    private static final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private static final String SJAVAC_PATTERN = "(([^ ]*)\\s(\\d)\\s(.*))";

    private static final String TEST_NUMBER_PATTERN = "\\d\\d\\d";

    private static final Pattern testNumberPattern = Pattern.compile(TEST_NUMBER_PATTERN);

    private static final Pattern filePattern = Pattern.compile(SJAVAC_PATTERN);

    private static final File testResults = new File("src/Tests/sjavac_tests.txt");

    private Matcher curMatcher;

    private String line = EMPTY_STRING;

    private String msgToUser = EMPTY_STRING;

    private String[] argToPass = {EMPTY_STRING};

    private Scanner reader;


    private void createReader(){
        try{
            reader = new Scanner(testResults);

        }
        catch (FileNotFoundException e){
            System.err.println("The sjavac_tests.txt file was not found. Make sure it's in the src " +
                    "folder");
        }
    }


    private void runSingleTest(){
        outputStreamCaptor.reset();
        System.setOut(new PrintStream(outputStreamCaptor));
        curMatcher = filePattern.matcher(line);
        assertTrue(curMatcher.matches());
        msgToUser = ERR_MSG_START + curMatcher.group(2) + ERR_MSG_MID + curMatcher.group(4);
        argToPass[0] = TEST_PREFIX + curMatcher.group(2);
        oop.ex6.main.Sjavac.main(argToPass);
        assertEquals(msgToUser, curMatcher.group(3), outputStreamCaptor.toString().trim());
//        outputStreamCaptor.reset();
    }


    @Test
    public void runAllTests(){
        createReader();
        while(reader.hasNextLine()){
            line = reader.nextLine();
            if(!line.isEmpty()){
                runSingleTest();
            }
        }
    }




    @Test
    public void runSpecificTest(){
////////////////////////////////CHANGE TO RUN SPECIFIC TEST/////////////////////////////////////////
        String test = "005";  ///
////////////////////////////////////////////////////////////////////////////////////////////////////
        curMatcher = testNumberPattern.matcher(test);
        assertTrue(WRONG_TEST_MSG ,curMatcher.matches());
        int testNumber = Integer.parseInt(test);
        assertTrue(WRONG_TEST_MSG, testNumber < 507);
        if(findTest(test)){
            runSingleTest();
        }
        else{
            System.err.println("Test not found for some reason, tell me which test and I'll debug.\n " +
                    "This should not ever be reached.");
        }
    }

    private boolean findTest(String test){
        createReader();
        while(reader.hasNext()){
            line = reader.nextLine();
            if(!line.isEmpty()){
                curMatcher = filePattern.matcher(line);
                assertTrue(curMatcher.matches());
                if(curMatcher.group(2).endsWith(test + ".sjava")) return true;
            }
        }
        return false;
    }



}
