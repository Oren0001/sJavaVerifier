package oop.ex6.parsesjava;

import java.io.BufferedReader;

@FunctionalInterface
public interface ParseSjava {

    int parse(BufferedReader reader, int lineNumber);
}
