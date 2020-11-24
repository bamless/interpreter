package com.bamless.interpreter.interpret;

@SuppressWarnings("serial")
public class Break extends RuntimeException {
	private final static Break instance = new Break();

	private Break() {
	}

	public static Break instance() {
		return instance;
	}

}
