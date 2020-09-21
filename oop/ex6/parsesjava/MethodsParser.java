package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;
import oop.ex6.main.Method;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goes over different methods, and tests whether or not they are legal.
 */
public class MethodsParser extends SjavaParser {

	private List<Method> methods;
	private final Map<String, Variable> globalVariables;
	private List<MethodCall> methodsCalls = new ArrayList<>();
	private int lineNumber;
	private int returnAt;

	/* A stack of maps from a variable's name to it's class. Each map represents an independent scope. */
	private Deque<Map<String, Variable>> variablesStack = new ArrayDeque<>();

	/* A map from a method's name to an array of it's parameters */
	private Map<String, Variable[]> methodsParameters = new HashMap<>();


	/**
	 * Initializes a new methods parser.
	 * @param methods an array list of Method classes. Each class wraps an array list of the method's
	 * 		lines.
	 * @param globalVariables a map from a global variable's name to it's class.
	 */
	public MethodsParser(List<Method> methods, Map<String, Variable> globalVariables) {
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
		for (Method method : methods) {
			lineNumber = 0;
			returnAt = 0;
			List<String> lines = method.getLines();
			isSignatureValid(lines.get(lineNumber));
			parseMethodLines(lines);
			if (returnAt != lines.size() - 2)
				throw new IllegalLineException();
			if (variablesStack.size() != 1)
				throw new IllegalLineException();
		}
		isMethodCallValid();
	}


	/*
	 * Checks if a method's signature is valid or not.
	 * @param signature: The method's signature.
	 * @throws IllegalLineException if the method's signature is invalid.
	 */
	private void isSignatureValid(String signature) throws IllegalLineException {
		Pattern p1 = Pattern.compile("[ \t]*+void[ \t]*([a-zA-Z]+[_0-9]*)[ \t]*+\\(");
		Matcher m1 = p1.matcher(signature);
		if (!m1.lookingAt())
			throw new IllegalLineException();
		String methodName = m1.group(1);
		if (methodsParameters.get(methodName) != null) // makes sure there is no method overloading
			throw new IllegalLineException();

		Pattern p2 = Pattern.compile("\\)[ \t]*+\\{[ \t]*+$");
		Matcher m2 = p2.matcher(signature);
		if (!m2.find())
			throw new IllegalLineException();

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
		if (parameters.matches("[ \t]*"))
			methodParameters = new Variable[0];
		else {
			String[] parametersArray = parameters.split(",[ \t]*");
			methodParameters = new Variable[parametersArray.length];
			VariableParser varParser;
			for (int i = 0; i < parametersArray.length; i++) {
				varParser = new VariableParser(parametersArray[i] + ";", variables);
				varParser.parse();
				String[] parameterSplit = parametersArray[i].split(" ");
				String parameterName = parameterSplit[parameterSplit.length - 1];
				Variable variable = variables.get(parameterName);
				if (variable == null)
					throw new IllegalLineException();
				variable.setWasAssignment(true);
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
			} else if (!checkSemicolonSuffix(line) && !checkIfWhileBlock(lines, line))
				throw new IllegalLineException();
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
			return checkReturn(beforeSemicolon) || checkMethodCall(beforeSemicolon) ||
					checkDeclaration(beforeSemicolon) || checkAssignment(beforeSemicolon);
		}
		return false;
	}


