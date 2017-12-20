package com.bamless.interpreter;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.codegen.BytecodeGenerator;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.SemanticAnalyzer;
import com.bamless.interpreter.visitor.PrinterVisitor;


public class Main {
	private static final String BYTECODE_FILE = "codegen-test.byc";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("Commencing parsing...");
		
		ASTParser p = new ASTParser();
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/codegen-test.c+-"));
		
		System.out.println("Parsing done, commencing semantic analysis...");
		
		SemanticAnalyzer semantic = new SemanticAnalyzer();
		semantic.analyze(root);
		
		System.out.println("Semantic analysis done, printing AST:");
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
		
		System.out.println("Generating bytecode...");
		
		BytecodeGenerator b = new BytecodeGenerator();
		root.accept(b, null);
		
		System.out.println("Done, printing bytecode:");
		
		int[] bytecode = b.getBytecode();
		for(int i = 0; i < bytecode.length; i++) {
			System.out.println("(" + i + ")" + bytecode[i]);
		}
		
		String currentUsersHomeDir = System.getProperty("user.home");

		System.out.println("\nSaving bytecode to " + currentUsersHomeDir + "/" + BYTECODE_FILE);
		
		try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(currentUsersHomeDir + "/" + BYTECODE_FILE))) {
			for(int i = 0; i < bytecode.length; i++) {
				dos.writeInt(bytecode[i]);
			}
		}
	}
}
