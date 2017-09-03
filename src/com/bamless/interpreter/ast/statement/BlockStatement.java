package com.bamless.interpreter.ast.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class BlockStatement extends Statement implements Iterable<Statement> {
	private List<Statement> stmts = new ArrayList<>();

	public BlockStatement(List<Statement> stmts, Position pos) {
		super(pos);
		this.stmts.addAll(stmts);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public Iterator<Statement> iterator() {
		return stmts.iterator();
	}
	
	public List<Statement> getStmts() {
		return Collections.unmodifiableList(stmts);
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

}
