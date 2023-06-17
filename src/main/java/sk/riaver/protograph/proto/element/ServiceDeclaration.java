package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class ServiceDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name name;
	private ProtoFile protoFile;
	private List<OptionDeclaration> options = new ArrayList<>();
	private List<MethodDeclaration> methods = new ArrayList<>();
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		for (MethodDeclaration mtdc : this.methods) {
			count += mtdc.countUnprocessedTypeNames();
		}
		return count;
	}
	
	public List<Comment> getAllComments() {
		return this.comments;
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
		return ElementType.SERVICE;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		ret.addAll(this.methods);
		return ret;
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
	public List<OptionDeclaration> getOptions() {
		return this.options;
	}
	public void addOption(OptionDeclaration option) {
		this.options.add(option);
	}
	public List<MethodDeclaration> getMethods() {
		return this.methods;
	}
	public void addMethod(MethodDeclaration method) {
		this.methods.add(method);
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
