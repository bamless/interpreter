package com.bamless.interpreter.natives;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.interpret.Interpreter;

public class ReadIn extends Native<String> {
	public static final String ID = "read_in";
	
	public ReadIn() {
		super(Type.STRING, ID, VOID);
	}

	@Override
	public String call(Interpreter i, Object... args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(i.getIn()));
		
		try {
			String inStr = in.readLine();
			return inStr == null ? "" : inStr;
		} catch (IOException e) {
			return "";
		}
	}

}
