package com.bamless.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.parser.ast.ASTNode;
import com.bamless.interpreter.parser.ast.visitor.PrinterVisitor;

public class Main {
	private final static String LEX_FILE = "/lexical-spec.lex";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ASTParser p = new ASTParser();
		//trick program to show else binding. The x at the end will be 0.
		ASTNode root = p.parse(new File(ClassLoader.class.getResource("/test.lang").getPath()));
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
	}
	
}
