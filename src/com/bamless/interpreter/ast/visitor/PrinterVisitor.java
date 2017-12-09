package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.Expression;
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

public class PrinterVisitor extends VoidVisitorAdapter<Integer> {
	private String tabs;
	
	public PrinterVisitor(int tabLength) {
		if(tabLength < 0) throw new IllegalArgumentException("Negative tab length");
		if(tabLength == 0) {
			tabs = "\t";
		} else {
			tabs = new String(new char[tabLength]).replace('\0', ' ');
		}
	}
	
	public PrinterVisitor() {
		this(0);
	}
	
	@Override
	public void visit(Visitable v, Integer arg) {
		print(arg, "UNKNWN");
	}
	
	@Override
	public void visit(Program p, Integer arg) {
		for(String id : p.getFunctions().keySet()) {
			p.getFunctions().get(id).accept(this, 0);
			System.out.print("\n");
		}
	}
	
	@Override
	public void visit(BlockStatement v, Integer indent) {
		print(indent, "START BLOCK");
		
		for(Statement s : v.getStmts()) {
			if(s instanceof Expression)
				print(indent + 1, s.toString());
			else
				s.accept(this, indent + 1);
		}
		
		print(indent, "END BLOCK");
	}

	@Override
	public void visit(IfStatement ifStmt, Integer indent) {
		print(indent, "IF " + ifStmt.getCondition());
		
		Statement thenBody = ifStmt.getThenStmt();
		if(thenBody instanceof Expression)
			print(indent + 1, thenBody.toString());
		else
			thenBody.accept(this, indent + 1);
		
		if(ifStmt.getElseStmt() != null) {
			print(indent, "ELSE");
			Statement elseBody = ifStmt.getElseStmt();
			if(elseBody instanceof Expression)
				print(indent + 1, elseBody.toString());
			else
				elseBody.accept(this, indent + 1);
		}
	}

	@Override
	public void visit(WhileStatement whileStmt, Integer indent) {
		print(indent, "WHILE " + whileStmt.getCondition());
		
		Statement body = whileStmt.getBody();
		if(body instanceof Expression)
			print(indent + 1, body.toString());
		else
			body.accept(this, indent + 1);
	}

	@Override
	public void visit(ForStatement forStmt, Integer indent) {
		String exp1 = "";
		if(forStmt.getInit() != null)
			exp1 = forStmt.getInit().toString();
		
		String exp2 = "";
		if(forStmt.getCond() != null)
			exp2 = forStmt.getCond().toString();
		
		String exp3 = "";
		if(forStmt.getAct() != null) 
			exp3 = forStmt.getAct().toString();
		
		print(indent, "FOR (" + exp1 + " ; " + exp2 + " ; " + exp3 + ")");
		
		Statement body = forStmt.getBody();
		if(body instanceof Expression)
			print(indent + 1, body.toString());
		else
			body.accept(this, indent + 1);
	}
	
	@Override
	public void visit(PrintStatement p, Integer indent) {
		print(indent, (p.isNweLine() ? "PRINTLN " : "PRINT ") + p.getExpression());
	}
	
	@Override
	public void visit(ReturnStatement r, Integer indent) {
		print(indent, "RETURN " + (r.getExpression() == null ? "" : r.getExpression().toString()));
	}
	
	@Override
	public void visit(VarDecl decl, Integer indent) {
		if(decl.getInitializer() != null)
			print(indent, decl.getType() + " " + decl.getInitializer());
		else
			print(indent, decl.getType() + " " + decl.getId().getVal());
	}
	
	@Override
	public void visit(ContinueStatement c, Integer arg) {
		print(arg, "CONTINUE");
	}
	
	@Override
	public void visit(BreakStatement b, Integer arg) {
		print(arg, "BREAK");
	}
	
	@Override
	public void visit(ArrayDecl a, Integer indent) {
		String idDim = a.getId().getVal();
		for(Expression e : a.getDimensions()) {
			idDim += "[" + e + "]";
		}
		print(indent, a.getType() + " " + idDim);
	}
	
	@Override
	public void visit(FuncDecl d, Integer indent) {
		String func = d.getType() + " " + d.getId().getVal() + "(";
		
		for(int i = 0; i < d.getFormalArgs().length; i++) {
			func += d.getFormalArgs()[i];
			if(i < d.getFormalArgs().length - 1) func += ", ";
		}
		
		func += ")";
		print(indent, func);
		d.getBody().accept(this, indent + 1);
	}
	
	private String indent(int i, String s) {
		String indent = "";
		while(i-- > 0) {
			indent += tabs;
		}
		return indent + s;
	}
	
	private void print(int indent, String s) {
		System.out.println(indent(indent, s));
	}
}
