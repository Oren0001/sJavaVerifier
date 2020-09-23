package oop.ex6.parsesjava;

import oop.ex6.main.Method;
import oop.ex6.main.Variable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Goes over different methods, and tests whether or not they are legal.
 */
public class MethodsParser extends SjavaParser {

	private static final String LEGAL_METHOD_TYPE = "void";
	private static final String LEGAL_METHOD_NAME = "([a-zA-Z]+[_0-9]*)";
	private static final String LEGAL_RETURN_STATEMENT = "[ \t]*+return[ \t]*+";
	private static final String LEGAL_IF_WHILE = "(?:if|while)";
	private static final String LEGAL_CONDITIONS_SEPARATOR = "\\|\\||&&";
	private List<Method> methods;
	private List<MethodCall> methodsCalls = new ArrayList<>();
	private int lineNumber;
	private int returnAt;

	/* A map from a method's name to an array of it's parameters */
	private Map<String, Variable[]> methodsParameters = new HashMap<>();


	/**
	 * Initializes a new methods parser.
	 * @param methods an array list of Method classes. Each class wraps an array list of the method's
	 * 	lines.
	 */
	public MethodsParser(List<Method> methods) {
		this.methods = methods;
	}


	/**
	 * Parses methods of the sjava file.
	 * @throws IllegalLineException If a line of the sjava file is illegal.
	 */
	@Override
	public void parse() throws IllegalLineException {
		Map<String, Variable> globalVariables = cloneGlobalVariables(variablesStack.peek());
		for (Method method : methods) {
			lineNumber = 0;
			returnAt = 0;
			List<String> lines = method.getLines();
			isSignatureValid(lines.get(lineNumber));
			parseMethodLines(lines);
			if (returnAt != lines.size() - 2) {
				throw new IllegalLineException();
			}
			if (variablesStack.size() != 1) {
				throw new IllegalLineException();
			}
			variablesStack.removeFirst();
			variablesStack.addFirst(globalVariables);
		}
		isMethodCallValid();
	}


	/*
	Returns a deep copy of the given global variables.
	 */
	private Map<String, Variable> cloneGlobalVariables(Map<String, Variable> globalVariables) {
		Map<String, Variable> clone = new HashMap<>();
		for (Map.Entry<String, Variable> variable : globalVariables.entrySet()) {
			clone.put(variable.getKey(), new Variable(variable.getValue()));
		}
		return clone;
	}


	/*
	 * Checks if a method's signature is valid or not.
	 * @param signature: The method's signature.
	 * @throws IllegalLineException if the method's signature is invalid.
	 */
	private void isSignatureValid(String signature) throws IllegalLineException {
		Pattern p1 = Pattern.compile("[ \t]*+" + LEGAL_METHOD_TYPE + "[ \t]*+" +
									 LEGAL_METHOD_NAME + "[ \t]*+\\(");
		Matcher m1 = p1.matcher(signature);
		if (!m1.lookingAt()) {
			throw new IllegalLineException();
		}
		String methodName = m1.group(1);
		if (methodsParameters.get(methodName) != null) // makes sure there is no method overloading
		{
			throw new IllegalLineException();
		}

		Pattern p2 = Pattern.compile("\\)[ \t]*+\\{[ \t]*+$");
		Matcher m2 = p2.matcher(signature);
		if (!m2.find()) {
			throw new IllegalLineException();
		}

		String parameters = signature.substring(m1.end(), m2.start());
		Variable[] methodParameters = getMethodParameters(parameters);
		methodsParameters.put(methodName, methodParameters);
	}


	/*
	 * @param parameters: The method's parameters.
	 * @return an array list of Variable classes. Each element represents a parameter.
	 * @throws IllegalLineException if the method's parameters are invalid.
	 */
	private Variable[] getMethodParameters(String parameters) throws IllegalLineException {
		HashMap<String, Variable> variables = new HashMap<>();
		variablesStack.addFirst(variables);
		Variable[] methodParameters;
		if (parameters.matches("[ \t]*")) {
			methodParameters = new Variable[0];
		} else {
			String[] parametersArray = parameters.split(",");
			methodParameters = new Variable[parametersArray.length];
			for (int i = 0; i < parametersArray.length; i++) {
				VariableParser variableParser = new VariableParser(parametersArray[i] + ";");
				variableParser.parse();
				String[] parameterSplit = parametersArray[i].split(" ");
				String parameterName = parameterSplit[parameterSplit.length - 1];
				Variable variable = variables.get(parameterName);
				if (variable == null) {
					throw new IllegalLineException();
				}
				variable.setIsAssigned(true);
				methodParameters[i] = variable;
			}
		}
		return methodParameters;
	}


	/*
	 * @param lines: The lines of a method.
	 * @throws IllegalLineException if one of the method's lines is illegal.
	 */
	private void parseMethodLines(List<String> lines) throws IllegalLineException {
		lineNumber++;
		while (lineNumber < lines.size()) {
			String line = lines.get(lineNumber);
			if (line.matches("[ \t]*+}[ \t]*+")) {
				variablesStack.removeFirst();
				return;
			} else if (!checkSemicolonSuffix(line) && !checkIfWhileBlock(lines, line)) {
				throw new IllegalLineException();
			}
			lineNumber++;
		}
	}


