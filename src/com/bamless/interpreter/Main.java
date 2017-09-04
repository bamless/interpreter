package com.bamless.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.ast.visitor.PrinterVisitor;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.TypeChecker;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("Commencing parsing...\n");
		
		ASTParser p = new ASTParser();
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/test.lang"));
		
		System.out.println("\nParsing done, printing AST:\n");
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
		
		System.out.println("\nCommencing type checking phase...\n");
		
		TypeChecker tc = new TypeChecker();
		root.accept(tc, null);
		
		System.out.println("\nDone");
	}
	
}
