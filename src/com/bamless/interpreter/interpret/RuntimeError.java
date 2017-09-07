package com.bamless.interpreter.interpret;

@SuppressWarnings("serial")
public class RuntimeError extends Error {
	public RuntimeError(String msg) {
		super(msg);
	}
}
