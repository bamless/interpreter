package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;

public class StrLen extends Native<Integer> {
	public final static String ID = "strlen";
	
	public StrLen() {
		super(Type.INT, ID, Type.STRING);
	}
	
	@Override
	public Integer call(Object... args) {
		return ((String)args[0]).length();
	}
	
}
