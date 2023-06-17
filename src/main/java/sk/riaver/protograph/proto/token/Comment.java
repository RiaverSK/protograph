package sk.riaver.protograph.proto.token;

public class Comment implements Token {
	
	private String text;
	private boolean endOfLine;
	
	public Comment(String text, boolean endOfLine) {
		this.text = text;
		this.endOfLine = endOfLine;
	}
	
	public String getText() {
		return this.text;
	}
	
	public boolean isEndOfLine() {
		return this.endOfLine;
	}
	
	public TokenType getType() {
		return TokenType.COMMENT;
	}
	
	public String getValue() {
		return this.getText();
	}

}
