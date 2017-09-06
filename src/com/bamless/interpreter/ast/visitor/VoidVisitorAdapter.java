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

public class VoidVisitorAdapter<A> implements VoidVisitor<A> {

	@Override
	public void visit(Visitable v, A arg) {
	}

	@Override
	public void visit(IfStatement v, A arg) {
	}

	@Override
	public void visit(WhileStatement v, A arg) {
		v.getBody().accept(this, null);
	}

	
	@Override
	public void visit(ForStatement v, A arg) {
	}

	@Override
	public void visit(BlockStatement v, A arg) {
	}

	@Override
	public void visit(VarDecl v, A arg) {
	}

	@Override
	public void visit(ArithmeticBinExpression e, A arg) {
	}

	@Override
	public void visit(LogicalExpression l, A arg) {
	}
	
	@Override
	public void visit(RelationalExpression r, A arg) {
	}

	@Override
	public void visit(EqualityExpression e, A arg) {
	}

	@Override
	public void visit(FloatLiteral f, A arg) {
	}

	@Override
	public void visit(IntegerLiteral i, A arg) {
	}

	@Override
	public void visit(BooleanLiteral b, A arg) {
	}

	@Override
	public void visit(VarLiteral v, A arg) {
	}

	@Override
	public void visit(LogicalNotExpression n, A arg) {
	}

	@Override
	public void visit(AssignExpression e, A arg) {
	}

	@Override
	public void visit(Identifier i, A arg) {
	}

	@Override
	public void visit(StringLiteral s, A arg) {
	}
}
