package com.bamless.interpreter.parser.ast.visitor;

public interface Visitable {
	public abstract <T, A> T accept(Visitor<T, A> v, A arg);
}
