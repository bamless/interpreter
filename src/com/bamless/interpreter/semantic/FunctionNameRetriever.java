package com.bamless.interpreter.semantic;

import java.util.HashMap;

import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;

public class FunctionNameRetriever extends VisitorAdapter<HashMap<Identifier, FuncDecl>, Void> {
	
	@Override
	public HashMap<Identifier, FuncDecl> visit(Program p, Void arg) {
		HashMap<Identifier, FuncDecl> functions = new HashMap<>();
		
		for(FuncDecl f : p.getFunctions()) {
			functions.put(f.getId(), f);
		}
		
		return functions;
	}

}
