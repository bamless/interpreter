package com.bamless.interpreter.parser.ast.visitor;

import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;

public interface VoidVisitor<A> {
	void visit(Visitable v, A arg);
	void visit(IfStatement v, A arg);
	void visit(WhileStatement v, A arg);
	void visit(BlockStatement v, A arg);
	void visit(VarDecl v, A arg);
	void visit(Identifier v, A arg);
	
	void visit(Expression v, A arg);
}
