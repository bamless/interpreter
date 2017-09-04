package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;

/**
 * WIP
 * @author fabrizio
 *
 */
public class TypeChecker extends VoidVisitorAdapter<Void> {
	private SymbolTable<Type> st;
	
	public TypeChecker() {
		this.st = new SymbolTable<>();
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
		if(st.probe(v.getId().getId()) != null) {
			throw new SemanticException(String.format("Error at %s: duplicate "
					+ "local variable \"%s\"", v.getId().getPosition(), v.getId().getId()));
		}
		
		st.define(v.getId().getId(), v.getType());
	}

}
