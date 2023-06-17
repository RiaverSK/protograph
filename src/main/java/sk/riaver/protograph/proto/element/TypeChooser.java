package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class TypeChooser implements ProtoNode {
	
	private ProtoNode anyType;
	private ProtoFile protoFile;
	
	public TypeChooser(ProtoFile protoFile) {
		this.protoFile = protoFile;
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
		return new ArrayList<>();
	}

	@Override
	public Name getName() {
		return null;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.TYPECHOOSER;
	}

	@Override
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		ret.add(this.anyType);
		return ret;
	}

	@Override
	public ProtoNode getRefContent() {
		return null;
	}

	@Override
	public ScalarType getScalarType() {
		return null;
	}
	
	public ProtoNode getAnyType() {
		return this.anyType;
	}
	
	public void setAnyType(ProtoNode anyType) {
		this.anyType = anyType;
	}

}
