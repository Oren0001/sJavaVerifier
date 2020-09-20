package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;


/**
 * Represents a sjava parser which is able to parse different components of the sjava file.
 */
public abstract class ParseSjava {

    /**
     * Parses different components of the sjava file.
     * @throws IllegalLineException If a line of the sjava file is illegal.
     */
    protected abstract void parse() throws IllegalLineException;
}
