package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.Position;
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
import com.bamless.interpreter.ast.type.ArrayType;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.Visitable;

/**
 * This class walks the AST and verifies if all the types are used correctly according to the laguage spec.
 * @author fabrizio
 */
public class TypeChecker implements GenericVisitor<Type, Void> {
	private SymbolTable<Type> st;

	public TypeChecker() {
		this.st = new SymbolTable<>();
	}

	public Type visit(Program p, Void arg) {
		return p.getBlock().accept(this, arg);
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
			typeError(i.getCondition().getPosition(), "cannot convert %s to boolean",
					condition.toString().toLowerCase());
		}

		i.getThenStmt().accept(this, null);
		if(i.getElseStmt() != null)
			i.getElseStmt().accept(this, null);

		return null;
	}

	@Override
	public Type visit(ForStatement f, Void arg) {
		// check condition type
		Type condition = f.getCond() == null ? Type.BOOLEAN : f.getCond().accept(this, null);
		if(condition != Type.BOOLEAN) {
			typeError(f.getCond().getPosition(), "cannot convert %s to boolean", condition.toString().toLowerCase());
		}

		// propagate visitor to the other 2 expressions and to the body
		if(f.getInit() != null)
			f.getInit().accept(this, null);
		if(f.getAct() != null)
			f.getAct().accept(this, null);

		f.getBody().accept(this, null);

		return null;
	}

	@Override
	public Type visit(WhileStatement w, Void arg) {
		Type condition = w.getCondition().accept(this, null);

		if(condition != Type.BOOLEAN) {
			typeError(w.getCondition().getPosition(), "cannot convert %s to boolean",
					condition.toString().toLowerCase());
		}

		w.getBody().accept(this, null);
		return null;
	}

	@Override
	public Type visit(PrintStatement p, Void arg) {
		Type e = p.getExpression().accept(this, null);
		if(e != Type.STRING) {
			typeError(p.getExpression().getPosition(), "cannot convert %s to string", e.toString());
		}

		return null;
	}

	@Override
	public Type visit(VarDecl v, Void arg) {
		st.define(v.getId().getVal(), v.getType());

		if(v.getInitializer() != null)
			v.getInitializer().accept(this, null);

		return null;
	}

	@Override
	public Type visit(ArrayDecl a, Void arg) {
		st.define(a.getId().getVal(), a.getType());

		for(Expression e : a.getDimensions()) {
			Type ind = e.accept(this, arg);
			if(ind != Type.INT)
				typeError(e.getPosition(), "cannot convert from %s to int", ind.toString().toLowerCase());
		}

		return null;
	}

	/* ************************* */
	/* 	 	  Expressions        */
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
			undefOperatorError(e.getPosition(), e.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
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
			undefOperatorError(l.getPosition(), l.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
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
			undefOperatorError(e.getPosition(), e.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
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
			undefOperatorError(r.getPosition(), r.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
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
	public Type visit(PostIncrementOperation p, Void arg) {
		Type t = p.getExpression().accept(this, null);

		if(t != Type.INT && t != Type.FLOAT) {
			undefOperatorError(p.getPosition(), p.getOperator().toString(), t.toString().toLowerCase());
		}

		p.setType(t);
		return t;
	}

	@Override
	public Type visit(PreIncrementOperation p, Void arg) {
		Type t = p.getExpression().accept(this, null);

		if(t != Type.INT && t != Type.FLOAT) {
			undefOperatorError(p.getPosition(), p.getOperator().toString(), t.toString().toLowerCase());
		}

		p.setType(t);
		return t;
	}

	@Override
	public Type visit(AssignExpression e, Void arg) {
		Type lval = e.getLvalue().accept(this, arg);
		Type expr = e.getExpression().accept(this, null);

		if(!lval.canAssign(expr)) {
			typeError(e.getPosition(), "type mismatch, cannot assign %s to %s", expr.toString().toLowerCase(),
					lval.toString().toLowerCase());
		}

		if(lval == Type.INT && expr == Type.FLOAT) {
			ErrUtils.warn("Warning %s: implicit conversion from float to int, possible loss of precision",
					e.getExpression().getPosition());
		}

		e.setType(lval);
		return lval;
	}

	@Override
	public Type visit(VarLiteral v, Void arg) {
		Type t = st.lookup(v.getId().getVal());

		v.setType(t);
		return t;
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
	public Type visit(ArrayAccess a, Void arg) {
		Type ltype = a.getLvalue().accept(this, arg);

		if(!ltype.isArray()) {
			typeError(a.getPosition(),
					"The type of the expression must be an array type," + " but instead resolved to %s",
					ltype.toString().toLowerCase());
		}

		Type index = a.getIndex().accept(this, arg);
		if(index != Type.INT)
			typeError(a.getIndex().getPosition(), "array access index must evaluate to int");

		Type type = ((ArrayType) ltype).getInternalType();
		a.setType(type);
		return type;
	}

	private void typeError(Position pos, String format, Object... args) {
		throw new TypeException(String.format("Type error at " + pos + ": " + format, args));
	}

	private void undefOperatorError(Position pos, String operator, String... types) {
		throw new TypeException("Type error at " + pos + " : operator " + operator + " undefined for the type"
				+ (types.length > 1 ? "s" : "") + " " + String.join(", ", types));
	}

	@Override
	public Type visit(Visitable v, Void arg) {
		return null;
	}

}
