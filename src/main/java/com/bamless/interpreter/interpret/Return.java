package com.bamless.interpreter.interpret;

@SuppressWarnings("serial")
public class Return extends RuntimeException {
	private static final Return instance = new Return();

	public Object val;
	
	private Return() {
	}
	
	public static Return instance() {
		return instance;
	}
	
	public static Return instance(Object val) {
		instance.val = val;
		return instance;
	}

}
