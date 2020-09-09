package oop.ex6.parsesjava;

import oop.ex6.main.MethodLines;

import java.util.*;

/**
 *
 */
public class MethodParser {

    private ArrayList<MethodLines> methodLines;
    private HashMap<String, ?> globalVariables;


    /**
     *
     * @param methodLines
     * @param globalVariables
     */
    public MethodParser(ArrayList<MethodLines> methodLines, HashMap<String, ?> globalVariables) {
        this.methodLines = methodLines;
        this.globalVariables = globalVariables;
    }

}
