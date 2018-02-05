package com.bamless.interpreter.interpret;

public class Return extends RuntimeException {
	private Object val;
	
	public Return(Object val) {
		this.val = val;
	}
	
	public Object getVal() {
		 return val;
	}

}
