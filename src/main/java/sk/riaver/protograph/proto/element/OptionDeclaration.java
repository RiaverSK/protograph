package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.proto.token.Comment;

public class OptionDeclaration {
	
	private List<Comment> comments = new ArrayList<>();
	private List<Name> names;
	private Value value;
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public List<Name> getNames() {
		return this.names;
	}
	public void setNames(List<Name> names) {
		this.names = names;
	}
	public Value getValue() {
		return this.value;
	}
	public void setValue(Value value) {
		this.value = value;
	}

}
