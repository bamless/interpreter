package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.memenv.CmlArr;

public class Length extends Native<Integer> {
	public final static String ID = "len";

	public Length() {
		super(Type.INT, ID, Type.arrayType(null));
	}

	@Override
	public Integer call(Interpreter i, Object... args) {
		return ((CmlArr) args[0]).getLength();
	}

}
