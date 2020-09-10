package oop.ex6.main;

public class Variable {
    private String type;
    private boolean isFinal;

    public Variable(String type, boolean isFinal) {
        this.type = type;
        this.isFinal = isFinal;
    }

    public String getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }


}
