package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.statement.ArrayDecl;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;

public class SemanticAnalyzer extends VoidVisitorAdapter<Void> {
	/**
	 * Symbol table to keep track of declared variables. The boolean associated with
	 * the variable's identifier denotes if the variable is initialized or not
	 */
	private SymbolTable<Boolean> sym;

	public SemanticAnalyzer() {
		sym = new SymbolTable<>();
	}

	@Override
	public void visit(BlockStatement v, Void arg) {
		sym.enterScope();
		for(Statement s : v.getStmts()) {
			if(s instanceof Expression && !hasSideEffect((Expression) s)) {
				ErrUtils.warn("Warning %s: computed value is not used", s.getPosition());
			}
			s.accept(this, null);
		}
		sym.exitScope();
	}

	@Override
	public void visit(ForStatement v, Void arg) {
		if(v.getInit() != null && !hasSideEffect(v.getInit())) {
			ErrUtils.warn("Warning %s: computed value is not used", v.getInit().getPosition());
		}
		if(v.getAct() != null && !hasSideEffect(v.getAct())) {
			ErrUtils.warn("Warning %s: computed value is not used", v.getAct().getPosition());
		}

		if(v.getInit() != null)
			v.getInit().accept(this, arg);
		if(v.getCond() != null)
			v.getCond().accept(this, arg);
		if(v.getAct() != null)
			v.getAct().accept(this, arg);
		v.getBody().accept(this, arg);
	}

	@Override
	public void visit(VarDecl v, Void arg) {
		try {
			sym.define(v.getId().getVal(), v.getInitializer() != null);
		} catch(IllegalArgumentException e) {
			semanticError(v.getPosition(), "double declaration of variable %s", v.getId().getVal());
		}
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, arg);
	}

	@Override
	public void visit(ArrayDecl a, Void arg) {
		try {
			// true because arrays get initialized automagically
			sym.define(a.getId().getVal(), true);
		} catch(IllegalArgumentException e) {
			semanticError(a.getPosition(), "double declaration of variable %s", a.getId().getVal());
		}

		for(Expression e : a.getDimensions()) {
			e.accept(this, arg);
		}
	}

	@Override
	public void visit(AssignExpression e, Void arg) {
		if(!(e.getLvalue() instanceof Lvalue))
			semanticError(e.getPosition(), "left hand side of assignement must be an lvalue");

		if(e.getLvalue() instanceof VarLiteral) {
			VarLiteral v = (VarLiteral) e.getLvalue();
			try {
				sym.set(v.getId().getVal(), true);
			} catch(IllegalArgumentException ex) {
				semanticError(v.getId().getPosition(), "variable %s cannot be resolved", v.getId().getVal());
			}
		}

		e.getLvalue().accept(this, arg);
		e.getExpression().accept(this, arg);
	}
	
	@Override
	public void visit(PreIncrementOperation p, Void arg) {
		if(!(p.getExpression() instanceof Lvalue))
			semanticError(p.getPosition(), "left hand side of assignement must be an lvalue");
		
		p.getExpression().accept(this, arg);
	}
	
	@Override
	public void visit(PostIncrementOperation p, Void arg) {
		if(!(p.getExpression() instanceof Lvalue))
			semanticError(p.getPosition(), "left hand side of assignement must be an lvalue");
		
		p.getExpression().accept(this, arg);
	}

	@Override
	public void visit(VarLiteral v, Void arg) {
		Boolean isInit = sym.lookup(v.getId().getVal());
		if(isInit == null)
			semanticError(v.getId().getPosition(), "variable %s cannot be resolved", v.getId().getVal());
		if(!isInit)
			semanticError(v.getId().getPosition(), "the local variable %s may not have been initialized",
					v.getId().getVal());
	}

	private void semanticError(Position pos, String format, Object... args) {
		throw new SemanticException(String.format("Semantic error at " + pos + ": " + format, args));
	}
	
	private boolean hasSideEffect(Expression e) {
		if(e instanceof AssignExpression || e instanceof PostIncrementOperation 
				|| e instanceof PreIncrementOperation) {
			return true;
		}
		return false;
	}
}
