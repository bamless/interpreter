package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public interface VoidVisitor<A> {
	void visit(Visitable v, A arg);
	void visit(IfStatement v, A arg);
	void visit(WhileStatement v, A arg);
	void visit(ForStatement v, A arg);
	void visit(BlockStatement v, A arg);
	void visit(VarDecl v, A arg);
	void visit(Identifier v, A arg);
}
