package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
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
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.Visitable;

/**
 * AST walker that checks if every functions declaring a return type actually returns on every
 * execution path.
 * @author fabrizio
 *
 */
public class ReturnChecker implements GenericVisitor<Boolean, Void> {

	@Override
	public Boolean visit(Program p, Void arg) {
		for(FuncDecl d : p.getFunctions()) {
			if(d.getType() != Type.VOID)
				d.accept(this, arg);
		}

		return null;
	}

	@Override
	public Boolean visit(FuncDecl d, Void arg) {
		boolean ret = d.getBody().accept(this, arg);
		if(!ret) {
			ErrUtils.semanticError(d.getPosition(), "Function `%s` declares return type of %s, but does not return.",
					d.getId(), d.getType().toString().toLowerCase());
		}
		return ret;
	}

	@Override
	public Boolean visit(BlockStatement v, Void arg) {
		boolean ret = false;

		int i;
		for(i = 0; i < v.getStmts().size(); i++) {
			Statement s = v.getStmts().get(i);
			
			if(s instanceof ReturnStatement || s instanceof IfStatement)
				ret |= s.accept(this, arg);
			else
				s.accept(this, arg);
			
			if(ret) break;
		}

		if(i < v.getStmts().size() - 1) {
			ErrUtils.semanticError(v.getStmts().get(i + 1).getPosition(), "Unreachable code.");
		}

		return ret;
	}

	@Override
	public Boolean visit(ForStatement v, Void arg) {
		return v.getBody().accept(this, arg);
	}

	@Override
	public Boolean visit(WhileStatement v, Void arg) {
		return v.getBody().accept(this, arg);
	}

	@Override
	public Boolean visit(IfStatement v, Void arg) {
		boolean thenStmt = v.getThenStmt().accept(this, arg);
		boolean elseStmt = v.getElseStmt() == null ? false : v.getElseStmt().accept(this, arg);

		return thenStmt && elseStmt;
	}

	@Override
	public Boolean visit(ReturnStatement r, Void arg) {
		return true;
	}

	@Override
	public Boolean visit(Visitable v, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(PrintStatement p, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(VarDecl v, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(ArrayDecl a, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(ArithmeticBinExpression e, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(LogicalExpression l, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(EqualityExpression e, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(RelationalExpression r, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(LogicalNotExpression n, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(PostIncrementOperation p, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(PreIncrementOperation p, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(AssignExpression e, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(FloatLiteral f, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(IntegerLiteral i, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(BooleanLiteral b, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(StringLiteral s, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(VarLiteral v, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(ArrayAccess a, Void arg) {
		return false;
	}

	@Override
	public Boolean visit(FuncCallExpression f, Void arg) {
		return false;
	}

}
