package com.bamless.interpreter.codegen;

public class Opcode {
	public static final int HALT = 0;       // stop program
	public static final int ADD_I32 = 1;    // int add
	public static final int SUB_I32 = 2;    // int sub
	public static final int MUL_I32 = 3;    // int mul
	public static final int DIV_I32 = 4;
	public static final int MOD_I32 = 5;
	public static final int LT_I32 = 6;     // int less than
	public static final int LE_I32 = 7;
	public static final int GT_I32 = 8;
	public static final int GE_I32 = 9;
	public static final int EQ_I32 = 10;     // int equal
	public static final int NEQ_I32 = 11;
	public static final int JMP = 12;        // branch
	public static final int JMPT = 13;       // branch if true
	public static final int JMPF = 14;       // branch if false
	public static final int CONST_I32 = 15;  // push constant integer
	public static final int LOAD = 16;       // load from local
	public static final int GLOAD = 17;      // load from global
	public static final int STORE = 18;      // store in local
	public static final int GSTORE = 19;     // store in global memory
	public static final int PRINT = 20;      // print value on top of the stack
	public static final int POP = 21;        // throw away top of the stack
	public static final int CALL = 22;       // call procedure
	public static final int GETARG = 23;
	public static final int RET = 24;   // return from procedure
	public static final int DUP = 25;
	
	private Opcode() {
	}
}
