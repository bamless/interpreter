package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.interpret.memenv.Array;

public class Length extends Native<Integer> {
	public final static String ID = "len";
	
	public Length() {
		super(Type.INT, ID, Type.arrayType(null));
	}
	
	@Override
	public Integer call(Object... args) {
		return ((Array)args[0]).getLength();
	}
	
}
