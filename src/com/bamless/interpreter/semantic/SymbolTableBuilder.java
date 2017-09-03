package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.semantic.symboltable.SymbolTable;

public class SymbolTableBuilder extends VisitorAdapter<SymbolTable, Void> {
	private SymbolTable symbolTable;
	
	public SymbolTableBuilder() {
		symbolTable = new SymbolTable();
	}
	
	@Override
	public SymbolTable visit(BlockStatement v, Void arg) {
		symbolTable.addNestedScope();
		symbolTable.enterScope();
		
		for(Statement stmt : v.getStmts()) {
			stmt.accept(this, null);
		}
		
		symbolTable.exitScope();
		
		return symbolTable;
	}
	
	@Override
	public SymbolTable visit(VarDecl v, Void arg) {
		if(symbolTable.lookupScope(v.getId().getId())) {
			throw new SemanticException(String.format("Error at %s: duplicate "
					+ "local variable \"%s\"", v.getId().getPosition(), v.getId().getId()));
		}
		symbolTable.define(v.getId().getId(), v.getType());
		
		return symbolTable;
	}
	
}
