package com.bamless.interpreter.interpret;

@SuppressWarnings("serial")
public class Continue extends RuntimeException {
    private final static Continue instance = new Continue();

    private Continue() {
    }

    public static Continue instance() {
        return instance;
    }

}
