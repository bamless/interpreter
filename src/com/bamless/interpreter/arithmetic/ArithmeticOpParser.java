  package com.bamless.interpreter.arithmetic;

import com.bamless.interpreter.lex.Lexer;
import com.bamless.interpreter.lex.Token;
import com.bamless.interpreter.parser.ParseException;

/**
 * Simple predictive parser for an LL(1) arithmetic expression
 * grammar. The grammar associated with each method is reported
 * as a javadoc comment on the beginning of the method itself 
 * in EBNF form.
 * 
 * The parser respect the left associativity of arithmetic expressions
 * 
 * @author fabrizio
 *
 */
public class ArithmeticOpParser {
	public final static String[] types = new String[] {
			"CONST",		"([0-9]+(\\.[0-9]*)?)",
			"OPERATOR",		"(\\+|-|/|\\*)",
			"PAREN",		"(\\(|\\))",
	}; 
	private Lexer lex = new Lexer(types, true);
	
	public ArithmeticOperation parse(String src) {
		lex.tokenize(src);
		
		ArithmeticOperation parsed = parseExpression();
		if(!lex.isFinished()) {
			error("unexpected token \"%s\"", lex.peek().getValue());
		}
		return parsed;
	}

	/**
	 * Expression -> Term ((+|-) Term)*
	 */
	private ArithmeticOperation parseExpression() {
		ArithmeticOperation operation = parseTerm();

		Token op;
		while((op = lex.peek()).getValue().equals("+") || op.getValue().equals("-")) {
			lex.consume();
			ArithmeticOperation op2 = parseTerm();
			
			switch(op.getValue()) {
			case "+":
				operation = new AdditionOperation(operation, op2);
				break;
			case "-":
				operation = new SubtractionOperation(operation, op2);
				break;
			}
			
		}
		return operation;
	}
	
	/**
	 * Term -> Factor ((*|/) Factor)*
	 */
	private ArithmeticOperation parseTerm() {
		ArithmeticOperation operation = parseFactor();
		
		Token op;
		while((op = lex.peek()).getValue().equals("*") || op.getValue().equals("/")) {
			lex.consume(); //consume the token
			ArithmeticOperation op2 = parseFactor();
			
			switch(op.getValue()) {
			case "*":
				operation = new MultiplicationOperation(operation, op2);
				break;
			case "/":
				operation = new DivisionOperation(operation, op2);
				break;
			}
			
		}
		return operation;
	}
	
	/**
	 * Factor -> -Factor
	 * 			|CONST
	 * 			|(Expression)
	 */
	private ArithmeticOperation parseFactor() {
		Token tok = lex.next();
		
		if(tok.getValue().equals("-")) {
			ArithmeticOperation factor = parseFactor();
			return new ConstantOperation(-factor.exec());
		}
		
		ArithmeticOperation factor = null;
		if(tok.getValue().equals("(")) {
			//subexpression
			factor = parseExpression();
			require(")");
		} else if(tok.getType().equals("CONST")){
			//try to parse the number
			factor = new ConstantOperation(Double.parseDouble(tok.getValue()));
		} else {
			error("expected number or nested expression but instead found \"%s\"", tok.getValue());
		}
		
		return factor;
	}
	
	private void require(String value) {
		Token next = lex.next();
		if(!next.getValue().equals(value)) {
			error("expected \"%s\" but instead found \"%s\"", value, next.getValue());
		}
	}
	
	private void error(String format, Object... args) {
		//lex.curr() could return null. In that case just omit the "near or at" part of the error
		String tokenErr = (lex.curr() != null ? " near or at token \"" + lex.curr().getValue() + "\"" : "");
		throw new ParseException(String.format("Syntax error" + tokenErr + ": " + format, args));
	}
	
}
