package oop.ex6.parsesjava;

import java.io.BufferedReader;

@FunctionalInterface
public interface ParseSjava {

    void parse(BufferedReader reader, int lineNumber);
}
