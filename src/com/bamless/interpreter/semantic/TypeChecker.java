package com.bamless.interpreter.semantic;

import java.util.List;
import java.util.Map;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.FormalArg;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.CastExpression;
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
import com.bamless.interpreter.ast.type.ArrayType;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.Visitable;

/**
 * This class walks the AST and verifies if all the types are used correctly
 * according to the laguage spec.
 * 
 * @author fabrizio
 */
public class TypeChecker implements GenericVisitor<Type, FuncDecl> {
	private SymbolTable<Type> st;
	private Map<String, FuncDecl> funcs;

	public TypeChecker() {
		this.st = new SymbolTable<>();
	}

	public Type visit(Program p, FuncDecl currentFunc) {
		this.funcs = p.getFunctions();

		for (String id : funcs.keySet()) {
			funcs.get(id).accept(this, null);
		}

		return null;
	}

	@Override
	public Type visit(FuncDecl d, FuncDecl currentFunc) {
		st.enterScope();

		for (FormalArg a : d.getFormalArgs()) {
			st.define(a.getIdentifier().getVal(), a.getType());
		}

		d.getBody().accept(this, d);

		st.exitScope();

		return null;
	}

	@Override
	public Type visit(BlockStatement b, FuncDecl currentFunc) {
		st.enterScope();
		for (Statement stmt : b.getStmts()) {
			stmt.accept(this, currentFunc);
		}
		st.exitScope();

		return null;
	}

	@Override
	public Type visit(IfStatement i, FuncDecl currentFunc) {
		Type condition = i.getCondition().accept(this, currentFunc);
		if (condition != Type.BOOLEAN) {
			typeError(i.getCondition().getPosition(), "cannot convert %s to boolean",
					condition.toString().toLowerCase());
		}

		i.getThenStmt().accept(this, currentFunc);
		if (i.getElseStmt() != null)
			i.getElseStmt().accept(this, currentFunc);

		return null;
	}

	@Override
	public Type visit(ForStatement f, FuncDecl currentFunc) {
		// propagate visitor to the other 2 expressions and to the body
		if (f.getInit() != null)
			f.getInit().accept(this, currentFunc);

		// check condition type
		Type condition = f.getCond() == null ? Type.BOOLEAN : f.getCond().accept(this, currentFunc);
		if (condition != Type.BOOLEAN) {
			typeError(f.getCond().getPosition(), "cannot convert %s to boolean", condition.toString().toLowerCase());
		}

		if (f.getAct() != null)
			f.getAct().accept(this, currentFunc);

		f.getBody().accept(this, currentFunc);

		return null;
	}

	@Override
	public Type visit(WhileStatement w, FuncDecl currentFunc) {
		Type condition = w.getCondition().accept(this, currentFunc);

		if (condition != Type.BOOLEAN) {
			typeError(w.getCondition().getPosition(), "cannot convert %s to boolean",
					condition.toString().toLowerCase());
		}

		w.getBody().accept(this, currentFunc);
		return null;
	}

	@Override
	public Type visit(PrintStatement p, FuncDecl currentFunc) {
		Type e = p.getExpression().accept(this, currentFunc);
		if (e != Type.STRING) {
			typeError(p.getExpression().getPosition(), "cannot convert %s to string", e.toString());
		}

		return null;
	}

	@Override
	public Type visit(VarDecl v, FuncDecl currentFunc) {
		st.define(v.getId().getVal(), v.getType());

		if (v.getInitializer() != null)
			v.getInitializer().accept(this, currentFunc);

		return null;
	}

	@Override
	public Type visit(ArrayDecl a, FuncDecl currentFunc) {
		st.define(a.getId().getVal(), a.getType());

		for (Expression e : a.getDimensions()) {
			Type ind = e.accept(this, currentFunc);
			if (ind != Type.INT)
				typeError(e.getPosition(), "cannot convert from %s to int", ind.toString().toLowerCase());
		}

		return null;
	}

	@Override
	public Type visit(ReturnStatement r, FuncDecl currentFunc) {
		Type exp = r.getExpression() == null ? Type.VOID : r.getExpression().accept(this, currentFunc);
		boolean ret = currentFunc.getType().canAssign(exp);

		if (!ret) {
			typeError(r.getPosition(), "Return type mismatch, cannot convert from %s to %s",
					exp.toString().toLowerCase(), currentFunc.getType().toString().toLowerCase());
		}

		if (currentFunc.getType() == Type.INT && exp == Type.FLOAT) {
			ErrUtils.warn("Warning %s: implicit conversion from float to int, possible loss of precision",
					r.getExpression().getPosition());
		}

		return null;
	}

	/* ************************* */
	/* Expressions */
	/* ************************* */

