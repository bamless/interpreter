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

public class VisitorAdapter<T, A> implements GenericVisitor<T, A> {

	@Override
	public T visit(Visitable v, A arg) {
		return null;
	}

	@Override
	public T visit(IfStatement i, A arg) {
		return null;
	}

	@Override
	public T visit(WhileStatement w, A arg) {
		w.getBody().accept(this, null);
		return null;
	}

	
	@Override
	public T visit(ForStatement f, A arg) {
		return null;
	}

	@Override
	public T visit(BlockStatement b, A arg) {
		return null;
	}

	@Override
	public T visit(VarDecl v, A arg) {
		return null;
	}

	@Override
	public T visit(ArithmeticBinExpression e, A arg) {
		return null;
	}

	@Override
	public T visit(LogicalExpression l, A arg) {
		return null;
	}


	@Override
	public T visit(RelationalExpression r, A arg) {
		return null;
	}
	
	@Override
	public T visit(EqualityExpression e, A arg) {
		return null;
	}

	@Override
	public T visit(FloatLiteral f, A arg) {
		return null;
	}

	@Override
	public T visit(IntegerLiteral i, A arg) {
		return null;
	}

	@Override
	public T visit(BooleanLiteral b, A arg) {
		return null;
	}

	@Override
	public T visit(VarLiteral v, A arg) {
		return null;
	}

	@Override
	public T visit(LogicalNotExpression n, A arg) {
		return null;
	}

	@Override
	public T visit(AssignExpression e, A arg) {
		return null;
	}

	@Override
	public T visit(Identifier i, A arg) {
		return null;
	}

	@Override
	public T visit(StringLiteral s, A arg) {
		return null;
	}

}
