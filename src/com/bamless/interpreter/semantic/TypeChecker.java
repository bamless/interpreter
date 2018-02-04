package com.bamless.interpreter.semantic;

import java.util.Map;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.FormalArg;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BinaryExpression;
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
import com.bamless.interpreter.ast.statement.BreakStatement;
import com.bamless.interpreter.ast.statement.ContinueStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.PrintStatement;
import com.bamless.interpreter.ast.statement.ReturnStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;
import com.bamless.interpreter.ast.type.ArrayType;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.natives.Native;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.Visitable;

/**
 * This class walks the AST and verifies if all the types are used correctly
 * according to the laguage spec.
 * 
 * @author fabrizio
 */
public class TypeChecker implements GenericVisitor<Type, FuncDecl> {
	private SymbolTable<Type> st;
	private Map<String, FuncDecl> funcs;
	private Map<String, Native<?>> natives;

	public TypeChecker(Map<String, Native<?>> natives) {
		this.st = new SymbolTable<>();
		this.natives = natives;
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
		p.getExpression().accept(this, currentFunc);

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
		boolean ret = currentFunc.getType().isCompatible(exp);

		if (!ret) {
			typeError(r.getPosition(), "Return type mismatch, cannot convert from %s to %s",
					exp.toString().toLowerCase(), currentFunc.getType().toString().toLowerCase());
		}

		if (currentFunc.getType().getWidenFactor() < exp.getWidenFactor()) {
			ErrUtils.warn("Warning %s: implicit conversion from %s to %s, possible loss of precision",
					r.getExpression().getPosition(), exp, currentFunc.getType());
		}
		
		//types are compatible, apply type coercion if needed
		if(exp != currentFunc.getType()) {
			Expression e = r.getExpression();
			r.setExpression(new CastExpression(currentFunc.getType(), e, e.getPosition()));
		}

		return null;
	}

	/* ************************* */
	/*        Expressions        */
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
		
		//apply widening type coercion if needed
		widen(e);

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

		widen(e);
		
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

		widen(r);
		
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

		if (!lval.isCompatible(expr)) {
			typeError(e.getPosition(), "type mismatch, cannot assign %s to %s", expr.toString().toLowerCase(),
					lval.toString().toLowerCase());
		}

		if (lval.getWidenFactor() < expr.getWidenFactor()) {
			ErrUtils.warn("Warning %s: implicit conversion from %s to %s, possible loss of precision",
					 e.getExpression().getPosition(), expr, lval);
		}
		
		//types are compatible, apply type coercion if needed
		if(lval != expr) {
			e.setExpression(new CastExpression(lval, e.getExpression(), e.getExpression().getPosition()));
		}
		
		e.setType(lval);
		return lval;
	}

	@Override
	public Type visit(FuncCallExpression f, FuncDecl currentFunc) {
		for (Expression e : f.getArgs()) {
			e.accept(this, currentFunc);
		}
		
		Expression[] callArgs = f.getArgs();
		Type[] declArgsTypes = f.isNative() ? natives.get(f.getFuncName().getVal()).getArgTypes() :
				funcs.get(f.getFuncName().getVal()).getFormalArgsTypes();

		//check that number of arguments matches function decl
		if (callArgs.length != declArgsTypes.length) {
			ErrUtils.semanticError(f.getPosition(), "Function %s requires %d arguments, but instead %d supplied",
					f.getFuncName(), declArgsTypes.length, callArgs.length);
		}
		
		//check type of args against function decl
		for (int i = 0; i < callArgs.length; i++) {
			Type callType = callArgs[i].getType();
			Type declType = declArgsTypes[i];

			if (!callType.isCompatible(declType)) {
				typeError(callArgs[i].getPosition(),
						"type mismatch, cannot convert %s to %s on %s argument of function call `%s`",
						callType.toString().toLowerCase(), declType.toString().toLowerCase(), cardinal(i + 1),
						f.getFuncName());
			}

			if (declType.getWidenFactor() < callType.getWidenFactor()) {
				ErrUtils.warn("Warning %s: implicit conversion from %s to %s, possible loss of precision", 
						 callArgs[i].getPosition(), callType, declType);
			}
			
			//types are compatible, apply type coercion if needed
			if(callType != declType && !callType.isArray()) {
				callArgs[i] = new CastExpression(declType, callArgs[i], callArgs[i].getPosition());
			}
		}

		Type retType = f.isNative() ? natives.get(f.getFuncName().getVal()).getType() :
				funcs.get(f.getFuncName().getVal()).getType();
		f.setType(retType);
		return retType;
	}

	@Override
	public Type visit(CastExpression c, FuncDecl arg) {
		Type exprType = c.getExpression().accept(this, arg);
		if (!c.getType().isCompatible(exprType)) {
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

	@Override
	public Type visit(BreakStatement b, FuncDecl arg) {
		return null;
	}

	@Override
	public Type visit(ContinueStatement c, FuncDecl arg) {
		return null;
	}
	
	/**
	 * Widens one of the two subexpression of a binary expression to the wider type.
	 * The 2 types should be compatible, i.e. {@link Type#isCompatible(Type)} should
	 * return true.
	 */
	private void widen(BinaryExpression e) {
		Type l = e.getLeft().getType();
		Type r = e.getRight().getType();
		
		if(l == r || l.getWidenFactor() == Type.NULL_WIDENFACT || r.getWidenFactor() == Type.NULL_WIDENFACT)
			return;
		
		Type widen = l.getWidenFactor() < r.getWidenFactor() ? r : l;
		if(widen == r)
			e.setLeft(new CastExpression(widen, e.getLeft(), e.getLeft().getPosition()));
		else
			e.setRight(new CastExpression(widen, e.getRight(), e.getRight().getPosition()));
	}

}