	/*
	 * Parses a line with a semicolon suffix.
	 * @param line: A line of the method.
	 * @return true if the line is valid, false otherwise.
	 * @throws IllegalLineException if the line is invalid.
	 */
	private boolean checkSemicolonSuffix(String line) throws IllegalLineException {
		Pattern p = Pattern.compile(";[ \t]*$");
		Matcher m = p.matcher(line);
		if (m.find()) {
			String beforeSemicolon = line.substring(0, m.start());
			return checkReturn(beforeSemicolon) || checkMethodCall(beforeSemicolon) || checkVariable(line);
		}
		return false;
	}


	/*
	 * @param line: A line of the method.
	 * @return true if the line contains a valid variable declaration or assignment,
	 *         false if it doesn't regard a variable.
	 * @throws IllegalLineException if the line contains an invalid variable declaration or assignment.
	 */
	private boolean checkVariable(String line) throws IllegalLineException {
		VariableParser variableParser = new VariableParser(line);
		variableParser.parse();
		return true;
	}


	/*
	 * @param line: A line of the method.
	 * @return True if the line contains a valid return statement, false otherwise.
	 */
	private boolean checkReturn(String line) {
		Pattern p = Pattern.compile(LEGAL_RETURN_STATEMENT);
		Matcher m = p.matcher(line);
		if (m.matches()) {
			returnAt = lineNumber;
			return true;
		}
		return false;
	}


	/*
	 * @param line: A line of the method.
	 * @return true if the line contains a valid method call, false otherwise.
	 * @throws IllegalLineException if the line is invalid.
	 */
	private boolean checkMethodCall(String line) throws IllegalLineException {
		Pattern p1 = Pattern.compile("[ \t]*+" + LEGAL_METHOD_NAME + "[ \t]*+\\(");
		Matcher m1 = p1.matcher(line);
		if (!m1.lookingAt()) {
			return false;
		}
		String methodName = m1.group(1);

		Pattern p2 = Pattern.compile("\\)[ \t]*$");
		Matcher m2 = p2.matcher(line);
		if (!m2.find()) {
			throw new IllegalLineException();
		}

		String parameters = line.substring(m1.end(), m2.start());
		String[] parametersArray = getMethodCallParameters(parameters);
		MethodCall methodCall = new MethodCall();
		methodCall.name = methodName;
		methodCall.parameters = parametersArray;
		methodsCalls.add(methodCall);
		return true;
	}


	/*
	 * @param parameters: A string of parameters which belong to a method call. E.g. "5, 'hello', a".
	 * @return an array of valid method call parameters.
	 * @throws IllegalLineException if the parameters are invalid.
	 */
	private String[] getMethodCallParameters(String parameters) throws IllegalLineException {
		String[] parametersArray;
		if (parameters.equals("")) {
			parametersArray = new String[0];
		} else {
			parametersArray = parameters.split(",[ \t]*");
			for (int i = 0; i < parametersArray.length; i++) {
				String parameterType;
				String parameter = parametersArray[i].trim();
				Variable variable = getVariable(parameter);
				if (variable != null) {
					if (!variable.isAssigned()) {
						throw new IllegalLineException();
					}
					parameterType = variable.getType();
				} else {
					String type = getType(parameter);
					if (type == null) {
						throw new IllegalLineException();
					}
					parameterType = type;
				}
				parametersArray[i] = parameterType;
			}
		}
		return parametersArray;
	}


	/*
	 * Represents a method call which has a name and an array of parameters.
	 */
	private class MethodCall {
		private String name;
		private String[] parameters;
	}


	/*
	 * @param lines: The lines of a methods.
	 * @param line: A specific line of a method.
	 * @return true if the if\while block is legal, false if the given line doesn't start an if\while block.
	 * @throws IllegalLineException if the if\while block is invalid.
	 */
	private boolean checkIfWhileBlock(List<String> lines, String line) throws IllegalLineException {
		Pattern p1 = Pattern.compile("[ \t]*+" + LEGAL_IF_WHILE + "[ \t]*+\\(");
		Matcher m1 = p1.matcher(line);
		if (!m1.lookingAt()) {
			return false;
		}
		Pattern p2 = Pattern.compile("\\)[ \t]*+\\{[ \t]*+$");
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


	/*
	 * @param conditions of if\while block.
	 * @throws IllegalLineException if the conditions are invalid.
	 */
	private void checkCondition(String conditions) throws IllegalLineException {
		conditions = " " + conditions + " ";
		String[] matches = conditions.split(LEGAL_CONDITIONS_SEPARATOR);
		for (String condition : matches) {
			String trimmedCondition = condition.trim();
			Variable variable = getVariable(trimmedCondition);
			if (variable != null) {
				if (!variable.isAssigned() || isBoolean(variable.getType())) {
					throw new IllegalLineException();
				}
			} else {
				String type = getType(trimmedCondition);
				if (isBoolean(type)) {
					throw new IllegalLineException();
				}
			}
		}
	}


	/*
	 * Checks if methods calls match the methods of the sjava file.
	 * @throws IllegalLineException if methods calls are illegal.
	 */
	private void isMethodCallValid() throws IllegalLineException {
		for (MethodCall methodCall : methodsCalls) {
			if (!methodsParameters.containsKey(methodCall.name)) {
				throw new IllegalLineException();
			}
			Variable[] methodVariables = methodsParameters.get(methodCall.name);
			if (methodVariables.length != methodCall.parameters.length) {
				throw new IllegalLineException();
			}
			for (int i = 0; i < methodVariables.length; i++) {
				String variableType = methodVariables[i].getType();
				String assignmentType = methodCall.parameters[i];
				if (!isTypeMatch(variableType, assignmentType)) {
					throw new IllegalLineException();
				}
			}
		}
	}

}
