package sk.riaver.protograph.proto.token;

public class IntLiteral implements Token {
	
	private String number;
	
	public IntLiteral(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return this.number;
	}
	
	public TokenType getType() {
		return TokenType.INT_LITERAL;
	}
	
	public String getValue() {
		return this.getNumber();
	}

}
