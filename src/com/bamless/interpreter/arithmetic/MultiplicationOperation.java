package com.bamless.interpreter.arithmetic;

public class MultiplicationOperation implements ArithmeticOperation {
	private ArithmeticOperation op1;
	private ArithmeticOperation op2;
	
	public MultiplicationOperation(ArithmeticOperation op1, ArithmeticOperation op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	@Override
	public double exec() {
		return op1.exec() * op2.exec();
	}
	
	@Override
	public String toString() {
		return "(" + op1.toString() + "*" + op2.toString() + ")";
	}
}
