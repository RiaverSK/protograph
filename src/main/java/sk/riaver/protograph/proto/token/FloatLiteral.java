package sk.riaver.protograph.proto.token;

public class FloatLiteral implements Token {
	
	private String number;
	
	public FloatLiteral(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return this.number;
	}
	
	public TokenType getType() {
		return TokenType.FLOAT_LITERAL;
	}
	
	public String getValue() {
		return this.getNumber();
	}

}
