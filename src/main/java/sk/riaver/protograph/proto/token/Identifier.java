package sk.riaver.protograph.proto.token;

import sk.riaver.protograph.proto.ProtoTokenizer;

public class Identifier implements Token {
	
	private String name;
	private KeywordEnum key;
	
	public Identifier(String name) {
		this.name = name;
		if (ProtoTokenizer.KEYWORDS.contains(name)) {
			this.key = KeywordEnum.valueOf(name.toUpperCase());
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public KeywordEnum getKey() {
		return this.key;
	}
	public String getValue() {
		return this.getName();
	}
	
	public TokenType getType() {
		return TokenType.IDENTIFIER;
	}

}
