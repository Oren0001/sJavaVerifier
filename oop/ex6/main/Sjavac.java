package oop.ex6.main;

import java.io.*;

public class Sjavac {

    public static void main(String[] args) {
        try (Reader file = new FileReader(args[0])) {
            if (args.length > 1)
                throw new IndexOutOfBoundsException();
            if (!args[0].endsWith(".sjava"))
                throw new FileNotFoundException();

            BufferedReader reader = new BufferedReader(file);
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {

            }

        } catch (IndexOutOfBoundsException e) {
            System.err.print("ERROR: Wrong usage. Should receive only one argument\n");
        } catch (FileNotFoundException e) {
            System.err.print("ERROR: Not a sjava file\n");
        } catch (IOException e) {
            System.err.print("ERROR: Problem while accessing sjava file\n");
        }
    }
}
