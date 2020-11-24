package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.natives.Native;
import java.util.Map;

/**
 * Facade class that takes an AST as input ad applies all necessary semantic passes to it
 * @author fabrizio
 *
 */
public class SemanticAnalyzer {
	private SemanticChecker vc; 
	private ReturnChecker rc;
	private TypeChecker tc;
	private LoopBreakingChecker lc;
	private NativeCallsChecker nc;
	
	public SemanticAnalyzer(Map<String, Native<?>> natives) {
		rc = new ReturnChecker();
		vc = new SemanticChecker();
		tc = new TypeChecker(natives);
		lc = new LoopBreakingChecker();
		nc = new NativeCallsChecker(natives);
	}

	public void analyze(ASTNode root) {
		//check for native function calls
		root.accept(nc, null);
		//check for undeclared/uninitialized var
		root.accept(vc, null);
		//check that functions with return type actually return
		root.accept(rc, null);
		//type checking
		root.accept(tc, null);
		//check that breaks and continues have a surrounding loop
		root.accept(lc, null);
	}
	
}
