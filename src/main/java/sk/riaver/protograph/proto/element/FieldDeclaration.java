package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.proto.token.KeywordEnum;
import sk.riaver.protograph.util.Property;

public class FieldDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private KeywordEnum cardinality;
	private Name typeName;
	private Type fieldType;
	private Name name;
	private String number;
	private List<OptionDeclaration> compactOptions = new ArrayList<>();
	private ProtoFile protoFile;
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		if (!this.typeName.isProcessed()) {
			count++;
		}
		return count;
	}
	
	public List<Property> getAllProperties() {
		List<Property> props = new ArrayList<>();
		props.add(new Property(PROP_FULLNAME, this.name.getProcessedName()));
		props.add(new Property(PROP_NUMBER, this.number));
		for (OptionDeclaration opdc : this.compactOptions) {
			String opname = "";
			for (Name name : opdc.getNames()) {
				opname += "." + name.toDisplay();
			}
			props.add(new Property(opname.substring(1), opdc.getValue().toDisplay()));
		}
		return props;
	}
	
	public ElementType getElementType() {
		return ElementType.FIELD;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		if (ANY_TYPE.equals(this.typeName.getProcessedName())) {
			ret.add(new TypeChooser(this.protoFile));
		} else if (!(this.fieldType instanceof ScalarType)) {
			ret.add((ProtoNode) this.fieldType);
		}
		return ret;
	}
	
	public ProtoNode getRefContent() {
		if (!(this.fieldType instanceof ScalarType)) {
			return (ProtoNode) this.fieldType;
		}
		return null;
	}
	
	public ScalarType getScalarType() {
		if (this.fieldType instanceof ScalarType) {
			return (ScalarType) this.fieldType;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return this.name.toDisplay();
	}
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public KeywordEnum getCardinality() {
		return this.cardinality;
	}
	public void setCardinality(KeywordEnum cardinality) {
		this.cardinality = cardinality;
	}
	public Name getTypeName() {
		return this.typeName;
	}
	public void setTypeName(Name typeName) {
		this.typeName = typeName;
	}
	public Type getFieldType() {
		return this.fieldType;
	}
	public void setFieldType(Type fieldType) {
		this.fieldType = fieldType;
	}
	public Name getName() {
		return this.name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public String getNumber() {
		return this.number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public List<OptionDeclaration> getCompactOptions() {
		return this.compactOptions;
	}
	public void addCompactOption(OptionDeclaration option) {
		this.compactOptions.add(option);
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
