package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;
import oop.ex6.main.Method;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goes over different methods, and tests whether or not they are legal.
 */
public class MethodsParser implements ParseSjava {

    private ArrayList<Method> methods;
    private HashMap<String, Variable> globalVariables;
    private Deque<HashMap<String, Variable>> variablesStack;
    private HashMap<String, Variable[]> methodsParameters;
//    private ArrayList<MethodCall> methodsCall;


    /**
     * Initializes a new methods parser.
     * @param methods an array list of Method classes.
     *                Each class wraps an array list of the method's lines.
     * @param globalVariables a map from a variable's name to the variable's class.
     */
    public MethodsParser(ArrayList<Method> methods, HashMap<String, Variable> globalVariables) {
        this.methods = methods;
        this.globalVariables = globalVariables;
        variablesStack.push(globalVariables);
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
            parseMethodLines(lines);
        }
//        for (String methodCallName: methodsCall) {

//        }
    }


    private void checkDefinition(String def) throws IllegalLineException {
        Pattern p = Pattern.compile("void [a-zA-Z]+[_0-9]* *\\(");
        Matcher m = p.matcher(def);
        if (!m.lookingAt())
            throw new IllegalLineException();
        String[] matches = def.substring(0, m.end() - 1).split(" ");
        String methodName = matches[1];

        int parametersStart = m.end(); //the index of where the parameters start
        int parametersEnd = def.lastIndexOf(")", def.length() - 1);
        if (parametersEnd == -1)
            throw new IllegalLineException();
        String parametersString = def.substring(parametersStart, parametersEnd);
        String[] parameters = parametersString.split(", *");

        HashMap<String, Variable> variables = new HashMap<>();
        variablesStack.push(variables);
        VariableParser varParser;
        Variable[] methodParameters = new Variable[parameters.length];
        for(int i=0; i<parameters.length; i++) {
            varParser = new VariableParser(parameters[i], variables);
            varParser.parse();
            methodParameters[i] = variablesStack.getLast().get(parameters[i]);
        }
        methodsParameters.put(methodName, methodParameters);
    }


    private void parseMethodLines(ArrayList<String> lines) throws IllegalLineException {
        for (int i=1; i< lines.size(); i++) {
            String line = lines.get(i);
            if (!checkSemicolonSuffix(line))
                throw new IllegalLineException();
        }
    }


    private boolean checkSemicolonSuffix(String line) throws IllegalLineException {
        Pattern p = Pattern.compile("; *$");
        Matcher m = p.matcher(line);
        if (m.find()) {
            String beforeSemicolon = line.substring(0, m.start());
            return checkReturn(beforeSemicolon) || checkVariableDeclaration(line) ||
                    checkMethodCall(beforeSemicolon);
        }
        return false;
    }


    private boolean checkReturn(String line) {
        Pattern p = Pattern.compile(" *return *");
        Matcher m = p.matcher(line);
        return m.matches();
    }


    private boolean checkVariableDeclaration(String line) throws IllegalLineException  {
        Pattern p = Pattern.compile(" *(?:int|double|String|boolean|char) +");
        Matcher m = p.matcher(line);
        if (m.lookingAt()) {
            VariableParser varParser = new VariableParser(line, variablesStack.getLast());
            varParser.parse();
            return true;
        }
        return false;
    }


    private boolean checkVariableAssignment(String line) {
        Pattern p = Pattern.compile(" *(?:[a-zA-Z]|__)+\\w* *= *.*");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String[] lineSplit = line.split("=");
            String varName = lineSplit[0].trim();
            String varValue = lineSplit[1].trim();
            String varType = getVariableType(varValue);
            if (varType == null)
                return false;
            else {
                for (HashMap<String, Variable> variables: variablesStack) {
                    Variable var = variables.get(varName);
                    if (var == null)
                        continue;
                    if (var.isFinal())
                        return false;
                    return isTypeMatch(var, varType);
                }
            }
        }
        return false;
    }


    private String getVariableType(String value) {
        if (value.matches("-?\\d+"))
            return "int";
        else if (value.matches("-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)"))
            return "double";
        else if (value.startsWith("\"") && value.endsWith("\""))
            return "String";
        else if (value.equals("true") || value.equals("false"))
            return "boolean";
        else if (value.startsWith("'") && value.endsWith("'"))
            return "char";
        else
            return null;
    }


    private boolean isTypeMatch(Variable var, String varType) {
        if (varType.equals("int") && (var.getType().equals("int") ||
                var.getType().equals("double") || var.getType().equals("boolean")))
            return true;
        else if (varType.equals("double") && (var.getType().equals("double") ||
                var.getType().equals("boolean")))
            return true;
        else if (varType.equals("String") && var.getType().equals("String"))
            return true;
        else if (varType.equals("boolean") && var.getType().equals("boolean"))
            return true;
        else
            return varType.equals("char") && var.getType().equals("char");
    }


    private boolean checkMethodCall(String line) {
        Pattern p = Pattern.compile(" *[a-zA-Z]+[_0-9]* *\\(");
        Matcher m = p.matcher(line);
        if (!m.lookingAt())
            return false;
        String[] matches = line.substring(0, m.end() - 1).split(" ");
        String methodName = matches[0];

        int parametersStart = m.end(); //the index of where the parameters start
        int parametersEnd = line.lastIndexOf(")", line.length() - 1);
        if (parametersEnd == -1)
            return false;
        String parametersString = line.substring(parametersStart, parametersEnd);
        String[] parameters = parametersString.split(", *");

        for (int i=0; i<parameters.length; i++) {
            String paramType = getVariableType(parameters[i]);
            if (paramType == null)
                return false;
            else {
                parameters[i] = paramType;
            }
        }
        class MethodCalls {

        }
//        methodsCall
        return false;
    }


    private class MethodCalls {
        private String methodCallName;
        private String[] parameters;
    }



}
