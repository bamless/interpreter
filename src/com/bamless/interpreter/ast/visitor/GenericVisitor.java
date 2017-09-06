package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.RelationalExpression;
import com.bamless.interpreter.ast.expression.StringLiteral;
import com.bamless.interpreter.ast.expression.VarLiteral;
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
	T visit(ArithmeticBinExpression e, A arg);
	T visit(LogicalExpression l, A arg);
	T visit(EqualityExpression e, A arg);
	T visit(RelationalExpression r, A arg);
	T visit(LogicalNotExpression n, A arg);
	T visit(AssignExpression e, A arg);
	T visit(FloatLiteral f, A arg);
	T visit(IntegerLiteral i, A arg);
	T visit(BooleanLiteral b, A arg);
	T visit(StringLiteral s, A arg);
	T visit(VarLiteral v, A arg);
	T visit(Identifier i, A arg);
}
