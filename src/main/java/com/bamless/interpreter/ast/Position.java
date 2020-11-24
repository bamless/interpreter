package com.bamless.interpreter.ast;

public class Position implements Comparable<Position> {
	public final int line;
	public final int col;

	public Position(int line, int col) {
		if(line < 0 || col < 0) {
			throw new IllegalArgumentException("Invalid line or col arg");
		}
		this.line = line;
		this.col = col;
	}

	public Position withCol(int col) {
		return new Position(this.line, col);
	}

	public Position withLine(int line) {
		return new Position(line, this.col);
	}

	public boolean isValid() {
		return col > 0 && line > 0;
	}

	@Override
	public int hashCode() {
		return 31 * line + col;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Position other = (Position) o;
		return other.line == line && other.col == col;
	}

	@Override
	public String toString() {
		return "(" + line + "," + col + ")";
	}

	@Override
	public int compareTo(Position o) {
		int res = Integer.compare(o.line, line);
		if(res == 0)
			res = Integer.compare(o.col, col);
		return res;
	}
}
