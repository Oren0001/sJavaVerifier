package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;
import oop.ex6.main.Method;
import oop.ex6.main.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goes over different methods, and tests whether or not they are legal.
 */
public class MethodsParser implements ParseSjava {

    private ArrayList<Method> methods;
    private HashMap<String, Variable> globalVariables;
//    private Stack<>


    /**
     * Initializes a new methods parser.
     * @param methods an array list of Method classes.
     *                Each class wraps an array list of the method's lines.
     * @param globalVariables a map from a variable's name to the variable's class.
     */
    public MethodsParser(ArrayList<Method> methods, HashMap<String, Variable> globalVariables) {
        this.methods = methods;
        this.globalVariables = globalVariables;
    }


    /**
     * Parses methods of the sjava file.
     * @throws IllegalLineException If a line of the sjava file is illegal.
     */
    @Override
    public void parse() throws IllegalLineException {
        for (Method method: methods) {
            ArrayList<String> lines = method.getLines();
            checkDefinition(lines.get(0));
            for (int i=1; i< lines.size(); i++) {
                String line = lines.get(i);
                if (!checkSemicolonSuffix(line))
                    throw new IllegalLineException();
            }
        }
    }


    private void checkDefinition(String def) throws IllegalLineException {
        Pattern p1 = Pattern.compile("void [a-zA-Z]+[_0-9]* \\(");
        Matcher m1 = p1.matcher(def);
        if (!m1.lookingAt())
            throw new IllegalLineException();
        int parametersStart = m1.end(); //the index of where the parameters start
        int parametersEnd = def.lastIndexOf(")", def.length() - 1);
        String parametersString = def.substring(parametersStart, parametersEnd);
        String[] parameters = parametersString.split(", ");
        GlobalVariableParser varParser;
        for (String parameter: parameters) {
            varParser = new GlobalVariableParser(parameter);
            varParser.parse();
        }
    }


    private boolean checkSemicolonSuffix(String line) {
        Pattern p = Pattern.compile("; *$");
        Matcher m = p.matcher(line);
        if (m.find()) {
            String beforeSemicolon = line.substring(0, m.start());
            return checkReturn(beforeSemicolon) || checkVariable(beforeSemicolon);
        }
        return false;
    }


    private boolean checkReturn(String line) {
        Pattern p = Pattern.compile(" *return *");
        Matcher m = p.matcher(line);
        return m.matches();
    }


    private boolean checkVariable(String line) {
        return false;
    }

}
