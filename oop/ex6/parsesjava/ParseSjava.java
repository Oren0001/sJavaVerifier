package oop.ex6.parsesjava;

import oop.ex6.main.IllegalLineException;

import java.io.BufferedReader;
@FunctionalInterface
public interface ParseSjava {

    void parse(String lineToRead) throws IllegalLineException;
}
