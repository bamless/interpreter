package com.bamless.interpreter.ast.expression;

public enum IncrementOperator {
	INCR("++"), DECR("--");
	
	private String repr;
	
	private IncrementOperator(String repr) {
		this.repr = repr;
	}
	
	@Override
	public String toString() { return repr; }
}
