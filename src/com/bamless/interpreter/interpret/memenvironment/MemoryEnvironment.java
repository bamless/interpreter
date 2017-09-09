package com.bamless.interpreter.interpret.memenvironment;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.semantic.SymbolTable;

public class MemoryEnvironment {
	private SymbolTable<Object> environmet;
	
	private VarRetriever varRetriever;
	private VarSetter varSetter;
	
	private Interpreter interpreter;
	
	public MemoryEnvironment(Interpreter interpreter) {
		environmet = new SymbolTable<>();
		varRetriever = new VarRetriever();
		varSetter = new VarSetter();
		
		this.interpreter = interpreter;
	}
	
	public void define(Identifier id, Object val) {
		environmet.define(id.getVal(), val);
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
			l.set(a.getIndex().accept(interpreter.getArithmeticExpInterpreter(), null).intValue(), arg);
		}
	}
	
	private class VarRetriever extends VisitorAdapter<Object, Void> {
		@Override
		public Object visit(VarLiteral v, Void arg) {
			return environmet.lookup(v.getId().getVal());
		}
		
		@Override
		public Object visit(AssignExpression e, Void arg) {
			Object o = e.getExpression().accept(this, arg);
			set((Lvalue) e.getLvalue(), o);
			return o;
		}
		
		@Override
		public Object visit(ArrayAccess a, Void arg) {
			Array array = (Array) a.getLvalue().accept(this, arg);
			return array.get(a.getIndex().accept(interpreter.getArithmeticExpInterpreter(), arg).intValue());
		}
	}

}
