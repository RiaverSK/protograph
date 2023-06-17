package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class MethodDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name name;
	private Name inputTypeName;
	private Name outputTypeName;
	private Type inputType;
	private Type outputType;
	private boolean inputStream;
	private boolean outputStream;
	private List<OptionDeclaration> options = new ArrayList<>();
	private ProtoFile protoFile;
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		if (!this.inputTypeName.isProcessed()) {
			count++;
		}
		if (!this.outputTypeName.isProcessed()) {
			count++;
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
		return ElementType.METHOD;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		if (!(this.inputType instanceof ScalarType)) {
			ret.add(new MethodInOut("Input", this.inputType, this.inputStream, this.protoFile));
		}
		if (!(this.outputType instanceof ScalarType)) {
			ret.add(new MethodInOut("Output", this.outputType, this.outputStream, this.protoFile));
		}
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
	public boolean isInputStream() {
		return this.inputStream;
	}
	public void setInputStream(boolean inputStream) {
		this.inputStream = inputStream;
	}
	public boolean isOutputStream() {
		return this.outputStream;
	}
	public void setOutputStream(boolean outputStream) {
		this.outputStream = outputStream;
	}
	public List<OptionDeclaration> getOptions() {
		return this.options;
	}
	public void addOption(OptionDeclaration option) {
		this.options.add(option);
	}
	public Name getInputTypeName() {
		return this.inputTypeName;
	}
	public void setInputTypeName(Name inputTypeName) {
		this.inputTypeName = inputTypeName;
	}
	public Name getOutputTypeName() {
		return this.outputTypeName;
	}
	public void setOutputTypeName(Name outputTypeName) {
		this.outputTypeName = outputTypeName;
	}
	public Type getInputType() {
		return this.inputType;
	}
	public void setInputType(Type inputType) {
		this.inputType = inputType;
	}
	public Type getOutputType() {
		return this.outputType;
	}
	public void setOutputType(Type outputType) {
		this.outputType = outputType;
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
