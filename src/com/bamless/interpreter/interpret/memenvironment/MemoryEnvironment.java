package com.bamless.interpreter.interpret.memenvironment;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;
import com.bamless.interpreter.interpret.Interpreter;

public class MemoryEnvironment {
	private Deque<Frame> environmet;
	
	private Interpreter interpreter;
	
	public MemoryEnvironment(Interpreter interpreter) {
		environmet = new ArrayDeque<Frame>();
		
		this.interpreter = interpreter;
	}
	
	public void pushStackFrame() {
		environmet.push(new Frame());
	}
	
	public void popStackFrame() {
		environmet.pop();
	}
	
	public Frame getCurrentFrame() {
		return environmet.peek();
	}
	
	public class Frame {
		private VarSetter setter;
		private VarRetriever retriever;
		
		private HashMap<String, Object> mem;
		private Object returnRegister;
		
		public Frame() {
			mem = new HashMap<>();
			setter = new VarSetter();
			retriever = new VarRetriever();
		}
		
		public <T> void define(Identifier id, T val) {
			if(mem.get(id.getVal()) != null)
				throw new IllegalArgumentException("Identifier " + id + " is already defined");
			mem.put(id.getVal(), val);
		}
		
		public <T> void set(Lvalue var, T val) {
			var.accept(setter, val);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T retrieve(Lvalue var) {
			return (T) var.accept(retriever, null);
		}
		
		public <T> void setReturnRegister(T val) {
			returnRegister = val;
		}
		
		public Object getReturnRegister() {
			return returnRegister;
		}
		
		private class VarSetter extends VoidVisitorAdapter<Object> {
			@Override
			public void visit(VarLiteral v, Object val) {
				mem.put(v.getId().getVal(), val);
			}
			
			@Override
			public void visit(ArrayAccess a, Object arg) {
				Array l = a.getLvalue().accept(interpreter.getArrayExpInterpreter(), getCurrentFrame());
				
				try {
					l.set(a.getIndex().accept(interpreter.getArithmeticExpInterpreter(), getCurrentFrame()).intValue(), arg);
				} catch(ArrayIndexOutOfBoundsException e) {
					throw new ArrayIndexOutOfBoundsException(a.getPosition() + " " + a + ": " + e.getMessage());
				}
			}
		}
		
		private class VarRetriever extends VisitorAdapter<Object, Void> {
			@Override
			public Object visit(VarLiteral v, Void arg) {
				return mem.get(v.getId().getVal());
			}
			
			@Override
			public Object visit(ArrayAccess a, Void arg) {
				Array array = a.getLvalue().accept(interpreter.getArrayExpInterpreter(), getCurrentFrame());
				
				Object o = null;
				try {
					o = array.get(a.getIndex().accept(interpreter.getArithmeticExpInterpreter(), getCurrentFrame()).intValue());
				} catch(ArrayIndexOutOfBoundsException e) {
					throw new ArrayIndexOutOfBoundsException(a.getPosition() + " " + a + ": " + e.getMessage());
				}
				
				return o;
			}
		}
	}

}
