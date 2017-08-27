package com.bamless.interpreter.parser.ast.visitor;

import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.statements.AssignStatement;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;

public interface GenericVisitor<T, A> {
	T visit(Visitable v, A arg);
	T visit(AssignStatement v, A arg);
	T visit(IfStatement v, A arg);
	T visit(WhileStatement v, A arg);
	T visit(BlockStatement v, A arg);
	T visit(VarDecl v, A arg);
	T visit(Identifier v, A arg);
}
