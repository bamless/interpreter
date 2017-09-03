package com.bamless.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.ast.visitor.PrinterVisitor;
import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.semantic.SymbolTableBuilder;
import com.bamless.interpreter.semantic.TypeChecker;
import com.bamless.interpreter.semantic.symboltable.SymbolTable;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {		
		ASTParser p = new ASTParser();
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/test.lang"));
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
		
		SymbolTableBuilder stb = new SymbolTableBuilder();
		SymbolTable st = root.accept(stb, null);
		root.accept(new TypeChecker(st), null);
	}
	
}
