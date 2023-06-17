package sk.riaver.protograph.proto.token;

import sk.riaver.protograph.proto.ProtoParserException;

public enum SymbolEnum {
	
	SEMICOLON(';'), COLON(':'), LEFT_PAREN('('), LEFT_BRACKET('['),
	COMMA(','), EQUALS('='), RIGHT_PAREN(')'), RIGHT_BRACKET(']'),
	DOT('.'), MINUS('-'), LEFT_BRACE('{'), LEFT_ANGLE('<'),
	SLASH('/'), PLUS('+'), RIGHT_BRACE('}'), RIGHT_ANGLE('>');
	
	private final char symbol;
	SymbolEnum(char symbol) {
		this.symbol = symbol;
	}
	
	public char getSymbol() {
		return this.symbol;
	}
	
	public static SymbolEnum getByChar(char chr) {
		for (SymbolEnum s : SymbolEnum.values()) {
			if (s.getSymbol() == chr) {
				return s;
			}
		}
		throw new ProtoParserException("Invalid character");
	}

}
