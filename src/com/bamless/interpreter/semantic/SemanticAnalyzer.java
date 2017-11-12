package com.bamless.interpreter.semantic;

import java.util.HashMap;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Identifier;

/**
 * Facade class that takes an AST as input ad applies all necessary semantic passes to it
 * @author fabrizio
 *
 */
public class SemanticAnalyzer {
	private FunctionNameRetriever retr;
	private ReturnChecker rc;
	
	public SemanticAnalyzer() {
		retr = new FunctionNameRetriever();
		rc = new ReturnChecker();
	}

	public void analyze(ASTNode root) {
		HashMap<Identifier, FuncDecl> funcs = root.accept(retr, null);
		
		//check for undeclared/uninitialized var
		VarDeclChecker vc = new VarDeclChecker(funcs);
		root.accept(vc, null);
		
		//check that functions with return type actually return
		root.accept(rc, null);
		
		//type checking
		TypeChecker tc = new TypeChecker(funcs);
		root.accept(tc, null);
	}
	
}
