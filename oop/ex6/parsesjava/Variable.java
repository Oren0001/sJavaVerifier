package oop.ex6.parsesjava;

public class Variable {

	private String name;
	private String type;
	private String value;
	private boolean isFinal;
	private boolean isAssignment;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean status) {
		isFinal = status;
	}

	public boolean isAssignment() {
		return isAssignment;
	}

	public void setAssignment(boolean assignment) {
		isAssignment = assignment;
	}
}
