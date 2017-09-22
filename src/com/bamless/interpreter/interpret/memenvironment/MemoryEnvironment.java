package com.bamless.interpreter.interpret.memenvironment;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.expression.ArrayAccess;
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
	
	public <T> void define(Identifier id, T val) {
		environmet.define(id.getVal(), val);
	}
	
	public <T> void set(Lvalue var, T val) {
		var.accept(varSetter, val);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T retrieve(Lvalue var) {
		return (T) var.accept(varRetriever, null);
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
			Array l = a.getLvalue().accept(interpreter.getArrayExpInterpreter(), null);
			
			try {
				l.set(a.getIndex().accept(interpreter.getArithmeticExpInterpreter(), null).intValue(), arg);
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException(a.getPosition() + " " + a + ": " + e.getMessage());
			}
		}
	}
	
	private class VarRetriever extends VisitorAdapter<Object, Void> {
		@Override
		public Object visit(VarLiteral v, Void arg) {
			return environmet.lookup(v.getId().getVal());
		}
		
		@Override
		public Object visit(ArrayAccess a, Void arg) {
			Array array = a.getLvalue().accept(interpreter.getArrayExpInterpreter(), null);
			
			Object o = null;
			try {
				o = array.get(a.getIndex().accept(interpreter.getArithmeticExpInterpreter(), arg).intValue());
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException(a.getPosition() + " " + a + ": " + e.getMessage());
			}
			
			return o;
		}
	}

}
