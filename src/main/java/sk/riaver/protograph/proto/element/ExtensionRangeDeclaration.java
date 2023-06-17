package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.proto.token.Comment;

public class ExtensionRangeDeclaration {
	
	private List<Comment> comments = new ArrayList<>();
	private List<TagRange> ranges = new ArrayList<>();
	private List<OptionDeclaration> compactOptions = new ArrayList<>();
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public List<TagRange> getRanges() {
		return this.ranges;
	}
	public void addRange(TagRange range) {
		this.ranges.add(range);
	}
	public List<OptionDeclaration> getCompactOptions() {
		return this.compactOptions;
	}
	public void addCompactOptions(OptionDeclaration option) {
		this.compactOptions.add(option);
	}

}
