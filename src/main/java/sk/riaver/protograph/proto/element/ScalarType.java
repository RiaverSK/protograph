package sk.riaver.protograph.proto.element;

import java.util.HashMap;
import java.util.Map;

import sk.riaver.protograph.proto.token.KeywordEnum;

public class ScalarType implements Type {
	
	private static final Map<String, Type> SCALAR_TYPES = new HashMap<>();
	
	private Name name;
	
	private ScalarType(KeywordEnum key) {
		this.name = new Name();
		this.name.setFullyQualified(true);
		this.name.setKey(key);
		this.name.addQualifiedName(key.name().toLowerCase());
		this.name.setProcessedName(key.name().toLowerCase());
	}
	
	static {
		SCALAR_TYPES.put("bool", new ScalarType(KeywordEnum.BOOL));
		SCALAR_TYPES.put("bytes", new ScalarType(KeywordEnum.BYTES));
		SCALAR_TYPES.put("double", new ScalarType(KeywordEnum.DOUBLE));
		SCALAR_TYPES.put("fixed32", new ScalarType(KeywordEnum.FIXED32));
		SCALAR_TYPES.put("fixed64", new ScalarType(KeywordEnum.FIXED64));
		SCALAR_TYPES.put("float", new ScalarType(KeywordEnum.FLOAT));
		SCALAR_TYPES.put("int32", new ScalarType(KeywordEnum.INT32));
		SCALAR_TYPES.put("int64", new ScalarType(KeywordEnum.INT64));
		SCALAR_TYPES.put("sfixed32", new ScalarType(KeywordEnum.SFIXED32));
		SCALAR_TYPES.put("sfixed64", new ScalarType(KeywordEnum.SFIXED64));
		SCALAR_TYPES.put("sint32", new ScalarType(KeywordEnum.SINT32));
		SCALAR_TYPES.put("sint64", new ScalarType(KeywordEnum.SINT64));
		SCALAR_TYPES.put("string", new ScalarType(KeywordEnum.STRING));
		SCALAR_TYPES.put("uint32", new ScalarType(KeywordEnum.UINT32));
		SCALAR_TYPES.put("uint64", new ScalarType(KeywordEnum.UINT64));
	}
	
	public static Map<String, Type> getScalarTypes() {
		return SCALAR_TYPES;
	}
	
	public Name getName() {
		return this.name;
	}
	public ProtoFile getProtoFile() {
		return null;
	}
	public String toString() {
		return this.name.getProcessedName();
	}

}
