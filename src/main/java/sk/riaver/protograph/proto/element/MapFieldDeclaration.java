package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class MapFieldDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name mapKeyTypeName;
	private Name mapValueTypeName;
	private Type mapKeyType;
	private Type mapValueType;
	private Name name;
	private String number;
	private List<OptionDeclaration> compactOptions = new ArrayList<>();
	private ProtoFile protoFile;
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		if (!this.mapKeyTypeName.isProcessed()) {
			count++;
		}
		if (!this.mapValueTypeName.isProcessed()) {
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
		return ElementType.MAPFIELD;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		if (!(this.mapValueType instanceof ScalarType)) {
			ret.add((ProtoNode) this.mapValueType);
		}
		return ret;
	}
	
	public ProtoNode getRefContent() {
		if (!(this.mapValueType instanceof ScalarType)) {
			return (ProtoNode) this.mapValueType;
		}
		return null;
	}
	
	public ScalarType getScalarType() {
		if (this.mapValueType instanceof ScalarType) {
			return (ScalarType) this.mapValueType;
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
	public Name getMapKeyTypeName() {
		return this.mapKeyTypeName;
	}
	public void setMapKeyTypeName(Name mapKeyTypeName) {
		this.mapKeyTypeName = mapKeyTypeName;
	}
	public Name getMapValueTypeName() {
		return this.mapValueTypeName;
	}
	public void setMapValueTypeName(Name mapValueTypeName) {
		this.mapValueTypeName = mapValueTypeName;
	}
	public Type getMapKeyType() {
		return this.mapKeyType;
	}
	public void setMapKeyType(Type mapKeyType) {
		this.mapKeyType = mapKeyType;
	}
	public Type getMapValueType() {
		return this.mapValueType;
	}
	public void setMapValueType(Type mapValueType) {
		this.mapValueType = mapValueType;
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
