package oop.ex6.parsesjava;

/**
 * This class represent a sjava variable.
 */
public class Variable {

	private String name;
	private String type;
	private String value;
	private boolean isFinal;
	private boolean wasAssign;

	/**
	 * @return the value of this variable.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the vale of this variable.
	 * @param value value to be set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the name of this variable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this variable.
	 * @param name name to be set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type of this variable.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of this variable.
	 * @param type type to be set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return true if this variable is final.
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * Set whether this variable is a final
	 * @param status status to be set.
	 */
	public void setFinal(boolean status) {
		isFinal = status;
	}

	/**
	 * @return true if this variable was assignment.
	 */
	public boolean wasAssign() {
		return wasAssign;
	}

	/**
	 * Set whether this variable was assignment.
	 * @param status status to be set.
	 */
	public void setWasAssign(boolean status) {
		wasAssign = status;
	}
}
