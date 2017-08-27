package com.bamless.interpreter.parser;

import static com.bamless.interpreter.parser.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation.DIV;
import static com.bamless.interpreter.parser.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation.MINUS;
import static com.bamless.interpreter.parser.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation.MOD;
import static com.bamless.interpreter.parser.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation.MULT;
import static com.bamless.interpreter.parser.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation.PLUS;
import static com.bamless.interpreter.parser.ast.expression.EqualityExpression.EqualityOperation.EQ;
import static com.bamless.interpreter.parser.ast.expression.EqualityExpression.EqualityOperation.NEQ;
import static com.bamless.interpreter.parser.ast.expression.LogicalExpression.BooleanBinOperation.AND;
import static com.bamless.interpreter.parser.ast.expression.LogicalExpression.BooleanBinOperation.OR;
import static com.bamless.interpreter.parser.ast.expression.RelationalExpression.RelationalOperation.GE;
import static com.bamless.interpreter.parser.ast.expression.RelationalExpression.RelationalOperation.GT;
import static com.bamless.interpreter.parser.ast.expression.RelationalExpression.RelationalOperation.LE;
import static com.bamless.interpreter.parser.ast.expression.RelationalExpression.RelationalOperation.LT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.lex.Lexer;
import com.bamless.interpreter.lex.Token;
import com.bamless.interpreter.parser.ast.ASTNode;
import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.parser.ast.expression.BooleanLiteral;
import com.bamless.interpreter.parser.ast.expression.EqualityExpression;
import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.expression.FloatLiteral;
import com.bamless.interpreter.parser.ast.expression.IntegerLiteral;
import com.bamless.interpreter.parser.ast.expression.LogicalExpression;
import com.bamless.interpreter.parser.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.parser.ast.expression.RelationalExpression;
import com.bamless.interpreter.parser.ast.expression.VarLiteral;
import com.bamless.interpreter.parser.ast.statements.AssignStatement;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.Statement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;
import com.bamless.interpreter.parser.ast.type.Type;

public class ASTParser {
	private final static String LEX_FILE = "/lexical-spec.lex";
	
	private Lexer lex;
	
	public ASTParser() {
		lex = new Lexer(ClassLoader.class.getResourceAsStream(LEX_FILE), true);
	}
	
	public ASTNode parse(File f) throws FileNotFoundException, IOException {
		lex.tokenize(f);
		return parse();
	}
	
	public ASTNode parse(InputStream src) throws IOException {
		lex.tokenize(src);
		return parse();
	}
	
	public ASTNode parse(String src) {
		lex.tokenize(src);
		return parse();
	}
	
	private ASTNode parse() {
		ASTNode root = block();
		
		if(!lex.isFinished()) {
			throw new ParseException(String.format("Syntax error at %s: unexpected token \"%s\"", 
					lex.peek().getPosition(), lex.peek().getValue()));
		}
		
		return root;
	}
	
	private Statement statement() {
		switch(lex.peek().getType()) {
			case "INT":
			case "FLOAT":
			case "BOOLEAN":
				return varDecl();
			case "IDENTIFIER":
				return assignStmt();
			case "IF":
				return ifStmt();
			case "WHILE":
				return whileStmt();
			case "{":
				return block();
			default:
				error("expected statement but instead found \"%s\"", lex.peek().getValue());
				return null;
		}
	}
	
	private Statement block() {
		Position start = require("{").getPosition();
		
		List<Statement> statements = new ArrayList<>();
		while(!lex.peek().getType().equals("}")) {
			statements.add(statement());
		}
		
		require("}");
		
		return new BlockStatement(statements, start);
	}

	private Statement varDecl() {
		Token typeTok = lex.next();
		Token idTok = require("IDENTIFIER");
		require(";");
		
		Type t = Type.valueOf(typeTok.getType());
		Identifier id = new Identifier(idTok.getPosition(), idTok.getValue());
		
		return new VarDecl(typeTok.getPosition(), t, id);
	}

	private Statement assignStmt() {
		Token idTok = require("IDENTIFIER");

		require("=");
		
		Expression e = expression();
		
		require(";");
		
		Identifier id = new Identifier(idTok.getPosition(), idTok.getValue());
		return new AssignStatement(idTok.getPosition(), id, e);
	}
	
	private Statement ifStmt() {
		Position start = require("IF").getPosition();
		require("(");
		
		Expression e = expression();
		
		require(")");
		
		Statement thenBody = statement();
		Statement elseBody = null;
		
		if(lex.peek().getType().equals("ELSE")) {
			lex.next();
			elseBody = statement();
		}
		
		return new IfStatement(e, thenBody, elseBody, start);
	}
	
	private Statement whileStmt() {
		Position start = require("WHILE").getPosition();
		require("(");
		
		Expression cond = expression();
		
		require(")");
		
		Statement body = statement();
		return new WhileStatement(cond, body, start);
	}
	
