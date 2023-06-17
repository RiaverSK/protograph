package sk.riaver.protograph.proto.token;

public class Symbol implements Token {
	
	private char character;
	private SymbolEnum symb;
	
	public Symbol(char character) {
		this.character = character;
		this.symb = SymbolEnum.getByChar(character);
	}
	
	public char getCharacter() {
		return this.character;
	}
	
	public SymbolEnum getSymbol() {
		return this.symb;
	}
	
	public TokenType getType() {
		return TokenType.SYMBOL;
	}
	
	public String getValue() {
		return String.valueOf(this.character);
	}

}
