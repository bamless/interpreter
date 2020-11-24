package com.bamless.interpreter.interpret.memenv;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.visitor.VisitorAdapter;
import com.bamless.interpreter.visitor.VoidVisitorAdapter;

public class MemoryEnvironment {
	private Interpreter interpreter;
	private Deque<Frame> environmet;
	
	public MemoryEnvironment(Interpreter interpreter) {
		this.interpreter = interpreter;
		environmet = new ArrayDeque<Frame>();
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
		
		private Frame() {
			mem = new HashMap<>();
			setter = new VarSetter();
			retriever = new VarRetriever();
		}
		
		public void define(Identifier id, Object val) {
			mem.put(id.getVal(), val);
		}
		
		public void set(Lvalue var, Object val) {
			var.accept(setter, val);
		}
		
		public Object retrieve(Lvalue var) {
			return var.accept(retriever, null);
		}
		
		private class VarSetter extends VoidVisitorAdapter<Object> {
			@Override
			public void visit(VarLiteral v, Object val) {
				mem.put(v.getId().getVal(), val);
			}
			
			@Override
			public void visit(ArrayAccess a, Object arg) {
				CmlArr l = a.getLvalue().accept(interpreter.array(), getCurrentFrame());
				
				try {
					l.set(a.getIndex().accept(interpreter.integer(), getCurrentFrame()), arg);
				} catch(ArrayIndexOutOfBoundsException e) {
					throw new ArrayIndexOutOfBoundsException(a + ": " + e.getMessage());
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
				CmlArr array = a.getLvalue().accept(interpreter.array(), getCurrentFrame());
				
				Object o = null;
				try {
					o = array.get(a.getIndex().accept(interpreter.integer(), getCurrentFrame()));
				} catch(ArrayIndexOutOfBoundsException e) {
					throw new ArrayIndexOutOfBoundsException(a + ": " + e.getMessage());
				}
				
				return o;
			}
		}
	}

}
