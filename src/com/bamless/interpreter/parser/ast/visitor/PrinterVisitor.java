package com.bamless.interpreter.parser.ast.visitor;

import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.Statement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;

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
	public void visit(BlockStatement v, Integer indent) {
		print(indent, "START BLOCK");
		
		for(Statement s : v) {
			s.accept(this, indent + 1);
		}
		
		print(indent, "END BLOCK");
	}

	@Override
	public void visit(IfStatement v, Integer arg) {
		print(arg, "IF " + v.getCondition());
		v.getThenStmt().accept(this, arg + 1);
		
		if(v.getElseStmt() != null) {
			print(arg, "ELSE");
			v.getElseStmt().accept(this, arg + 1);
		}
	}

	@Override
	public void visit(WhileStatement v, Integer arg) {
		print(arg, "WHILE " + v.getCondition());
		v.getBody().accept(this, arg + 1);
	}

	@Override
	public void visit(VarDecl v, Integer arg) {
		print(arg, v.getType() + " " + v.getId().getId());
	}
	
	private String indent(int i, String s) {
		String indent = "";
		while(i-- > 0) {
			indent += tabs;
		}
		return indent + s;
	}
	
	@Override
	public void visit(Expression v, Integer arg) {
		print(arg, v.toString());
	}
	
	private void print(int indent, String s) {
		System.out.println(indent(indent, s));
	}
}
