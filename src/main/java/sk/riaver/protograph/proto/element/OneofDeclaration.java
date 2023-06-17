package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class OneofDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name name;
	private List<FieldDeclaration> fields = new ArrayList<>();
	private List<GroupDeclaration> groups = new ArrayList<>();
	private List<OptionDeclaration> options = new ArrayList<>();
	private ProtoFile protoFile;
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		for (FieldDeclaration fldc : this.fields) {
			count += fldc.countUnprocessedTypeNames();
		}
		for (GroupDeclaration grdc : this.groups) {
			count += grdc.countUnprocessedTypeNames();
		}
		return count;
	}
	
	public List<Property> getAllProperties() {
		List<Property> props = new ArrayList<>();
		props.add(new Property(PROP_FULLNAME, this.name.getProcessedName()));
		for (OptionDeclaration opdc : this.options) {
			String opname = "";
			for (Name name : opdc.getNames()) {
				opname += "." + name.toDisplay();
			}
			props.add(new Property(opname.substring(1), opdc.getValue().toDisplay()));
		}
		return props;
	}
	
	public ElementType getElementType() {
		return ElementType.ONEOF;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		ret.addAll(this.fields);
		ret.addAll(this.groups);
		return ret;
	}
	
	public ProtoNode getRefContent() {
		return null;
	}
	
	public ScalarType getScalarType() {
		return null;
	}
	
	public List<MessageReservedDeclaration> getAllReserveds() {
		List<MessageReservedDeclaration> allreserveds = new ArrayList<>();
		for (GroupDeclaration grdc : this.groups) {
			allreserveds.addAll(grdc.getAllReserveds());
		}
		return allreserveds;
	}
	
	public List<ExtensionRangeDeclaration> getAllExtensions() {
		List<ExtensionRangeDeclaration> allexts = new ArrayList<>();
		for (GroupDeclaration grdc : this.groups) {
			allexts.addAll(grdc.getAllExtensions());
		}
		return allexts;
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
	public List<FieldDeclaration> getFields() {
		return this.fields;
	}
	public void addField(FieldDeclaration field) {
		this.fields.add(field);
	}
	public List<GroupDeclaration> getGroups() {
		return this.groups;
	}
	public void addGroup(GroupDeclaration group) {
		this.groups.add(group);
	}
	public List<OptionDeclaration> getOptions() {
		return this.options;
	}
	public void addOption(OptionDeclaration option) {
		this.options.add(option);
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