	/*
	 * @param line: A line of the method.
	 * @return True if the line contains a valid return statement, false otherwise.
	 */
	private boolean checkReturn(String line) {
		Pattern p = Pattern.compile("[ \t]*+return[ \t]*+");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			returnAt = lineNumber;
			return true;
		}
		return false;
	}


	private boolean checkDeclaration(String line) throws IllegalLineException {
		Pattern p = Pattern.compile(LEGAL_TYPE);
		Matcher m = p.matcher(line);
		if (m.find()) {
			int assignmentIndex = line.indexOf("=", m.end());
			if (assignmentIndex != -1) {
				String value = line.substring(assignmentIndex + 1).trim();
				Variable content = getVariable(value);
				if (content != null) {
					if (!content.wasAssignment())
						throw new IllegalLineException();
					line = line.substring(0, assignmentIndex) + ";";
					VariableParser varParser = new VariableParser(line, variablesStack.peek());
					varParser.parse();
					String variableName = line.substring(m.end(), assignmentIndex).trim();
					Variable reference = variablesStack.peek().get(variableName);
					if (!isTypeMatch(reference.getType(), content.getType()))
						throw new IllegalLineException();
					reference.setValue(value);
					return true;
				}
				else if (getType(value) != null) {}
				else throw new IllegalLineException();
			}
			VariableParser varParser = new VariableParser(line + ";", variablesStack.peek());
			varParser.parse();
			return true;
		}
		return false;
	}


	private boolean checkAssignment(String line) throws IllegalLineException {
		Pattern p = Pattern.compile("[ \t]*+(?:[a-zA-Z]|__)++\\w*+[ \t]*+=[ \t]*+.*+");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			String[] lineSplit = line.split("=");
			String name = lineSplit[0].trim();
			String value = lineSplit[1].trim();
			String valueType = getType(value);
			if (valueType == null) {
				Variable content = getVariable(value);
				if (content == null || !content.wasAssignment())
					throw new IllegalLineException();
				valueType = content.getType();
			}
			Variable reference = getVariable(name);
			if (reference == null || reference.isFinal())
				throw new IllegalLineException();
			return isTypeMatch(reference.getType(), valueType);
		}
		return false;
	}


	/*
	 * Looks for the variable's name in the variables stack.
	 * @param variableName: A variable's name to look for.
	 * @return a Variable class object which matches the variable's name.
	 */
	protected Variable getVariable(String variableName) {
		for (Map<String, Variable> variables : variablesStack) {
			Variable var = variables.get(variableName);
			if (var == null)
				continue;
			return var;
		}
		return null;
	}


	/*
	 * @param line: A line of the method.
	 * @return true if the line contains a valid method call, false otherwise.
	 * @throws IllegalLineException if the line is invalid.
	 */
	private boolean checkMethodCall(String line) throws IllegalLineException {
		Pattern p1 = Pattern.compile("[ \t]*+([a-zA-Z]+[_0-9]*)[ \t]*+\\(");
		Matcher m1 = p1.matcher(line);
		if (!m1.lookingAt())
			return false;
		String methodName = m1.group(1);

		Pattern p2 = Pattern.compile("\\)[ \t]*$");
		Matcher m2 = p2.matcher(line);
		if (!m2.find())
			throw new IllegalLineException();

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
		if (parameters.equals(""))
			parametersArray = new String[0];
		else {
			parametersArray = parameters.split(",[ \t]*");
			for (int i = 0; i < parametersArray.length; i++) {
				String parameterType;
				String parameter = parametersArray[i].trim();
				Variable variable = getVariable(parameter);
				if (variable != null) {
					if (!variable.wasAssignment())
						throw new IllegalLineException();
					parameterType = variable.getType();
				} else {
					String type = getType(parameter);
					if (type == null)
						throw new IllegalLineException();
					parameterType = type;
				} parametersArray[i] = parameterType;
			}
		} return parametersArray;
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
		Pattern p1 = Pattern.compile("[ \t]*+(?:if|while)[ \t]*+\\(");
		Matcher m1 = p1.matcher(line);
		if (!m1.lookingAt())
			return false;
		Pattern p2 = Pattern.compile("\\)[ \t]*+\\{[ \t]*+$");
		Matcher m2 = p2.matcher(line);
		if (!m2.find())
			throw new IllegalLineException();

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
		String[] matches = conditions.split("\\|\\||&&");
		for (String condition : matches) {
			String trimmedCondition = condition.trim();
			Variable variable = getVariable(trimmedCondition);
			if (variable != null) {
				if (!variable.wasAssignment() || (!"boolean".equals(variable.getType()) &&
					!"int".equals(variable.getType()) && !"double".equals(variable.getType()))) {
					throw new IllegalLineException();
				}
			} else {
				String type = getType(trimmedCondition);
				if (!"boolean".equals(type) && !"int".equals(type) && !"double".equals(type)) {
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
			if (!methodsParameters.containsKey(methodCall.name))
				throw new IllegalLineException();
			Variable[] methodVariables = methodsParameters.get(methodCall.name);
			if (methodVariables.length != methodCall.parameters.length)
				throw new IllegalLineException();
			for (int i = 0; i < methodVariables.length; i++) {
				if (!methodVariables[i].getType().equals(methodCall.parameters[i])) {
					throw new IllegalLineException();
				}
			}
		}
	}

}
