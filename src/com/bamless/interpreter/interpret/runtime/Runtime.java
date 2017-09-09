package com.bamless.interpreter.interpret.runtime;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;
import com.bamless.interpreter.interpret.ArithmeticExpInterpreter;
import com.bamless.interpreter.semantic.SymbolTable;

public class Runtime {
	private SymbolTable<Object> environmet;
	
	private VarRetriever varRetriever;
	private VarSetter varSetter;
	
	private ArithmeticExpInterpreter ai;
	
	public Runtime() {
		environmet = new SymbolTable<>();
		varRetriever = new VarRetriever();
		varSetter = new VarSetter();
		ai = new ArithmeticExpInterpreter(this);
	}
	
	public void define(Identifier id, Object val) {
		environmet.define(id.getVal(), val);
	}
	
	public Object retrieve(Identifier id) {
		return environmet.lookup(id.getVal());
	}
	
	public void set(Identifier id, Object val) {
		environmet.set(id.getVal(), val);
	}
	
	public void set(Lvalue var, Object val) {
		var.accept(varSetter, val);
	}
	
	public Object retrieve(Lvalue var) {
		return var.accept(varRetriever, null);
	}
	
	public void enterScope() {
		environmet.enterScope();
	}
	
	public void exitScope() {
		environmet.exitScope();
	}
	
	private class VarSetter extends VoidVisitorAdapter<Object> {
		
		@Override
		public void visit(VarLiteral v, Object val) {
			environmet.set(v.getId().getVal(), val);
		}
		
		@Override
		public void visit(ArrayAccess a, Object arg) {
			Array l = (Array) a.getLvalue().accept(varRetriever, null);
			l.set(a.getIndex().accept(ai, null).intValue(), arg);
		}
		
	}
	
	private class VarRetriever extends VisitorAdapter<Object, Void> {
		@Override
		public Object visit(VarLiteral v, Void arg) {
			return environmet.lookup(v.getId().getVal());
		}
		
		@Override
		public Object visit(ArrayAccess a, Void arg) {
			Array array = (Array) a.getLvalue().accept(this, arg);
			return array.get(a.getIndex().accept(ai, arg).intValue());
		}
	}

}
