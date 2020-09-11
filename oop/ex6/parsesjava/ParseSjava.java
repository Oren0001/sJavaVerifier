package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;


/**
 * Represents a sjava parser which is able to parse different components of the sjava file.
 */
@FunctionalInterface
public interface ParseSjava {

    /**
     * Parses different components of the sjava file.
     * @throws IllegalLineException If a line of the sjava file is illegal.
     */
    void parse() throws IllegalLineException;
}
