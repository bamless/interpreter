package com.bamless.interpreter;

import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.natives.Length;
import com.bamless.interpreter.natives.Native;
import com.bamless.interpreter.natives.StrLen;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.SemanticAnalyzer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fabrizio
 */
public class CML {
	private static Map<String, Native<?>> natives = new HashMap<>();
	static {
		natives.put(Length.ID, new Length());
		natives.put(StrLen.ID, new StrLen());
	}

	private SemanticAnalyzer semantic;
	private Interpreter interpreter;
	private ASTParser parser;
	
	public CML() {
		semantic = new SemanticAnalyzer(natives);
		interpreter = new Interpreter(natives);
		parser = new ASTParser();
	}

	public Program compile(File src) throws FileNotFoundException, IOException {
		Program p = parser.parse(src);
		parser.clear();
		return analyze(p);
	}
	
	public Program compile(InputStream src) throws IOException {
		Program p = parser.parse(src);
		parser.clear();
		return analyze(p);
	}
	
	public Program compile(String src) {
		Program p = parser.parse(src);
		parser.clear();
		return analyze(p);
	}
	
	public Object run(Program program) {
		program.accept(interpreter, null);
		return interpreter.getMainReturn();
	}
	
	private Program analyze(Program p) {
		semantic.analyze(p);
		return p;
	}
	
}
