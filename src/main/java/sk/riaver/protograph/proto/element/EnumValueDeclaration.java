package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class EnumValueDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name name;
	private String number;
	private List<OptionDeclaration> compactOptions = new ArrayList<>();
	private ProtoFile protoFile;
	
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
		return ElementType.ENUMVALUE;
	}
	
	public List<ProtoNode> getComplexContent() {
		return  new ArrayList<>();
	}
	
	public ProtoNode getRefContent() {
		return null;
	}
	
	public ScalarType getScalarType() {
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
