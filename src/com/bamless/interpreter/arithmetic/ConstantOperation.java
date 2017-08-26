package com.bamless.interpreter.arithmetic;

public class ConstantOperation implements ArithmeticOperation {

	private double value;
	
	public ConstantOperation(double value) {
		this.value = value;
	}
	
	@Override
	public double exec() {
		return value;
	}

	@Override
	public String toString() {
		return value+"";
	}
}
