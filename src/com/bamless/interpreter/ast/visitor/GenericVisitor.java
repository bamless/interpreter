package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public interface GenericVisitor<T, A> {
	T visit(Visitable v, A arg);
	T visit(IfStatement v, A arg);
	T visit(WhileStatement v, A arg);
	T visit(BlockStatement v, A arg);
	T visit(ForStatement v, A arg);
	T visit(VarDecl v, A arg);
	T visit(Identifier v, A arg);
}