	private Expression expression() {
		Expression left = equalityExpr();
		
		Token op;
		while((op = lex.peek()).getType().equals("OR_OP") || op.getType().equals("AND_OP")) {
			lex.next();
			Expression right = equalityExpr();
			
			switch(op.getType()) {
			case "OR_OP":
				left = new LogicalExpression(OR, left, right, left.getPosition());
				break;
			case "AND_OP":
				left = new LogicalExpression(AND, left, right, left.getPosition());
				break;
			}
		}
		return left;
	}
	
	private Expression equalityExpr() {
		Expression left = relationalExpr();
		
		Token op;
		while((op = lex.peek()).getType().equals("EQ_OP") || op.getType().equals("NEQ_OP")) {
			lex.next();
			Expression right = relationalExpr();
			
			switch(op.getType()) {
			case "EQ_OP":
				left = new EqualityExpression(EQ, left, right, left.getPosition());
				break;
			case "NEQ_OP":
				left = new EqualityExpression(NEQ, left, right, left.getPosition());
				break;
			}
		}
		return left;
	}
	
	private Expression relationalExpr() {
		Expression left = arithmeticExpr();
		
		Token op;
		while((op = lex.peek()).getType().equals("<") || op.getType().equals(">") ||
				op.getType().equals("GE_OP") || op.getType().equals("LE_OP")) {
			lex.next();
			Expression right = arithmeticExpr();
			
			switch(op.getType()) {
			case "<":
				left = new RelationalExpression(LT, left, right, left.getPosition());
				break;
			case ">":
				left = new RelationalExpression(GT, left, right, left.getPosition());
				break;
			case "LE_OP":
				left = new RelationalExpression(LE, left, right, left.getPosition());
				break;
			case "GE_OP":
				left = new RelationalExpression(GE, left, right, left.getPosition());
				break;
			}
		}
		return left;
	}
	
	private Expression arithmeticExpr() {
		Expression left = term();
		
		Token op;
		while((op = lex.peek()).getType().equals("+") || op.getType().equals("-")) {
			lex.next();
			Expression right = term();
			
			switch(op.getType()) {
			case "+":
				left = new ArithmeticBinExpression(PLUS, left, right, left.getPosition());
				break;
			case "-":
				left = new ArithmeticBinExpression(MINUS, left, right, left.getPosition());
				break;
			}
		}
		return left;
	}
	
	private Expression term() {
		Expression left = factor();
		
		Token op;
		while((op = lex.peek()).getType().equals("/") || op.getType().equals("*") ||
				op.getType().equals("%")) {
			lex.next();
			Expression right = factor();
			
			switch(op.getType()) {
			case "/":
				left = new ArithmeticBinExpression(DIV, left, right, left.getPosition());
				break;
			case "*":
				left = new ArithmeticBinExpression(MULT, left, right, left.getPosition());
				break;
			case "%":
				left = new ArithmeticBinExpression(MOD, left, right, left.getPosition());
				break;
			}
		}
		return left;
	}

	private Expression factor() {
		if(lex.peek().getType().equals("!")) {
			require("!");
			Expression e = factor();
			return new LogicalNotExpression(e, e.getPosition());
		}
		
		return literal();
	}
	
	private Expression literal() {
		Token litTok = lex.next();
		
		switch(litTok.getType()) {
		case "INT_CONST":
			return new IntegerLiteral(litTok.getPosition(), Integer.parseInt(litTok.getValue()));
		case "FLOAT_CONST":
			return new FloatLiteral(litTok.getPosition(), Float.parseFloat(litTok.getValue()));
		case "BOOL_CONST":
			return new BooleanLiteral(litTok.getPosition(), Boolean.parseBoolean(litTok.getValue()));
		case "IDENTIFIER":
			return new VarLiteral(new Identifier(litTok.getPosition(), litTok.getValue()), litTok.getPosition());
		case "(":
			Expression e = expression();
			require(")");
			return e;
		default:
			error("expected an expression literal but instead found \"%s\"", lex.curr().getValue());
			return null;
		}
	}
	
	private Token require(String tokType) {
		Token next = lex.next();
		if(!next.getType().equals(tokType)) {
			error("expected \"%s\" but instead found \"%s\"", tokType, next.getValue());
		}
		return next;
	}
	
	private void error(String format, Object... args) {
		//lex.curr() could return null. In that case just omit the "near or at" part of the error
		String tokenErr = "";
		Position pos = new Position(0, 0);
		if(lex.curr() != null) {
			tokenErr = "near or at token \"" + lex.curr().getValue() + "\" ";
			pos = lex.curr().getPosition();
		}
		throw new ParseException(String.format("Syntax error " + tokenErr + pos + ": " + format, args));
	}

}
