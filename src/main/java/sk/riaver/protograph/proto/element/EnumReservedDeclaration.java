package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.proto.token.Comment;

public class EnumReservedDeclaration {
	
	private List<Comment> comments = new ArrayList<>();
	private List<Name> names = new ArrayList<>();
	private List<TagRange> ranges = new ArrayList<>();
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public List<Name> getNames() {
		return this.names;
	}
	public void addName(Name name) {
		this.names.add(name);
	}
	public List<TagRange> getRanges() {
		return this.ranges;
	}
	public void addRange(TagRange range) {
		this.ranges.add(range);
	}

}
