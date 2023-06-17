package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class ExtensionDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name messageName;
	private Type messageType;
	private List<FieldDeclaration> fields = new ArrayList<>();
	private List<GroupDeclaration> groups = new ArrayList<>();
	private ProtoFile protoFile;
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		if (!this.messageName.isProcessed()) {
			count++;
		}
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
		
		return props;
	}
	
	public ElementType getElementType() {
		return ElementType.EXTENSION;
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
	
	public Name getName() {
		return null;
	}
	
	@Override
	public String toString() {
		return "extension";
	}
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public Name getMessageName() {
		return this.messageName;
	}
	public void setMessageName(Name messageName) {
		this.messageName = messageName;
	}
	public Type getMessageType() {
		return this.messageType;
	}
	public void setMessageType(Type messageType) {
		this.messageType = messageType;
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
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
