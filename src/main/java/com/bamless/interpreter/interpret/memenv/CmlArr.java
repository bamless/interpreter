package com.bamless.interpreter.interpret.memenv;

import java.util.LinkedList;

import com.bamless.interpreter.ast.type.ArrayType;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.type.Type.TypeID;

public class CmlArr implements Cloneable {
	private Object[] arr;

	public CmlArr(LinkedList<Integer> dimensions, Type t) {
		arr = new Object[dimensions.poll()];

		if(dimensions.size() == 0) {
			init(t);
		} else {
			for(int i = 0; i < arr.length; i++) {
				arr[i] = new CmlArr(dimensions, ((ArrayType) t).getInternalType());
			}
		}

		dimensions.push(arr.length);
	}

	private void init(Type t) {
		for(int i = 0; i < arr.length; i++) {
			Object o = null;
			if(t.getId() == TypeID.BOOLEAN)
				o = false;
			if(t.getId() == TypeID.INT)
				o = 0;
			if(t.getId() == TypeID.FLOAT)
				o = 0.0f;
			arr[i] = o;
		}
	}

	public Object get(int i) {
		return arr[i];
	}

	public void set(int i, Object o) {
		arr[i] = o;
	}

	public int getLength() {
		return arr.length;
	}

	@Override
	public String toString() {
		String s = "{";
		for(int i = 0; i < arr.length; i++) {
			s += arr[i];
			if(i < arr.length - 1)
				s += ", ";
		}
		return s + "}";
	}

}
