package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.RelationalExpression;
import com.bamless.interpreter.ast.expression.StringLiteral;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.statement.ArrayDecl;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.PrintStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public class VoidVisitorAdapter<A> implements VoidVisitor<A> {

	@Override
	public void visit(Visitable v, A arg) {
	}


	@Override
	public void visit(Program p, A arg) {
		p.getBlock().accept(this, arg);
	}
	
	@Override
	public void visit(IfStatement v, A arg) {
		v.getCondition().accept(this, arg);
		v.getThenStmt().accept(this, arg);
		if(v.getElseStmt() != null)
			v.getElseStmt().accept(this, arg);
	}

	@Override
	public void visit(WhileStatement v, A arg) {
		v.getCondition().accept(this, arg);
		v.getBody().accept(this, null);
	}

	
	@Override
	public void visit(ForStatement v, A arg) {
		if(v.getInit() != null)
			v.getInit().accept(this, arg);
		if(v.getCond() != null)
			v.getCond().accept(this, arg);
		if(v.getAct() != null)
			v.getAct().accept(this, arg);
		v.getBody().accept(this, arg);
	}

	@Override
	public void visit(BlockStatement v, A arg) {
		for(Statement s : v.getStmts()) {
			s.accept(this, arg);
		}
	}
	
	@Override
	public void visit(PrintStatement p, A arg) {
		p.getExpression().accept(this, arg);
	}

	@Override
	public void visit(VarDecl v, A arg) {
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, arg);
	}
	
	@Override
	public void visit(ArrayDecl a, A arg) {
		for(Expression e : a.getDimensions()) {
			e.accept(this, arg);
		}
	}

	@Override
	public void visit(ArithmeticBinExpression e, A arg) {
		e.getLeft().accept(this, arg);
		e.getRight().accept(this, arg);
	}

	@Override
	public void visit(LogicalExpression l, A arg) {
		l.getLeft().accept(this, arg);
		l.getRight().accept(this, arg);
	}
	
	@Override
	public void visit(RelationalExpression r, A arg) {
		r.getLeft().accept(this, arg);
		r.getRight().accept(this, arg);
	}

	@Override
	public void visit(EqualityExpression e, A arg) {
		e.getLeft().accept(this, arg);
		e.getRight().accept(this, arg);
	}

	@Override
	public void visit(LogicalNotExpression n, A arg) {
		n.getExpression().accept(this, arg);
	}

	@Override
	public void visit(PostIncrementOperation p, A arg) {
		p.getExpression().accept(this, arg);
	}

	@Override
	public void visit(PreIncrementOperation p, A arg) {
		p.getExpression().accept(this, arg);
	}
	
	@Override
	public void visit(AssignExpression e, A arg) {
		e.getLvalue().accept(this, arg);
		e.getExpression().accept(this, arg);
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
	public void visit(StringLiteral s, A arg) {
	}
	

	@Override
	public void visit(VarLiteral v, A arg) {
	}


	@Override
	public void visit(ArrayAccess a, A arg) {
		a.getLvalue().accept(this, arg);
		a.getIndex().accept(this, arg);
	}
	
}
