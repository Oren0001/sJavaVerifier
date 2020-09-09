package oop.ex6.parsesjava;

<<<<<<< HEAD
import MethodLines;

=======
>>>>>>> d007d5a37dd0a0aa54b68d77bfaf14a5e928a4f1
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
