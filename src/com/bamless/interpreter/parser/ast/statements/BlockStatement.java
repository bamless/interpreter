package com.bamless.interpreter.parser.ast.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class BlockStatement extends Statement implements Iterable<Statement> {
	private List<Statement> stmts = new ArrayList<>();

	public BlockStatement(List<Statement> stmts, Position pos) {
		super(pos);
		this.stmts.addAll(stmts);
	}

	@Override
	public Iterator<Statement> iterator() {
		return stmts.iterator();
	}
	
	public List<Statement> getStmts() {
		return Collections.unmodifiableList(stmts);
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

}
