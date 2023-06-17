package sk.riaver.protograph.proto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sk.riaver.protograph.proto.element.*;
import sk.riaver.protograph.proto.token.*;

public final class ProtoParser {
	
	private static Log log = LogFactory.getLog(ProtoParser.class);
	
	private ProtoParser() { }
	
	public static ProtoFile parseProtofile(ProtoTokenizer tokenizer) {
		ProtoFile protofile = new ProtoFile();
		List<Comment> actualElementComments = protofile.getComments();
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		// SyntaxDecl
		protofile.setSyntax(parseSyntaxDecl(tokenizer, actualElementComments));
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			if (!(tokenizer.hasMoreTokens())) {
				protofile.getComments().addAll(actualElementComments);
				break;
			}
			Token token = tokenizer.getNext();
			// EmptyDecl
			if (isSymbol(token, SymbolEnum.SEMICOLON)) {
				continue;
			} else if (isKeyword(token, KeywordEnum.IMPORT)) {
				// ImportDecl
				protofile.addImport(parseImportDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.PACKAGE)) {
				// PackageDecl
				protofile.getComments().addAll(actualElementComments);
				protofile.setProtopackage(parsePackageDecl(tokenizer, protofile.getComments()));
			} else if (isKeyword(token, KeywordEnum.OPTION)) {
				// OptionDecl
				protofile.addOption(parseOptionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.MESSAGE)) {
				// MessageDecl
				protofile.addMessage(parseMessageDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.ENUM)) {
				// EnumDecl
				protofile.addEnum(parseEnumDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.EXTEND)) {
				// ExtensionDecl
				protofile.addExtension(parseExtensionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.SERVICE)) {
				// ServiceDecl
				protofile.addService(parseServiceDecl(tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected file element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			protofile.getComments().addAll(actualElementComments);
		}
		return protofile;
	}
	
	public static void parseComments(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		Token token = null;
		do {
			if (tokenizer.hasMoreTokens()) {
				token = tokenizer.getNext();
				if (token.getType() == TokenType.COMMENT) {
					actualElementComments.add((Comment) token);
				}
			} else {
				return;
			}
		} while (token.getType() == TokenType.COMMENT);
		tokenizer.rewind();
	}
	
	public static void parseEndOfLineComment(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		if (tokenizer.hasMoreTokens()) {
			Token token = tokenizer.getNext();
			if (token.getType() == TokenType.COMMENT && ((Comment) token).isEndOfLine()) {
				actualElementComments.add((Comment) token);
			} else {
				tokenizer.rewind();
			}
		}
	}
	
	public static String parseSyntaxDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseSyntaxDecl");
		String syntax = ProtoFile.DEFAULT_SYNTAX;
		if (isKeyword(tokenizer.getNext(), KeywordEnum.SYNTAX)) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			syntax = parseStringLiterals(tokenizer, actualElementComments);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.SEMICOLON);
			parseEndOfLineComment(tokenizer, actualElementComments);
		} else {
			tokenizer.rewind();
		}
		return syntax;
	}
	
	public static Name parsePackageDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parsePackageDecl");
		Name pcg = null;
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		pcg = parseQualifiedIdentifier(tokenizer, actualElementComments);
		pcg.setFullyQualified(true);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return pcg;
	}
	
	public static ImportDeclaration parseImportDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseImportDecl");
		ImportDeclaration idc = new ImportDeclaration();
		idc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (isKeyword(token, KeywordEnum.WEAK)) {
			idc.setWeak(true);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
		} else if (isKeyword(token, KeywordEnum.PUBLIC)) {
			idc.setPublic(true);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
		} else {
			tokenizer.rewind();
		}
		idc.setFilename(parseStringLiterals(tokenizer, actualElementComments));
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return idc;
	}
	
	public static OptionDeclaration parseOptionDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseOptionDecl");
		OptionDeclaration opdc = new OptionDeclaration();
		opdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		opdc.setNames(parseOptionNames(tokenizer, actualElementComments));
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		opdc.setValue(parseOptionValue(tokenizer, actualElementComments));
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return opdc;
	}
	
	public static List<Name> parseOptionNames(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseOptionNames");
		List<Name> names = new ArrayList<>();
		Token token = null;
		do {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (token.getType() == TokenType.IDENTIFIER) {
				Name name = new Name();
				name.addQualifiedName(token.getValue());
				names.add(name);
			} else if (isSymbol(token, SymbolEnum.LEFT_PAREN)) {
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				boolean fullyQualified = false;
				if (isSymbol(tokenizer.getNext(), SymbolEnum.DOT)) {
					fullyQualified = true;
				} else {
					tokenizer.rewind();
				}
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				Name name = parseQualifiedIdentifier(tokenizer, actualElementComments);
				name.setExtensionName(true);
				name.setFullyQualified(fullyQualified);
				names.add(name);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_PAREN);
			} else {
				throw new ProtoParserException("Syntax error, expecting option name");
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		} while (isSymbol(token, SymbolEnum.DOT));
		tokenizer.rewind();
		return names;
	}
	
	public static Value parseOptionValue(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseOptionValue");
		Value ret = null;
		Token token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.LEFT_BRACE)) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			ret = parseMessageTextFormat(tokenizer, actualElementComments);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACE);
		} else {
			tokenizer.rewind();
			ret = parseScalarValue(tokenizer, actualElementComments);
		}
		return ret;
	}
	
	public static Value parseScalarValue(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseScalarValue");
		ScalarValue ret = new ScalarValue();
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.STRING_LITERAL) {
			tokenizer.rewind();
			ret.setScalarValue(parseStringLiterals(tokenizer, actualElementComments));
		} else if (token.getType() == TokenType.IDENTIFIER) {
			ret.setScalarValue(token.getValue());
		} else if (isSymbol(token, SymbolEnum.PLUS) || isSymbol(token, SymbolEnum.MINUS)) {
			String lit = token.getValue();
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (token.getType() == TokenType.INT_LITERAL || token.getType() == TokenType.FLOAT_LITERAL
					|| isKeyword(token, KeywordEnum.INF)) {
				lit += token.getValue();
			} else {
				throw new ProtoParserException("Syntax error, expecting number");
			}
			ret.setScalarValue(lit);
		} else if (token.getType() == TokenType.INT_LITERAL || token.getType() == TokenType.FLOAT_LITERAL) {
			ret.setScalarValue(token.getValue());
		} else {
			throw new ProtoParserException("Syntax error, expecting scalar value");
		}
		return ret;
	}
	
	public static Value parseMessageTextFormat(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseMessageTextFormat");
		ListValue ret = new ListValue();
		Token token = tokenizer.getNext();
		while ((token.getType() == TokenType.IDENTIFIER) || (isSymbol(token, SymbolEnum.LEFT_BRACKET))) {
			FieldValue fval = new FieldValue();
			ret.addValue(fval);
			if (token.getType() == TokenType.IDENTIFIER) {
				// MessageLiteralField.MessageLiteralFieldName.FieldName
				Name name = new Name();
				name.addQualifiedName(token.getValue());
				fval.setName(name);
			} else {
				// MessageLiteralField.MessageLiteralFieldName.SpecialFieldName
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				Name name = parseQualifiedIdentifier(tokenizer, actualElementComments);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isSymbol(token, SymbolEnum.SLASH)) {
					// MessageLiteralField.MessageLiteralFieldName.SpecialFieldName.TypeURL
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					Name typeUrl = parseQualifiedIdentifier(tokenizer, actualElementComments);
					typeUrl.setUrl(name.toDisplay());
					typeUrl.setFullyQualified(true);
					name = typeUrl;
				} else {
					// MessageLiteralField.MessageLiteralFieldName.SpecialFieldName.ExtensionFieldName
					name.setExtensionName(true);
					tokenizer.rewind();
				}
				fval.setName(name);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isSymbol(token, SymbolEnum.COLON)) {
				// MessageLiteralField.Value
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isSymbol(token, SymbolEnum.LEFT_BRACE)) {
					// MessageLiteralField.Value.MessageLiteral.MessageLiteralWithBraces
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					fval.setValue(parseMessageTextFormat(tokenizer, actualElementComments));
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACE);
				} else if (isSymbol(token, SymbolEnum.LEFT_ANGLE)) {
					// MessageLiteralField.Value.MessageLiteral.MessageTextFormat
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					fval.setValue(parseMessageTextFormat(tokenizer, actualElementComments));
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_ANGLE);
				} else if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
					// MessageLiteralField.Value.ListLiteral
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					fval.setValue(parseListLiteral(tokenizer, actualElementComments));
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
				} else {
					// MessageLiteralField.Value.ScalarValue
					tokenizer.rewind();
					fval.setValue(parseScalarValue(tokenizer, actualElementComments));
				}
			} else if (isSymbol(token, SymbolEnum.LEFT_BRACE)) {
				// MessageLiteralField.MessageValue.MessageLiteral.MessageLiteralWithBraces
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				fval.setValue(parseMessageTextFormat(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACE);
			} else if (isSymbol(token, SymbolEnum.LEFT_ANGLE)) {
				// MessageLiteralField.MessageValue.MessageLiteral.MessageTextFormat
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				fval.setValue(parseMessageTextFormat(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_ANGLE);
			} else if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
				// MessageLiteralField.MessageValue.ListOfMessagesLiteral
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				fval.setValue(parseListOfMessagesLiteral(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			} else {
				throw new ProtoParserException("Syntax error, expecting message literal field value");
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isSymbol(token, SymbolEnum.COMMA) || isSymbol(token, SymbolEnum.SEMICOLON)) {
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
			}
		}
		tokenizer.rewind();
		return ret;
	}
	
	public static Value parseListLiteral(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseListLiteral");
		ListValue ret = new ListValue();
		Token token = null;
		do {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isSymbol(token, SymbolEnum.LEFT_BRACE)) {
				// ListLiteral.ListElement.MessageLiteral.MessageLiteralWithBraces
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				ret.addValue(parseMessageTextFormat(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(token, SymbolEnum.RIGHT_BRACE);
			} else if (isSymbol(token, SymbolEnum.LEFT_ANGLE)) {
				// ListLiteral.ListElement.MessageLiteral.MessageTextFormat
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				ret.addValue(parseMessageTextFormat(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(token, SymbolEnum.RIGHT_ANGLE);
			} else if (isSymbol(token, SymbolEnum.RIGHT_BRACKET)) {
				break; // in case of empty list
			} else {
				// ListLiteral.ListElement.ScalarValue
				tokenizer.rewind();
				ret.addValue(parseScalarValue(tokenizer, actualElementComments));
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		} while (isSymbol(token, SymbolEnum.COMMA));
		tokenizer.rewind();
		return ret;
	}
	
	public static Value parseListOfMessagesLiteral(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseListOfMessagesLiteral");
		ListValue ret = new ListValue();
		Token token = null;
		do {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isSymbol(token, SymbolEnum.LEFT_BRACE)) {
				// ListLiteral.ListElement.MessageLiteral.MessageLiteralWithBraces
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				ret.addValue(parseMessageTextFormat(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(token, SymbolEnum.RIGHT_BRACE);
			} else if (isSymbol(token, SymbolEnum.LEFT_ANGLE)) {
				// ListLiteral.ListElement.MessageLiteral.MessageTextFormat
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				ret.addValue(parseMessageTextFormat(tokenizer, actualElementComments));
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				assertSymbol(token, SymbolEnum.RIGHT_ANGLE);
			} else if (isSymbol(token, SymbolEnum.RIGHT_BRACKET)) {
				break; // in case of empty list
			} else {
				throw new ProtoParserException("Syntax error, expected message literal");
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		} while (isSymbol(token, SymbolEnum.COMMA));
		tokenizer.rewind();
		return ret;
	}
	
	public static MessageDeclaration parseMessageDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseMessageDecl");
		MessageDeclaration msdc = new MessageDeclaration();
		msdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			msdc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected message name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_BRACE);
		parseEndOfLineComment(tokenizer, actualElementComments);
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			// EmptyDecl
			if (isSymbol(token, SymbolEnum.SEMICOLON)) {
				continue;
			} else if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
				break;
			} else if (isKeyword(token, KeywordEnum.REQUIRED)
					|| isKeyword(token, KeywordEnum.OPTIONAL)
					|| isKeyword(token, KeywordEnum.REPEATED)) {
				KeywordEnum card = ((Identifier) token).getKey();
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isKeyword(token, KeywordEnum.GROUP)) {
					// GroupDecl
					msdc.addGroup(parseGroupDecl(card, tokenizer, actualElementComments));
				} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
					// FieldDecl
					tokenizer.rewind();
					msdc.addField(parseFieldDecl(card, tokenizer, actualElementComments));
				}
			} else if (isKeyword(token, KeywordEnum.MAP)) {
				// MapFieldDecl
				msdc.addMapfield(parseMapFieldDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.ONEOF)) {
				// OneofDecl
				msdc.addOneof(parseOneofDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.OPTION)) {
				// OptionDecl
				msdc.addOption(parseOptionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.EXTENSIONS)) {
				// ExtensionRangeDecl
				msdc.addExtensionRange(parseExtensionRangeDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.RESERVED)) {
				// MessageReservedDecl
				msdc.addMessageReserved(parseMessageReservedDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.MESSAGE)) {
				// MessageDecl
				msdc.addMessage(parseMessageDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.ENUM)) {
				// EnumDecl
				msdc.addEnum(parseEnumDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.EXTEND)) {
				// ExtensionDecl
				msdc.addExtension(parseExtensionDecl(tokenizer, actualElementComments));
			} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
				// FieldDecl
				tokenizer.rewind();
				msdc.addField(parseFieldDecl(null, tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected message element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			msdc.getComments().addAll(actualElementComments);
		}
		return msdc;
	}
	
	public static EnumDeclaration parseEnumDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseEnumDecl");
		EnumDeclaration endc = new EnumDeclaration();
		endc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			endc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected enum name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_BRACE);
		parseEndOfLineComment(tokenizer, actualElementComments);
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			// EmptyDecl
			if (isSymbol(token, SymbolEnum.SEMICOLON)) {
				continue;
			} else if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
				break;
			} else if (isKeyword(token, KeywordEnum.OPTION)) {
				// OptionDecl
				endc.addOption(parseOptionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.RESERVED)) {
				// EnumReservedDecl
				endc.addEnumReserved(parseEnumReservedDecl(tokenizer, actualElementComments));
			} else if (token.getType() == TokenType.IDENTIFIER) {
				// EnumValueDecl
				tokenizer.rewind();
				endc.addEnumValue(parseEnumValueDecl(tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected enum element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			endc.getComments().addAll(actualElementComments);
		}
		return endc;
	}
	
	public static EnumValueDeclaration parseEnumValueDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseEnumValueDecl");
		EnumValueDeclaration evdc = new EnumValueDeclaration();
		evdc.setComments(actualElementComments);
		Token token = tokenizer.getNext();
		Name name = new Name();
		name.addQualifiedName(token.getValue());
		evdc.setName(name);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		String value = "";
		token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.MINUS)) {
			value += "-";
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		if (token.getType() == TokenType.INT_LITERAL) {
			value += token.getValue();
			evdc.setNumber(value);
		} else {
			throw new ProtoParserException("Syntax error, expected enum number");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
			evdc.getCompactOptions().addAll(parseCompactOptions(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		assertSymbol(token, SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return evdc;
	}
	
	public static List<OptionDeclaration> parseCompactOptions(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseCompactOptions");
		List<OptionDeclaration> options = new ArrayList<>();
		Token token = null;
		do {
			OptionDeclaration opdc = new OptionDeclaration();
			options.add(opdc);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			opdc.setNames(parseOptionNames(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			opdc.setValue(parseOptionValue(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		} while (isSymbol(token, SymbolEnum.COMMA));
		tokenizer.rewind();
		return options;
	}
	
	public static EnumReservedDeclaration parseEnumReservedDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseEnumReservedDecl");
		EnumReservedDeclaration erdc = new EnumReservedDeclaration();
		erdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.STRING_LITERAL) {
			tokenizer.rewind();
			do {
				Name name = new Name();
				name.addQualifiedName(parseStringLiterals(tokenizer, actualElementComments));
				erdc.addName(name);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
			} while (isSymbol(token, SymbolEnum.COMMA));
		} else {
			tokenizer.rewind();
			do {
				TagRange range = new TagRange();
				erdc.addRange(range);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				String numb = "";
				if (isSymbol(token, SymbolEnum.MINUS)) {
					numb += "-";
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					token = tokenizer.getNext();
				}
				if (token.getType() == TokenType.INT_LITERAL) {
					numb += token.getValue();
					range.setStart(numb);
				} else {
					throw new ProtoParserException("Syntax error, expected enum value number");
				}
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isKeyword(token, KeywordEnum.TO)) {
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					token = tokenizer.getNext();
					numb = "";
					if (isSymbol(token, SymbolEnum.MINUS)) {
						numb += "-";
						parseComments(tokenizer, actualElementComments);
						assertNotEndOfFile(tokenizer);
						token = tokenizer.getNext();
					}
					if (token.getType() == TokenType.INT_LITERAL) {
						numb += token.getValue();
						range.setEnd(numb);
					} else if (isKeyword(token, KeywordEnum.MAX)) {
						range.setEnd(token.getValue());
					} else {
						throw new ProtoParserException("Syntax error, expected enum value number");
					}
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					token = tokenizer.getNext();
				}
			} while (isSymbol(token, SymbolEnum.COMMA));
		}
		assertSymbol(token, SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return erdc;
	}
	
	public static ExtensionDeclaration parseExtensionDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseExtensionDecl");
		ExtensionDeclaration exdc = new ExtensionDeclaration();
		exdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		boolean fullyQualified = false;
		if (isSymbol(token, SymbolEnum.DOT)) {
			fullyQualified = true;
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
		} else {
			tokenizer.rewind();
		}
		Name name = parseQualifiedIdentifier(tokenizer, actualElementComments);
		name.setFullyQualified(fullyQualified);
		exdc.setMessageName(name);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_BRACE);
		parseEndOfLineComment(tokenizer, actualElementComments);
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
				break;
			} else if (isKeyword(token, KeywordEnum.REQUIRED)
					|| isKeyword(token, KeywordEnum.OPTIONAL)
					|| isKeyword(token, KeywordEnum.REPEATED)) {
				KeywordEnum card = ((Identifier) token).getKey();
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isKeyword(token, KeywordEnum.GROUP)) {
					// GroupDecl
					exdc.addGroup(parseGroupDecl(card, tokenizer, actualElementComments));
				} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
					// FieldDecl
					tokenizer.rewind();
					exdc.addField(parseFieldDecl(card, tokenizer, actualElementComments));
				}
			} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
				// FieldDecl
				tokenizer.rewind();
				exdc.addField(parseFieldDecl(null, tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected extension element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			exdc.getComments().addAll(actualElementComments);
		}
		return exdc;
	}
	
	public static ServiceDeclaration parseServiceDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseServiceDecl");
		ServiceDeclaration srdc = new ServiceDeclaration();
		srdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			srdc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected service name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_BRACE);
		parseEndOfLineComment(tokenizer, actualElementComments);
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			// EmptyDecl
			if (isSymbol(token, SymbolEnum.SEMICOLON)) {
				continue;
			} else if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
				break;
			} else if (isKeyword(token, KeywordEnum.OPTION)) {
				// OptionDecl
				srdc.addOption(parseOptionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.RPC)) {
				// MethodDecl
				srdc.addMethod(parseMethodDecl(tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected service element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			srdc.getComments().addAll(actualElementComments);
		}
		return srdc;
	}
	
	public static MethodDeclaration parseMethodDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseMethodDecl");
		MethodDeclaration mtdc = new MethodDeclaration();
		mtdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			mtdc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected method name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_PAREN);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isKeyword(token, KeywordEnum.STREAM)) {
			mtdc.setInputStream(true);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		boolean fullyQualified = false;
		if (isSymbol(token, SymbolEnum.DOT)) {
			fullyQualified = true;
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		if (token.getType() == TokenType.IDENTIFIER) {
			tokenizer.rewind();
			Name type = parseQualifiedIdentifier(tokenizer, actualElementComments);
			type.setFullyQualified(fullyQualified);
			mtdc.setInputTypeName(type);
		} else {
			throw new ProtoParserException("Syntax error, expected input type");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_PAREN);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertKeyword(tokenizer.getNext(), KeywordEnum.RETURNS);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_PAREN);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isKeyword(token, KeywordEnum.STREAM)) {
			mtdc.setOutputStream(true);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		fullyQualified = false;
		if (isSymbol(token, SymbolEnum.DOT)) {
			fullyQualified = true;
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		if (token.getType() == TokenType.IDENTIFIER) {
			tokenizer.rewind();
			Name type = parseQualifiedIdentifier(tokenizer, actualElementComments);
			type.setFullyQualified(fullyQualified);
			mtdc.setOutputTypeName(type);
		} else {
			throw new ProtoParserException("Syntax error, expected input type");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_PAREN);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.LEFT_BRACE)) {
			parseEndOfLineComment(tokenizer, actualElementComments);
			actualElementComments = new ArrayList<>();
			while (tokenizer.hasMoreTokens()) {
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				// EmptyDecl
				if (isSymbol(token, SymbolEnum.SEMICOLON)) {
					continue;
				} else if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
					break;
				} else if (isKeyword(token, KeywordEnum.OPTION)) {
					// OptionDecl
					mtdc.addOption(parseOptionDecl(tokenizer, actualElementComments));
				} else {
					throw new ProtoParserException("Syntax error, expected service element");
				}
				actualElementComments = new ArrayList<>();
			}
		} else if (isSymbol(token, SymbolEnum.SEMICOLON)) {
			actualElementComments = new ArrayList<>();
		} else {
			throw new ProtoParserException("Syntax error, expected semicolon or left brace");
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			mtdc.getComments().addAll(actualElementComments);
		}
		return mtdc;
	}
	
	public static FieldDeclaration parseFieldDecl(KeywordEnum cardinality, ProtoTokenizer tokenizer,
			List<Comment> actualElementComments) {
		log.debug("parseFieldDecl");
		FieldDeclaration fddc = new FieldDeclaration();
		fddc.setComments(actualElementComments);
		fddc.setCardinality(cardinality);
		Token token = tokenizer.getNext();
		boolean fullyQalified = false;
		if (isSymbol(token, SymbolEnum.DOT)) {
			fullyQalified = true;
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
		} else {
			tokenizer.rewind();
		}
		Name type = parseQualifiedIdentifier(tokenizer, actualElementComments);
		type.setFullyQualified(fullyQalified);
		fddc.setTypeName(type);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			fddc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected field name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (token.getType() == TokenType.INT_LITERAL) {
			fddc.setNumber(token.getValue());
		} else {
			throw new ProtoParserException("Syntax error, expected field number");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
			fddc.getCompactOptions().addAll(parseCompactOptions(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		assertSymbol(token, SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return fddc;
	}
	
	public static GroupDeclaration parseGroupDecl(KeywordEnum cardinality, ProtoTokenizer tokenizer,
			List<Comment> actualElementComments) {
		log.debug("parseGroupDecl");
		GroupDeclaration grdc = new GroupDeclaration();
		grdc.setComments(actualElementComments);
		grdc.setCardinality(cardinality);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			grdc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected group name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (token.getType() == TokenType.INT_LITERAL) {
			grdc.setNumber(token.getValue());
		} else {
			throw new ProtoParserException("Syntax error, expected group number");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
			grdc.getCompactOptions().addAll(parseCompactOptions(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		assertSymbol(token, SymbolEnum.LEFT_BRACE);
		parseEndOfLineComment(tokenizer, actualElementComments);
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			// EmptyDecl
			if (isSymbol(token, SymbolEnum.SEMICOLON)) {
				continue;
			} else if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
				break;
			} else if (isKeyword(token, KeywordEnum.REQUIRED)
					|| isKeyword(token, KeywordEnum.OPTIONAL)
					|| isKeyword(token, KeywordEnum.REPEATED)) {
				KeywordEnum card = ((Identifier) token).getKey();
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isKeyword(token, KeywordEnum.GROUP)) {
					// GroupDecl
					grdc.addGroup(parseGroupDecl(card, tokenizer, actualElementComments));
				} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
					// FieldDecl
					tokenizer.rewind();
					grdc.addField(parseFieldDecl(card, tokenizer, actualElementComments));
				}
			} else if (isKeyword(token, KeywordEnum.MAP)) {
				// MapFieldDecl
				grdc.addMapfield(parseMapFieldDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.ONEOF)) {
				// OneofDecl
				grdc.addOneof(parseOneofDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.OPTION)) {
				// OptionDecl
				grdc.addOption(parseOptionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.EXTENSIONS)) {
				// ExtensionRangeDecl
				grdc.addExtensionRange(parseExtensionRangeDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.RESERVED)) {
				// MessageReservedDecl
				grdc.addMessageReserved(parseMessageReservedDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.MESSAGE)) {
				// MessageDecl
				grdc.addMessage(parseMessageDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.ENUM)) {
				// EnumDecl
				grdc.addEnum(parseEnumDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.EXTEND)) {
				// ExtensionDecl
				grdc.addExtension(parseExtensionDecl(tokenizer, actualElementComments));
			} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
				// FieldDecl
				tokenizer.rewind();
				grdc.addField(parseFieldDecl(null, tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected message element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			grdc.getComments().addAll(actualElementComments);
		}
		return grdc;
	}
	
	public static MapFieldDeclaration parseMapFieldDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseMapFieldDecl");
		MapFieldDeclaration mfdc = new MapFieldDeclaration();
		mfdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_ANGLE);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER && (((Identifier) token).getKey() != null)) {
			Name type = new Name();
			type.addQualifiedName(token.getValue());
			mfdc.setMapKeyTypeName(type);
		} else {
			throw new ProtoParserException("Syntax error, expected map key type");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.COMMA);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		boolean fullyQualified = false;
		if (isSymbol(token, SymbolEnum.DOT)) {
			fullyQualified = true;
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		} else {
			tokenizer.rewind();
		}
		Name type = parseQualifiedIdentifier(tokenizer, actualElementComments);
		type.setFullyQualified(fullyQualified);
		mfdc.setMapValueTypeName(type);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_ANGLE);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			mfdc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected map name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.EQUALS);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (token.getType() == TokenType.INT_LITERAL) {
			mfdc.setNumber(token.getValue());
		} else {
			throw new ProtoParserException("Syntax error, expected map number");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
			mfdc.getCompactOptions().addAll(parseCompactOptions(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		assertSymbol(token, SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return mfdc;
	}
	
	public static OneofDeclaration parseOneofDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseOneofDecl");
		OneofDeclaration oodc = new OneofDeclaration();
		oodc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			Name name = new Name();
			name.addQualifiedName(token.getValue());
			oodc.setName(name);
		} else {
			throw new ProtoParserException("Syntax error, expected oneof name");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		assertSymbol(tokenizer.getNext(), SymbolEnum.LEFT_BRACE);
		parseEndOfLineComment(tokenizer, actualElementComments);
		actualElementComments = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isSymbol(token, SymbolEnum.RIGHT_BRACE)) {
				break;
			} else if (isKeyword(token, KeywordEnum.OPTION)) {
				// OptionDecl
				oodc.addOption(parseOptionDecl(tokenizer, actualElementComments));
			} else if (isKeyword(token, KeywordEnum.GROUP)) {
				// OneofGroupDecl
				oodc.addGroup(parseGroupDecl(null, tokenizer, actualElementComments));
			} else if (isSymbol(token, SymbolEnum.DOT) || (token.getType() == TokenType.IDENTIFIER)) {
				// OneofFieldDecl
				tokenizer.rewind();
				oodc.addField(parseFieldDecl(null, tokenizer, actualElementComments));
			} else {
				throw new ProtoParserException("Syntax error, expected oneof element");
			}
			actualElementComments = new ArrayList<>();
		}
		parseEndOfLineComment(tokenizer, actualElementComments);
		if (!actualElementComments.isEmpty()) {
			oodc.getComments().addAll(actualElementComments);
		}
		return oodc;
	}
	
	public static ExtensionRangeDeclaration parseExtensionRangeDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseExtensionRangeDecl");
		ExtensionRangeDeclaration erdc = new ExtensionRangeDeclaration();
		erdc.setComments(actualElementComments);
		Token token = null;
		do {
			TagRange range = new TagRange();
			erdc.addRange(range);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (token.getType() == TokenType.INT_LITERAL) {
				range.setStart(token.getValue());
			} else {
				throw new ProtoParserException("Syntax error, expected field number");
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (isKeyword(token, KeywordEnum.TO)) {
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (token.getType() == TokenType.INT_LITERAL) {
					range.setEnd(token.getValue());
				} else if (isKeyword(token, KeywordEnum.MAX)) {
					range.setEnd(token.getValue());
				} else {
					throw new ProtoParserException("Syntax error, expected field number");
				}
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
			}
		} while (isSymbol(token, SymbolEnum.COMMA));
		if (isSymbol(token, SymbolEnum.LEFT_BRACKET)) {
			erdc.getCompactOptions().addAll(parseCompactOptions(tokenizer, actualElementComments));
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			assertSymbol(tokenizer.getNext(), SymbolEnum.RIGHT_BRACKET);
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		assertSymbol(token, SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return erdc;
	}
	
	public static MessageReservedDeclaration parseMessageReservedDecl(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		log.debug("parseMessageReservedDecl");
		MessageReservedDeclaration mrdc = new MessageReservedDeclaration();
		mrdc.setComments(actualElementComments);
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.STRING_LITERAL) {
			tokenizer.rewind();
			do {
				Name name = new Name();
				name.addQualifiedName(parseStringLiterals(tokenizer, actualElementComments));
				mrdc.addName(name);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
			} while (isSymbol(token, SymbolEnum.COMMA));
		} else {
			tokenizer.rewind();
			do {
				TagRange range = new TagRange();
				mrdc.addRange(range);
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (token.getType() == TokenType.INT_LITERAL) {
					range.setStart(token.getValue());
				} else {
					throw new ProtoParserException("Syntax error, expected field number");
				}
				parseComments(tokenizer, actualElementComments);
				assertNotEndOfFile(tokenizer);
				token = tokenizer.getNext();
				if (isKeyword(token, KeywordEnum.TO)) {
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					token = tokenizer.getNext();
					if (token.getType() == TokenType.INT_LITERAL) {
						range.setEnd(token.getValue());
					} else if (isKeyword(token, KeywordEnum.MAX)) {
						range.setEnd(token.getValue());
					} else {
						throw new ProtoParserException("Syntax error, expected field number");
					}
					parseComments(tokenizer, actualElementComments);
					assertNotEndOfFile(tokenizer);
					token = tokenizer.getNext();
				}
			} while (isSymbol(token, SymbolEnum.COMMA));
		}
		assertSymbol(token, SymbolEnum.SEMICOLON);
		parseEndOfLineComment(tokenizer, actualElementComments);
		return mrdc;
	}
	
	public static void assertNotEndOfFile(ProtoTokenizer tokenizer) {
		if (!tokenizer.hasMoreTokens()) {
			throw new ProtoParserException("End of file");
		}
	}
	
	public static void assertSymbol(Token token, SymbolEnum symbol) {
		if (!(token.getType() == TokenType.SYMBOL) || ((Symbol) token).getSymbol() != symbol) {
			throw new ProtoParserException("Syntax error, expecting symbol " + symbol.getSymbol());
		}
	}
	
	public static void assertKeyword(Token token, KeywordEnum key) {
		if (!(token.getType() == TokenType.IDENTIFIER) || ((Identifier) token).getKey() != key) {
			throw new ProtoParserException("Syntax error, expecting keyword " + key.name().toLowerCase());
		}
	}
	
	public static boolean isSymbol(Token token, SymbolEnum symbol) {
		return ((token.getType() == TokenType.SYMBOL) && ((Symbol) token).getSymbol() == symbol);
	}
	
	public static boolean isKeyword(Token token, KeywordEnum key) {
		return ((token.getType() == TokenType.IDENTIFIER) && ((Identifier) token).getKey() == key);
	}
	
	public static String parseStringLiterals(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		String text = "";
		boolean hasLiteral = false;
		Token token = tokenizer.getNext();
		while (token.getType() == TokenType.STRING_LITERAL) {
			text += unwrapString(token.getValue());
			hasLiteral = true;
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		if ("".equals(text) && !hasLiteral) {
			throw new ProtoParserException("Syntax error, expecting string literal");
		}
		tokenizer.rewind();
		return text;
	}
	
	public static Name parseQualifiedIdentifier(ProtoTokenizer tokenizer, List<Comment> actualElementComments) {
		Name name = new Name();
		KeywordEnum key = null;
		Token token = tokenizer.getNext();
		if (token.getType() == TokenType.IDENTIFIER) {
			name.addQualifiedName(token.getValue());
			key = ((Identifier) token).getKey();
		} else {
			throw new ProtoParserException("Syntax error, expecting identifier");
		}
		parseComments(tokenizer, actualElementComments);
		assertNotEndOfFile(tokenizer);
		token = tokenizer.getNext();
		while (isSymbol(token, SymbolEnum.DOT)) {
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
			if (token.getType() == TokenType.IDENTIFIER) {
				name.addQualifiedName(token.getValue());
			} else {
				throw new ProtoParserException("Syntax error, expecting identifier");
			}
			parseComments(tokenizer, actualElementComments);
			assertNotEndOfFile(tokenizer);
			token = tokenizer.getNext();
		}
		tokenizer.rewind();
		if (name.isSimpleName() && key != null) {
			name.setKey(key);
		}
		return name;
	}
	
	private static String unwrapString(String text) {
		// just remove quote characters
		return text.substring(1, text.length()-1);
	}

}
