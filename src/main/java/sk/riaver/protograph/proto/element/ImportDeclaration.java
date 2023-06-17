package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.proto.token.Comment;

public class ImportDeclaration {
	
	private List<Comment> comments = new ArrayList<>();
	private String filename;
	private ProtoFile protoFile;
	private boolean publicFlag;
	private boolean weakFlag;
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public boolean isPublic() {
		return this.publicFlag;
	}
	public void setPublic(boolean publicFlag) {
		this.publicFlag = publicFlag;
	}
	public boolean isWeak() {
		return this.weakFlag;
	}
	public void setWeak(boolean weakFlag) {
		this.weakFlag = weakFlag;
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
