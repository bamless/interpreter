package com.bamless.interpreter.codegen;

public class Opcode {
	public static int HALT = 0;       // stop program
	public static int ADD_I32 = 1;    // int add
	public static int SUB_I32 = 2;    // int sub
	public static int MUL_I32 = 3;    // int mul
	public static int DIV_I32 = 4;
	public static int MOD_I32 = 5;
	public static int LT_I32 = 6;     // int less than
	public static int LE_I32 = 7;
	public static int GT_I32 = 8;
	public static int GE_I32 = 9;
	public static int EQ_I32 = 10;     // int equal
	public static int NEQ_I32 = 11;
	public static int JMP = 12;        // branch
	public static int JMPT = 13;       // branch if true
	public static int JMPF = 14;       // branch if false
	public static int CONST_I32 = 15;  // push constant integer
	public static int LOAD = 16;       // load from local
	public static int GLOAD = 17;      // load from global
	public static int STORE = 18;      // store in local
	public static int GSTORE = 19;     // store in global memory
	public static int PRINT = 20;      // print value on top of the stack
	public static int POP = 21;        // throw away top of the stack
	public static int CALL = 22;       // call procedure
	public static int GETARG = 23;
	public static int RET = 24;   // return from procedure

	private Opcode() {
	}
}
