package sk.riaver.protograph.proto.token;

public class StringLiteral implements Token {
	
	private String text;
	
	public StringLiteral(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public TokenType getType() {
		return TokenType.STRING_LITERAL;
	}
	
	public String getValue() {
		return this.getText();
	}

}
