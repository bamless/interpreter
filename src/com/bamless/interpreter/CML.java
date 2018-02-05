package com.bamless.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.natives.Length;
import com.bamless.interpreter.natives.Native;
import com.bamless.interpreter.natives.StrLen;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.SemanticAnalyzer;

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
		semantic.analyze(p);
		return p;
	}
	
	public Program compile(InputStream src) throws IOException {
		Program p = parser.parse(src);
		semantic.analyze(p);
		return p;
	}
	
	public Program compile(String src) {
		Program p = parser.parse(src);
		semantic.analyze(p);
		return p;
	}
	
	public Object run(Program program) {
		program.accept(interpreter, null);
		return interpreter.getMainReturn();
	}
	
	public void setStdOut(PrintStream out) {
		interpreter.setOut(out);
	}
	
	public static void serialize(Program p, String filepath) throws FileNotFoundException, IOException {
		try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(filepath)))) {
			os.writeObject(p);
		}
	}
	
	public static Program deSerialize(String filepath) throws FileNotFoundException, IOException {
		try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(filepath)))) {
			return (Program) is.readObject();
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
}
