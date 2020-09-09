package oop.ex6.main;

import java.util.ArrayList;

/**
 * A wrapper class which holds an array list of strings. These strings represent a method's lines.
 */
public class MethodLines {
    private ArrayList<String> methodLines;

    /**
     * Creates a new wrapper of method  lines.
     * @param methodLines An array list of a method's lines.
     */
    public MethodLines(ArrayList<String> methodLines) {
        this.methodLines = methodLines;
    }

    /**
     * @return a method's lines.
     */
    public ArrayList<String> getLines() {
        return methodLines;
    }
}
