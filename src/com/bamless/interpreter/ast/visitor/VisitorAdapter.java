package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
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
import com.bamless.interpreter.ast.statement.ReturnStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public class VisitorAdapter<T, A> implements GenericVisitor<T, A> {

	@Override
	public T visit(Visitable v, A arg) {
		return null;
	}

	@Override
	public T visit(Program p, A arg) {
		for(FuncDecl f : p.getFunctions()) {
			f.accept(this, arg);
		}
		
		return null;
	}
	
	@Override
	public T visit(IfStatement v, A arg) {
		v.getCondition().accept(this, arg);
		v.getThenStmt().accept(this, arg);
		if(v.getElseStmt() != null)
			v.getElseStmt().accept(this, arg);
		
		return null;
	}

	@Override
	public T visit(WhileStatement v, A arg) {
		v.getCondition().accept(this, arg);
		v.getBody().accept(this, null);
		
		return null;
	}

	
	@Override
	public T visit(ForStatement v, A arg) {
		if(v.getInit() != null)
			v.getInit().accept(this, arg);
		if(v.getCond() != null)
			v.getCond().accept(this, arg);
		if(v.getAct() != null)
			v.getAct().accept(this, arg);
		v.getBody().accept(this, arg);
		
		return null;
	}

	@Override
	public T visit(BlockStatement v, A arg) {
		for(Statement s : v.getStmts()) {
			s.accept(this, arg);
		}
		
		return null;
	}
	
	@Override
	public T visit(PrintStatement p, A arg) {
		p.getExpression().accept(this, arg);
		
		return null;
	}
	
	@Override
	public T visit(ReturnStatement r, A arg) {
		r.getExpression().accept(this, arg);
		
		return null;
	}

	@Override
	public T visit(VarDecl v, A arg) {
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, arg);
		
		return null;
	}
	
	@Override
	public T visit(ArrayDecl a, A arg) {
		for(Expression e : a.getDimensions()) {
			e.accept(this, arg);
		}
		
		return null;
	}

	@Override
	public T visit(ArithmeticBinExpression e, A arg) {
		e.getLeft().accept(this, arg);
		e.getRight().accept(this, arg);
		
		return null;
	}

	@Override
	public T visit(LogicalExpression l, A arg) {
		l.getLeft().accept(this, arg);
		l.getRight().accept(this, arg);
		
		return null;
	}
	
	@Override
	public T visit(RelationalExpression r, A arg) {
		r.getLeft().accept(this, arg);
		r.getRight().accept(this, arg);
		
		return null;
	}

	@Override
	public T visit(EqualityExpression e, A arg) {
		e.getLeft().accept(this, arg);
		e.getRight().accept(this, arg);
		
		return null;
	}

	@Override
	public T visit(LogicalNotExpression n, A arg) {
		n.getExpression().accept(this, arg);
		return null;
	}
	
	@Override
	public T visit(PostIncrementOperation p, A arg) {
		p.getExpression().accept(this, arg);
		return null;
	}

	@Override
	public T visit(PreIncrementOperation p, A arg) {
		p.getExpression().accept(this, arg);
		return null;
	}

	@Override
	public T visit(AssignExpression e, A arg) {
		e.getLvalue().accept(this, arg);
		e.getExpression().accept(this, arg);
		
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
	public T visit(StringLiteral s, A arg) {
		return null;
	}

	@Override
	public T visit(VarLiteral v, A arg) {
		return null;
	}

	@Override
	public T visit(ArrayAccess a, A arg) {
		a.getLvalue().accept(this, arg);
		a.getIndex().accept(this, arg);
		
		return null;
	}
	
	@Override
	public T visit(FuncCallExpression f, A arg) {
		for(Expression e : f.getArgs()) {
			e.accept(this, arg);
		}
		
		return null;
	}

	@Override
	public T visit(FuncDecl d, A arg) {
		d.getBody().accept(this, arg);

		return null;
	}

}
