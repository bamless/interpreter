package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
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
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public interface GenericVisitor<T, A> {
	T visit(Visitable v, A arg);
	
	T visit(Program p, A arg);
	
	T visit(IfStatement i, A arg);
	T visit(WhileStatement w, A arg);
	T visit(BlockStatement b, A arg);
	T visit(ForStatement f, A arg);
	T visit(PrintStatement p, A arg);
	
	T visit(VarDecl v, A arg);
	T visit(ArrayDecl a, A arg);
	
	T visit(ArithmeticBinExpression e, A arg);
	T visit(LogicalExpression l, A arg);
	T visit(EqualityExpression e, A arg);
	T visit(RelationalExpression r, A arg);
	T visit(LogicalNotExpression n, A arg);
	T visit(PostIncrementOperation p, A arg);
	T visit(PreIncrementOperation p, A arg);
	T visit(AssignExpression e, A arg);
	
	T visit(FloatLiteral f, A arg);
	T visit(IntegerLiteral i, A arg);
	T visit(BooleanLiteral b, A arg);
	T visit(StringLiteral s, A arg);
	T visit(VarLiteral v, A arg);
	T visit(ArrayAccess a, A arg);
}
