package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class MethodInOut implements ProtoNode {
	
	private Type inoutType;
	private Name name;
	private boolean inoutStream;
	private ProtoFile protoFile;
	
	public MethodInOut(String inout, Type inoutType, boolean inoutStream, ProtoFile protoFile) {
		this.protoFile = protoFile;
		this.inoutType = inoutType;
		this.inoutStream = inoutStream;
		this.name = new Name();
		this.name.addQualifiedName(inout);
	}

	@Override
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}

	@Override
	public List<Comment> getComments() {
		return new ArrayList<>();
	}

	@Override
	public List<Property> getAllProperties() {
		List<Property> ret = new ArrayList<>();
		ret.add(new Property("stream", String.valueOf(this.inoutStream)));
		return ret;
	}

	@Override
	public Name getName() {
		return this.name;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.METHODINOUT;
	}

	@Override
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		if (!(this.inoutType instanceof ScalarType)) {
			ret.add((ProtoNode) this.inoutType);
		}
		return ret;
	}

	@Override
	public ProtoNode getRefContent() {
		if (!(this.inoutType instanceof ScalarType)) {
			return (ProtoNode) this.inoutType;
		}
		return null;
	}

	@Override
	public ScalarType getScalarType() {
		if (this.inoutType instanceof ScalarType) {
			return (ScalarType) this.inoutType;
		}
		return null;
	}

}
