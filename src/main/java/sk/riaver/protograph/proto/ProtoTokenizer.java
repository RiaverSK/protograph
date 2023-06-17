package sk.riaver.protograph.proto;

import java.util.Arrays;
import java.util.List;

import sk.riaver.protograph.proto.token.*;

public class ProtoTokenizer {
	
	public static final List<String> KEYWORDS = Arrays.asList("bool","bytes","double","enum","extend","extensions",
			"fixed32","fixed64","float","group","import","inf","int32","int64","map","max","message","oneof","option",
			"optional","package","public","repeated","required","reserved","returns","rpc","service","sfixed32",
			"sfixed64","sint32","sint64","stream","string","syntax","to","uint32","uint64","weak");
	
	public static final List<String> SCALAR_TYPES = Arrays.asList("bool","bytes","double","fixed32","fixed64","float",
			"int32","int64","sfixed32","sfixed64","sint32","sint64","string","uint32","uint64");
	
	private final char[] input;
	
	private int idx;
	private State state;
	private boolean newLine;
	private boolean rewinded;
	private Token previousToken;
	
	public ProtoTokenizer(char[] input) {
		if (input == null) {
			throw new IllegalArgumentException("Input argument is null");
		}
		this.input = input;
		this.idx = 0;
		this.state = State.BEGIN;
		this.newLine = true;
		this.rewinded = false;
	}
	
	public boolean hasMoreTokens() {
		if (this.rewinded) {
			return true;
		}
		if (this.state == State.BEGIN) {
			this.skipWhitespaces();
		}
		if (this.state == State.BEGIN_OF_TOKEN) {
			return true;
		}
		return false;
	}
	
	public void rewind() {
		if (this.rewinded) {
			throw new ProtoParserException("Allredy rewinded");
		}
		if (this.previousToken == null) {
			throw new ProtoParserException("Begin of the file");
		}
		this.rewinded = true;
	}
	
	public Token getNext() {
		if (this.rewinded) {
			this.rewinded = false;
			return this.previousToken;
		}
		if (this.state == State.BEGIN) {
			this.skipWhitespaces();
		}
		if (this.state == State.END_OF_FILE) {
			throw new ProtoParserException("End of file");
		}
		if (this.state != State.BEGIN_OF_TOKEN) {
			throw new ProtoParserException("Unexpected state");
		}
		StringBuilder sb = new StringBuilder();
		TokenType type = this.extractToken(sb);
		Token token = null;
		if (type != null) {
			switch (type) {
			case COMMENT:
				token = new Comment(sb.toString().trim(), !this.newLine);
				break;
			case FLOAT_LITERAL:
				token = new FloatLiteral(sb.toString());
				break;
			case IDENTIFIER:
				token = new Identifier(sb.toString());
				break;
			case INT_LITERAL:
				token = new IntLiteral(sb.toString());
				break;
			case STRING_LITERAL:
				token = new StringLiteral(sb.toString());
				break;
			case SYMBOL:
				token = new Symbol(sb.charAt(0));
				break;
			}
		} else {
			throw new ProtoParserException("Unknown token");
		}
		this.previousToken = token;
		return token;
	}
	
	private boolean isWhitespace(char chr) {
		return " \n\r\t\f\u000B".indexOf(chr) >= 0;
	}
	
	private boolean isLetter(char chr) {
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_".indexOf(chr) >= 0;
	}
	
	private boolean isDecimalDigit(char chr) {
		return "0123456789".indexOf(chr) >= 0;
	}
	
//	private boolean isOctalDigit(char chr) {
//		return "01234567".indexOf(chr) >= 0;
//	}
//	
//	private boolean isHexDigit(char chr) {
//		return "0123456789ABCDEFabcdef".indexOf(chr) >= 0;
//	}
	
	private void skipWhitespaces() {
		while (this.idx < this.input.length && this.isWhitespace(this.input[this.idx])) {
			if (this.input[this.idx] == '\n') {
				this.newLine = true;
			}
			this.idx++;
		}
		if (this.idx >= this.input.length) {
			this.state = State.END_OF_FILE;
		} else {
			this.state = State.BEGIN_OF_TOKEN;
		}
	}
	
