package com.bamless.interpreter.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class Program extends ASTNode {
	private List<FuncDecl> functions = new ArrayList<>();
	
	public Program(Position start, Collection<FuncDecl> functions) {
		super(start);
		this.functions.addAll(functions);
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public List<FuncDecl> getFunctions() {
		return Collections.unmodifiableList(functions);
	}
	
}
