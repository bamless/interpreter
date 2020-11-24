package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.interpret.Interpreter;

public class StrLen extends Native<Integer> {
    public final static String ID = "strlen";

    public StrLen() {
        super(Type.INT, ID, Type.STRING);
    }

    @Override
    public Integer call(Interpreter i, Object... args) {
        return ((String) args[0]).length();
    }

}
