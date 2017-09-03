package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;
import com.bamless.interpreter.semantic.symboltable.SymbolTable;

/**
 * WIP
 * @author fabrizio
 *
 */
public class TypeChecker extends VoidVisitorAdapter<Void> {
	private SymbolTable st;
	
	public TypeChecker(SymbolTable st) {
		this.st = st;
	}
	
	@Override
	public void visit(BlockStatement v, Void arg) {
		st.enterScope();
		for(Statement stmt : v.getStmts()) {
			stmt.accept(this, null);
		}
		st.exitScope();
	}
	
	@Override
	public void visit(VarDecl v, Void arg) {
		System.out.println(st.retrieveSymbol(v.getId().getId()).type);
	}

}
