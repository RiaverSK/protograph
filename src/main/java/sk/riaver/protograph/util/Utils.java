package sk.riaver.protograph.util;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.element.FieldDeclaration;
import sk.riaver.protograph.proto.element.GroupDeclaration;
import sk.riaver.protograph.proto.element.ProtoNode;
import sk.riaver.protograph.proto.token.KeywordEnum;

public class Utils {
	
	private Utils() { }
	
	public static String getCardinality(ProtoNode node) {
		String ret = "";
		if (node.getElementType() == ElementType.FIELD) {
			ret = convertCardinality(((FieldDeclaration) node).getCardinality());
		}
		if (node.getElementType() == ElementType.GROUP) {
			ret = convertCardinality(((GroupDeclaration) node).getCardinality());
		}
		if (node.getElementType() == ElementType.MAPFIELD) {
			ret = "0..N";
		}
		if (node.getElementType() == ElementType.ONEOF) {
			ret = "0..1";
		}
		
		return ret;
	}
	
	public static String convertCardinality(KeywordEnum cardinal) {
		String ret = "";
		if (cardinal == KeywordEnum.REQUIRED) {
			ret = "1..1";
		} else if (cardinal == KeywordEnum.REPEATED) {
			ret = "0..N";
		} else {
			ret = "0..1";
		}
		return ret;
	}
	
	public static String prepareComment(String text) {
		if (text.startsWith("//")) {
			return text.substring(2).trim();
		}
		String[] lines = text.substring(2, text.length()-2).split("\n");
		String ret = "";
		for (String line : lines) {
			String ln = line.trim();
			while (ln.startsWith("*")) {
				ln = ln.substring(1);
			}
			ret += ln.trim() + "\n";
		}
		return ret.trim();
	}

}