	private TokenType extractToken(StringBuilder sb) {
		TokenType type = null;
		do {
			char chr = this.input[this.idx];
			switch (this.state) {
			case BEGIN_OF_TOKEN:
				if (chr == '/') {
					this.idx++;
					this.state = State.BEGIN_OF_TOKEN_WITH_SLASH;
				} else if (chr == '.') {
					this.idx++;
					this.state = State.BEGIN_OF_TOKEN_WITH_DOT;
					this.newLine = false;
				} else if (this.isDecimalDigit(chr)) {
					sb.append(chr);
					this.idx++;
					this.state = State.MIDDLE_OF_NUMBER;
					this.newLine = false;
				} else if (this.isLetter(chr)) {
					sb.append(chr);
					this.idx++;
					this.state = State.MIDDLE_OF_IDENTIFIER;
					this.newLine = false;
				} else if (chr == '"') {
					sb.append(chr);
					this.idx++;
					this.state = State.MIDDLE_OF_DOUBLE_QUOTED_STRING;
					this.newLine = false;
				} else if (chr == '\'') {
					sb.append(chr);
					this.idx++;
					this.state = State.MIDDLE_OF_SINGLE_QUOTED_STRING;
					this.newLine = false;
				} else if (chr == ';' || chr == ',' || chr == ':' || chr == '=' || chr == '-' || chr == '+'
						|| chr == '(' || chr == ')' || chr == '[' || chr == ']' || chr == '{' || chr == '}'
						|| chr == '<' || chr == '>') {
					sb.append(chr);
					this.idx++;
					type = TokenType.SYMBOL;
					this.state = State.BEGIN;
					this.newLine = false;
				}
				break;
			case BEGIN_OF_TOKEN_WITH_SLASH:
				if (chr == '/') {
					sb.append(chr);
					sb.append(chr);
					type = TokenType.COMMENT;
					this.idx++;
					this.state = State.MIDDLE_OF_INLINE_COMMENT;
				} else if (chr == '*') {
					sb.append('/');
					sb.append(chr);
					type = TokenType.COMMENT;
					this.idx++;
					this.state = State.MIDDLE_OF_MULTILINE_COMMENT;
				} else {
					type = TokenType.SYMBOL;
					sb.append('/');
					this.state = State.BEGIN;
				}
				break;
			case MIDDLE_OF_INLINE_COMMENT:
				if (chr == '\n') {
					this.state = State.BEGIN;
				} else if (chr == 0) {
					throw new ProtoParserException("Illegal character in comment");
				} else {
					sb.append(chr);
					this.idx++;
				}
				break;
			case MIDDLE_OF_MULTILINE_COMMENT:
				if (chr == '*') {
					this.idx++;
					this.state = State.END_OF_MULTILINE_COMMENT;
				} else if (chr == 0) {
					throw new ProtoParserException("Illegal character in comment");
				} else {
					sb.append(chr);
					this.idx++;
				}
				break;
			case END_OF_MULTILINE_COMMENT:
				if (chr == '/') {
					sb.append('*');
					sb.append(chr);
					this.idx++;
					this.state = State.BEGIN;
				} else if (chr == '*') {
					sb.append(chr);
					this.idx++;
				} else if (chr == 0) {
					throw new ProtoParserException("Illegal character in comment");
				} else {
					sb.append('*');
					sb.append(chr);
					this.idx++;
					this.state = State.MIDDLE_OF_MULTILINE_COMMENT;
				}
				break;
			case BEGIN_OF_TOKEN_WITH_DOT:
				if (this.isDecimalDigit(chr)) {
					sb.append('.');
					sb.append(chr);
					this.idx++;
					this.state = State.MIDDLE_OF_NUMBER;
				} else {
					type = TokenType.SYMBOL;
					sb.append('.');
					this.state = State.BEGIN;
				}
				break;
			case MIDDLE_OF_NUMBER:
				if (chr == '.' || chr == '+' || chr == '-' || this.isDecimalDigit(chr) || this.isLetter(chr)) {
					sb.append(chr);
					this.idx++;
				} else {
					String numb = sb.toString();
					this.state = State.BEGIN;
					if (numb.matches("\\d+") || numb.matches("0[xX][0-9A-Fa-f]+")) {
						type = TokenType.INT_LITERAL;
					} else {
						type = TokenType.FLOAT_LITERAL;
					}
				}
				break;
			case MIDDLE_OF_SINGLE_QUOTED_STRING:
				if (chr == '\'') {
					sb.append(chr);
					type = TokenType.STRING_LITERAL;
					this.idx++;
					this.state = State.BEGIN;
				} else if (chr == '\\') {
					sb.append(chr);
					this.idx++;
					sb.append(this.input[this.idx]);
					this.idx++;
				} else if (chr == '\n' || chr == 0) {
					throw new ProtoParserException("Illegal character in string literal");
				} else {
					sb.append(chr);
					this.idx++;
				}
				break;
			case MIDDLE_OF_DOUBLE_QUOTED_STRING:
				if (chr == '"') {
					sb.append(chr);
					type = TokenType.STRING_LITERAL;
					this.idx++;
					this.state = State.BEGIN;
				} else if (chr == '\\') {
					sb.append(chr);
					this.idx++;
					sb.append(this.input[this.idx]);
					this.idx++;
				} else if (chr == '\n' || chr == 0) {
					throw new ProtoParserException("Illegal character in string literal");
				} else {
					sb.append(chr);
					this.idx++;
				}
				break;
			case MIDDLE_OF_IDENTIFIER:
				if (this.isLetter(chr) || this.isDecimalDigit(chr)) {
					sb.append(chr);
					this.idx++;
				} else {
					this.state = State.BEGIN;
					type = TokenType.IDENTIFIER;
				}
				break;
			case BEGIN:
			case END_OF_FILE:
				throw new ProtoParserException("Illegal state");
			}
			if (this.idx >= this.input.length && this.state != State.BEGIN) {
				throw new ProtoParserException("End of file");
			}
		} while (this.state != State.BEGIN);
		return type;
	}
	
	private enum State {
		BEGIN,
		BEGIN_OF_TOKEN,
		BEGIN_OF_TOKEN_WITH_DOT,
		BEGIN_OF_TOKEN_WITH_SLASH,
		END_OF_FILE,
		END_OF_MULTILINE_COMMENT,
		MIDDLE_OF_IDENTIFIER,
		MIDDLE_OF_INLINE_COMMENT,
		MIDDLE_OF_NUMBER,
		MIDDLE_OF_MULTILINE_COMMENT,
		MIDDLE_OF_DOUBLE_QUOTED_STRING,
		MIDDLE_OF_SINGLE_QUOTED_STRING
	}

}
