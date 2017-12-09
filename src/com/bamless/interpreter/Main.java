package com.bamless.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.SemanticAnalyzer;
import com.bamless.interpreter.visitor.PrinterVisitor;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("Commencing parsing...");
		
		ASTParser p = new ASTParser();
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/test.c+-"));
		
		System.out.println("Parsing done, commencing semantic analysis...");
		
		SemanticAnalyzer semantic = new SemanticAnalyzer();
		semantic.analyze(root);
		
		System.out.println("Semantic analysis done, printing AST:");
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
		
		System.out.println("Executing program...\n");
		
		Interpreter i = new Interpreter();
		root.accept(i, null);
		
		System.out.println("\nDone executing");
	}
	
	
}
