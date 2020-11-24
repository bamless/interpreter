package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.interpret.Interpreter;

public class Charat extends Native<String> {
	public final static String ID = "charat";
	
	public Charat() {
		super(Type.STRING, ID, Type.STRING, Type.INT);
	}

	@Override
	public String call(Interpreter i, Object... args) {
		return ((String)args[0]).charAt((int)args[1]) + "";
	}

}
