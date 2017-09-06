package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.Position;
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
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.Visitable;

public class TypeChecker implements GenericVisitor<Type, Void> {
	private SymbolTable<Type> st;
	
	public TypeChecker() {
		this.st = new SymbolTable<>();
	}
	
	@Override
	public Type visit(BlockStatement b, Void arg) {
		st.enterScope();
		for(Statement stmt : b.getStmts()) {
			stmt.accept(this, null);
		}
		st.exitScope();
		
		return null;
	}
	
	@Override
	public Type visit(IfStatement i, Void arg) {
		Type condition = i.getCondition().accept(this, null);
		if(condition != Type.BOOLEAN) {
			typeError(i.getCondition().getPosition(), "if condition must evaluate to boolean");
		}
		
		i.getThenStmt().accept(this, null);
		if(i.getElseStmt() != null) 
			i.getElseStmt().accept(this, null);
		
		return null;
	}
	
	@Override
	public Type visit(ForStatement f, Void arg) {
		//check condition type
		Type condition = f.getCond().accept(this, null);
		
		if(condition != Type.BOOLEAN)
			typeError(f.getCond().getPosition(), "Type for condition must evaluate to boolean");
		
		//propagate visitor to the other 2 expressions an to the body
		f.getInit().accept(this, null);
		f.getAct().accept(this, null);
		
		f.getBody().accept(this, null);

		return null;
	}
	
	@Override
	public Type visit(WhileStatement w, Void arg) {
		Type condition = w.getCondition().accept(this, null);
		
		if(condition != Type.BOOLEAN)
			typeError(w.getCondition().getPosition(), "while condition must evaluate to boolean");
		
		w.getBody().accept(this, null);
		return null;
	}
	
	@Override
	public Type visit(VarDecl v, Void arg) {
		if(st.probe(v.getId().getVal()) != null) {
			semanticError(v.getId().getPosition(), "duplicate local variable \"%s\"", v.getId().getVal());
		}
		
		st.define(v.getId().getVal(), v.getType());
		
		if(v.getInitializer() != null) 
			v.getInitializer().accept(this, null);
		
		return null;
	}
	
	
	/* ************************* */
	/*        Expressions        */
	/* ************************* */
	
	@Override
	public Type visit(ArithmeticBinExpression e, Void arg) {
		Type left = e.getLeft().accept(this, null);
		Type right = e.getRight().accept(this, null);
		
		Type res = null;
		switch(e.getOperation()) {
		case DIV:
			res = left.div(right);
			break;
		case MINUS:
			res = left.minus(right);
			break;
		case MOD:
			res = left.modulus(right);
			break;
		case MULT:
			res = left.mul(right);
			break;
		case PLUS:
			res = left.plus(right);
			break;
		}
		
		if(res == null) {
			undefOperatorError(e.getPosition(), e.getOperation().toString().toLowerCase(), 
						left.toString().toLowerCase(), right.toString().toLowerCase());
		}
		
		e.setType(res);
		return res;
	}

	@Override
	public Type visit(LogicalExpression l, Void arg) {
		Type left = l.getLeft().accept(this, null);
		Type right = l.getRight().accept(this, null);
		
		Type res = left.logicalOp(right);
		
		if(res == null) {
			undefOperatorError(l.getPosition(), l.getOperation().toString().toLowerCase(), 
						left.toString().toLowerCase(), right.toString().toLowerCase());
		}
		
		l.setType(res);
		return res;
	}
	
	@Override
	public Type visit(EqualityExpression e, Void arg) {
		Type left = e.getLeft().accept(this, null);
		Type right = e.getRight().accept(this, null);
		
		Type res = left.equalityOp(right);
		if(res == null) {
			undefOperatorError(e.getPosition(), e.getOperation().toString().toLowerCase(), 
						left.toString().toLowerCase(), right.toString().toLowerCase());
		}
		
		e.setType(res);
		return res;
	}
	
	@Override
	public Type visit(RelationalExpression r, Void arg) {
		Type left = r.getLeft().accept(this, null);
		Type right = r.getRight().accept(this, null);
		
		Type res = left.relationalOp(right);
		if(res == null) {
			undefOperatorError(r.getPosition(), r.getOperation().toString().toLowerCase(), 
						left.toString().toLowerCase(), right.toString().toLowerCase());
		}
		
		r.setType(res);
		return res;
	}
	
	@Override
	public Type visit(LogicalNotExpression n, Void arg) {
		Type t = n.getExpression().accept(this, null);
		if(t != Type.BOOLEAN) {
			undefOperatorError(n.getPosition(), "!", t.toString().toLowerCase());
		}
		
		n.setType(Type.BOOLEAN);
		return Type.BOOLEAN;
	}
	
	@Override
	public Type visit(AssignExpression e, Void arg) {
		Type self = e.getId().accept(this, null);
		Type expr = e.getExpression().accept(this, null);
		
		if(!self.canAssign(expr)) {
			typeError(e.getPosition(), "type mismatch, cannot assign %s to %s", 
					 expr.toString().toLowerCase(), self.toString().toLowerCase());
		}
		
		if(self == Type.INT && expr == Type.FLOAT) {
			ErrUtils.warn("Warning %s: implicit conversion to int, possible loss of precision", e.getExpression().getPosition());
		}
		
		e.setType(self);
		return self;
	}
	
	@Override
	public Type visit(BooleanLiteral b, Void arg) {
		return b.getType();
	}
	
	@Override
	public Type visit(FloatLiteral f, Void arg) {
		return f.getType();
	}
	
	@Override
	public Type visit(IntegerLiteral i, Void arg) {
		return i.getType();
	}
	
	@Override
	public Type visit(StringLiteral s, Void arg) {
		return s.getType();
	}
	
	@Override
	public Type visit(VarLiteral v, Void arg) {
		Type t = v.getId().accept(this, null);
		v.setType(t);
		return t;
	}
	
	@Override
	public Type visit(Identifier i, Void arg) {
		Type t = st.lookup(i.getVal());
		if(t == null) {
			semanticError(i.getPosition(), "variable %s cannot be resolved", i.getVal());
		}
		return t;
	}
	
	private void semanticError(Position pos, String format, Object... args) {
		throw new SemanticException(String.format("Semantic error at " + pos + ": " + format, args));
	}
	
	private void typeError(Position pos, String format, Object... args) {
		throw new TypeException(String.format("Type error at " + pos + ": " + format, args));
	}
	
	private void undefOperatorError(Position pos, String operator, String... types) {
		throw new TypeException("Type error at " + pos + " : operator " + operator 
				+ " undefined for the type" + (types.length > 1 ? "s" : "") + " " + String.join(", ", types));
	}

	@Override
	public Type visit(Visitable v, Void arg) {
		return null;
	}

}
