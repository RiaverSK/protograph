package sk.riaver.protograph.proto.element;

import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public interface ProtoNode {
	
	final String ANY_TYPE = ".google.protobuf.Any";
	final String PROP_FULLNAME = "fullyQualifiedName";
	final String PROP_NUMBER = "number";
	final String PROP_RESERVED_NAMES = "reserved names";
	final String PROP_RESERVED_RANGES = "reserved ranges";
	final String PROP_EXTENSIONS = "extension ranges";
	
	ProtoFile getProtoFile();
	List<Comment> getComments();
	List<Property> getAllProperties();
	Name getName();
	ElementType getElementType();
	List<ProtoNode> getComplexContent();
	ProtoNode getRefContent();
	ScalarType getScalarType();

}
