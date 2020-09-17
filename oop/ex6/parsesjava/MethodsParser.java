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
    private Map<String, Variable> globalVariables;
    private Deque<Map<String, Variable>> variablesStack = new ArrayDeque<>();
    private Map<String, Variable[]> methodsParameters = new HashMap<>();
    private ArrayList<MethodCall> methodsCalls = new ArrayList<>();
    private int lineNumber = 0;


    /**
     * Initializes a new methods parser.
     * @param methods an array list of Method classes.
     *                Each class wraps an array list of the method's lines.
     * @param globalVariables a map from a variable's name to the variable's class.
     */
    public MethodsParser(ArrayList<Method> methods, Map<String, Variable> globalVariables) {
        this.methods = methods;
        this.globalVariables = globalVariables;
        variablesStack.addFirst(globalVariables);
    }


    /**
     * Parses methods of the sjava file.
     * @throws IllegalLineException If a line of the sjava file is illegal.
     */
    @Override
    public void parse() throws IllegalLineException {
        for (Method method: methods) {
            ArrayList<String> lines = method.getLines();
            checkDefinition(lines.get(lineNumber));
            parseMethodLines(lines);
        }
        if (variablesStack.size() != 1)
            throw new IllegalLineException();
        for (MethodCall methodCall: methodsCalls) {
            if (!methodsParameters.containsKey(methodCall.name))
                throw new IllegalLineException();
            Variable[] methodVariables = methodsParameters.get(methodCall.name);
            if (methodVariables.length != methodCall.parameters.length)
                throw new IllegalLineException();
            for (int i=0; i<methodVariables.length; i++) {
                if (!methodVariables[i].getType().equals(methodCall.parameters[i]))
                    throw new IllegalLineException();
            }
        }
    }


    private void checkDefinition(String def) throws IllegalLineException {
        Pattern p1 = Pattern.compile("void [a-zA-Z]+[_0-9]*[ \t]*\\(");
        Matcher m1 = p1.matcher(def);
        if (!m1.lookingAt())
            throw new IllegalLineException();
        String[] matches = def.substring(0, m1.end() - 1).split(" ");
        String methodName = matches[1];

        Pattern p2 = Pattern.compile("\\)[ \t]*\\{[ \t]*$");
        Matcher m2 = p2.matcher(def);
        if (!m2.find())
            throw new IllegalLineException();

        HashMap<String, Variable> variables = new HashMap<>();
        variablesStack.addFirst(variables);
        String parametersString = def.substring(m1.end(), m2.start());
        Variable[] methodParameters;
        if (parametersString.equals(""))
            methodParameters = new Variable[0];
        else {
            String[] parameters = parametersString.split(",[ \t]*");
            methodParameters = new Variable[parameters.length];
            VariableParser varParser;
            for (int i = 0; i < parameters.length; i++) {
                varParser = new VariableParser(parameters[i] + ";", variables);
                varParser.parse();
                String[] parameterSplit = parameters[i].split(" ");
                String parameterName = parameterSplit[parameterSplit.length - 1];
                methodParameters[i] = variablesStack.peek().get(parameterName);
            }
        }
        methodsParameters.put(methodName, methodParameters);
    }


    private void parseMethodLines(ArrayList<String> lines) throws IllegalLineException {
        lineNumber++;
        final int numOfBlocks = variablesStack.size();
        while (lineNumber < lines.size()) {
            String line = lines.get(lineNumber);
            if (line.trim().length() == 0) {}
            else if (line.matches("[ \t]*+}[ \t]*+")) {
                variablesStack.removeFirst();
                if (variablesStack.size() != numOfBlocks - 1)
                    throw new IllegalLineException();
                else
                    return;
            }
            else if (!checkSemicolonSuffix(line) && !checkIfWhileBlock(lines, line)) {
                throw new IllegalLineException();
            }
            lineNumber++;
        }
    }


    private boolean checkSemicolonSuffix(String line) throws IllegalLineException {
        Pattern p = Pattern.compile(";[ \t]*$");
        Matcher m = p.matcher(line);
        if (m.find()) {
            String beforeSemicolon = line.substring(0, m.start());
            return checkReturn(beforeSemicolon) || checkVariableDeclaration(line) ||
                    checkMethodCall(beforeSemicolon);
        }
        return false;
    }


    private boolean checkReturn(String line) {
        Pattern p = Pattern.compile("[ \t]*return[ \t]*");
        Matcher m = p.matcher(line);
        return m.matches();
    }


    private boolean checkVariableDeclaration(String line) throws IllegalLineException  {
        Pattern p = Pattern.compile("[ \t]*(?:int|double|String|boolean|char)[ \t]+");
        Matcher m = p.matcher(line);
        if (m.find()) {
            VariableParser varParser = new VariableParser(line, variablesStack.peek());
            varParser.parse();
            return true;
        }
        return false;
    }


    private boolean checkVariableAssignment(String line) {
        Pattern p = Pattern.compile("[ \t]*(?:[a-zA-Z]|__)+\\w*[ \t]*=[ \t]*.*");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String[] lineSplit = line.split("=");
            String varName = lineSplit[0].trim();
            String varValue = lineSplit[1].trim();
            String varType = getVariableType(varValue);
            if (varType == null)
                return false;
            else {
                for (Map<String, Variable> variables: variablesStack) {
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
        else {
            for (Map<String, Variable> variables: variablesStack) {
                Variable var = variables.get(value);
                if (var == null)
                    continue;
                return var.getType();
            }
            return null;
        }
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
        Pattern p = Pattern.compile("[ \t]*[a-zA-Z]+[_0-9]*[ \t]*\\(");
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
        String[] parameters = parametersString.split(",[ \t]*");

        for (int i=0; i<parameters.length; i++) {
            String paramType = getVariableType(parameters[i]);
            if (paramType == null)
                return false;
            else {
                parameters[i] = paramType;
            }
        }

        MethodCall methodCall = new MethodCall();
        methodCall.name = methodName;
        methodCall.parameters = parameters;
        methodsCalls.add(methodCall);
        return false;
    }


    private class MethodCall {
        private String name;
        private String[] parameters;
    }


    private boolean checkIfWhileBlock(ArrayList<String> lines, String line) throws IllegalLineException{
        Pattern p1 = Pattern.compile("[ \t]*(?:if|while)[ \t]*\\(");
        Matcher m1 = p1.matcher(line);
        if (!m1.lookingAt())
            return false;
        Pattern p2 = Pattern.compile("\\)[ \t]*\\{[ \t]*$");
        Matcher m2 = p2.matcher(line);
        if (!m2.find()) {
            throw new IllegalLineException();
        }
        String conditions = line.substring(m1.end(), m2.start());
        checkCondition(conditions);
        variablesStack.addFirst(new HashMap<>());
        parseMethodLines(lines);
        return true;
    }


    private void checkCondition(String conditions) throws IllegalLineException {
        conditions = " " + conditions + " ";
        String[] matches = conditions.split("\\|\\||&&");
        for (String condition: matches) {
            String type = getVariableType(condition);
            if (!"boolean".equals(type) && !"int".equals(type) && !"double".equals(type))
                throw new IllegalLineException();
        }
    }

}
