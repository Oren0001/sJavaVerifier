package oop.ex6.main;

/**
 * This class represent a sjava variable.
 */
public class Variable {

	private String name;
	private String type;
	private String value;
	private boolean isFinal;
	private boolean isAssigned;

	/**
	 * A default constructor.
	 */
	public Variable() {}

	/**
	 * A copy constructor.
	 * @param other A Variable object to copy.
	 */
	public Variable(Variable other) {
		this();
		this.name = other.name;
		this.type = other.type;
		this.value = other.value;
		this.isFinal = other.isFinal;
		this.isAssigned = other.isAssigned;
	}

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
	 * @return true if this variable is initialized.
	 */
	public boolean isAssigned() {
		return isAssigned;
	}

	/**
	 * Sets if this variable was initialized or not.
	 * @param status status to be set.
	 */
	public void setIsAssigned(boolean status) {
		isAssigned = status;
	}
}