	@Override
	public Type visit(ArithmeticBinExpression e, FuncDecl currentFunc) {
		Type left = e.getLeft().accept(this, currentFunc);
		Type right = e.getRight().accept(this, currentFunc);

		Type res = null;
		switch (e.getOperation()) {
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

		if (res == null) {
			undefOperatorError(e.getPosition(), e.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
		}

		e.setType(res);
		return res;
	}

	@Override
	public Type visit(LogicalExpression l, FuncDecl currentFunc) {
		Type left = l.getLeft().accept(this, currentFunc);
		Type right = l.getRight().accept(this, currentFunc);
		Type res = left.logicalOp(right);

		if (res == null) {
			undefOperatorError(l.getPosition(), l.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
		}

		l.setType(res);
		return res;
	}

	@Override
	public Type visit(EqualityExpression e, FuncDecl currentFunc) {
		Type left = e.getLeft().accept(this, currentFunc);
		Type right = e.getRight().accept(this, currentFunc);
		Type res = left.equalityOp(right);

		if (res == null) {
			undefOperatorError(e.getPosition(), e.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
		}

		e.setType(res);
		return res;
	}

	@Override
	public Type visit(RelationalExpression r, FuncDecl currentFunc) {
		Type left = r.getLeft().accept(this, currentFunc);
		Type right = r.getRight().accept(this, currentFunc);
		Type res = left.relationalOp(right);

		if (res == null) {
			undefOperatorError(r.getPosition(), r.getOperation().toString(), left.toString().toLowerCase(),
					right.toString().toLowerCase());
		}

		r.setType(res);
		return res;
	}

	@Override
	public Type visit(LogicalNotExpression n, FuncDecl currentFunc) {
		Type t = n.getExpression().accept(this, currentFunc);

		if (t != Type.BOOLEAN) {
			undefOperatorError(n.getPosition(), "!", t.toString().toLowerCase());
		}

		n.setType(Type.BOOLEAN);
		return Type.BOOLEAN;
	}

	@Override
	public Type visit(PostIncrementOperation p, FuncDecl currentFunc) {
		Type t = p.getExpression().accept(this, currentFunc);

		if (t != Type.INT && t != Type.FLOAT) {
			undefOperatorError(p.getPosition(), p.getOperator().toString(), t.toString().toLowerCase());
		}

		p.setType(t);
		return t;
	}

	@Override
	public Type visit(PreIncrementOperation p, FuncDecl currentFunc) {
		Type t = p.getExpression().accept(this, currentFunc);

		if (t != Type.INT && t != Type.FLOAT) {
			undefOperatorError(p.getPosition(), p.getOperator().toString(), t.toString().toLowerCase());
		}

		p.setType(t);
		return t;
	}

	@Override
	public Type visit(AssignExpression e, FuncDecl currentFunc) {
		Type lval = e.getLvalue().accept(this, currentFunc);
		Type expr = e.getExpression().accept(this, currentFunc);

		if (!lval.canAssign(expr)) {
			typeError(e.getPosition(), "type mismatch, cannot assign %s to %s", expr.toString().toLowerCase(),
					lval.toString().toLowerCase());
		}

		if (lval == Type.INT && expr == Type.FLOAT) {
			ErrUtils.warn("Warning %s: implicit conversion from float to int, possible loss of precision",
					e.getExpression().getPosition());
		}

		e.setType(lval);
		return lval;
	}

	@Override
	public Type visit(FuncCallExpression f, FuncDecl currentFunc) {
		for (Expression e : f.getArgs()) {
			e.accept(this, currentFunc);
		}

		List<Expression> callArgs = f.getArgs();
		List<FormalArg> declArgs = funcs.get(f.getFuncName().getVal()).getFormalArgs();

		for (int i = 0; i < callArgs.size(); i++) {
			Type callType = callArgs.get(i).getType();
			Type declType = declArgs.get(i).getType();

			if (!callType.canAssign(declType)) {
				typeError(callArgs.get(i).getPosition(),
						"type mismatch, cannot convert %s to %s on %s argument of function call `%s`",
						callType.toString().toLowerCase(), declType.toString().toLowerCase(), cardinal(i + 1),
						f.getFuncName());
			}

			if (declType == Type.INT && callType == Type.FLOAT) {
				ErrUtils.warn("Warning %s: implicit conversion from float to int, possible loss of precision",
						callArgs.get(i).getPosition());
			}
		}

		f.setType(funcs.get(f.getFuncName().getVal()).getType());
		return f.getType();
	}

	@Override
	public Type visit(CastExpression c, FuncDecl arg) {
		Type exprType = c.getExpression().accept(this, arg);
		if (!c.getType().canAssign(exprType)) {
			typeError(c.getPosition(), "Cannot cast from %s to %s", exprType.toString().toLowerCase(),
					c.getType().toString().toLowerCase());
		}

		return c.getType();
	}

	@Override
	public Type visit(VarLiteral v, FuncDecl currentFunc) {
		Type t = st.lookup(v.getId().getVal());

		v.setType(t);
		return t;
	}

	@Override
	public Type visit(BooleanLiteral b, FuncDecl currentFunc) {
		return b.getType();
	}

	@Override
	public Type visit(FloatLiteral f, FuncDecl currentFunc) {
		return f.getType();
	}

	@Override
	public Type visit(IntegerLiteral i, FuncDecl currentFunc) {
		return i.getType();
	}

	@Override
	public Type visit(StringLiteral s, FuncDecl currentFunc) {
		return s.getType();
	}

	@Override
	public Type visit(ArrayAccess a, FuncDecl currentFunc) {
		Type ltype = a.getLvalue().accept(this, currentFunc);

		if (!ltype.isArray()) {
			typeError(a.getPosition(),
					"The type of the expression must be an array type," + " but instead resolved to %s",
					ltype.toString().toLowerCase());
		}

		Type index = a.getIndex().accept(this, currentFunc);
		if (index != Type.INT)
			typeError(a.getIndex().getPosition(), "array access index must evaluate to int");

		Type type = ((ArrayType) ltype).getInternalType();
		a.setType(type);
		return type;
	}

	private void typeError(Position pos, String format, Object... currentFuncs) {
		throw new TypeException(String.format("Type error at " + pos + ": " + format, currentFuncs));
	}

	private void undefOperatorError(Position pos, String operator, String... types) {
		throw new TypeException("Type error at " + pos + " : operator " + operator + " undefined for the type"
				+ (types.length > 1 ? "s" : "") + " " + String.join(", ", types));
	}

	private String cardinal(int i) {
		switch (i) {
		case 1:
			return i + "st";
		case 2:
			return i + "nd";
		case 3:
			return i + "rd";
		default:
			return i + "th";
		}
	}

	@Override
	public Type visit(Visitable v, FuncDecl currentFunc) {
		return null;
	}

}
