package com.bamless.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.parser.ast.ASTNode;
import com.bamless.interpreter.parser.ast.visitor.PrinterVisitor;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {		
		ASTParser p = new ASTParser();
		//trick program to show else binding. The x at the end will be 0.
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/test.lang"));
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
		
	}
	
}
