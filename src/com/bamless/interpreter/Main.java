package com.bamless.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.ast.visitor.PrinterVisitor;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.SemanticAnalyzer;
import com.bamless.interpreter.semantic.TypeChecker;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("Commencing parsing...");
		
		ASTParser p = new ASTParser();
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/test.ii"));
		
		System.out.println("Parsing done, printing AST:");
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
		
		System.out.println("\nCommencing semantic analysis...");
	
		SemanticAnalyzer sa = new SemanticAnalyzer();
		root.accept(sa, null);		
		
		System.out.println("Done");
		System.out.println("Commencing type checking...");
		
		TypeChecker tc = new TypeChecker();
		root.accept(tc, null);
		
		System.out.println("Done");
		
		System.out.println("Executing program...\n");
		
		Interpreter i = new Interpreter();
		root.accept(i, null);
		
		System.out.println("\nDone executing");
	}
	
}
