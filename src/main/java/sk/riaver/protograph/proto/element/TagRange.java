package sk.riaver.protograph.proto.element;

public class TagRange {
	
	private String start;
	private String end;
	
	public String getStart() {
		return this.start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return this.end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	public String toDisplay() {
		String ret = this.start;
		if (this.end != null) {
			ret += "-" + this.end;
		}
		return ret;
	}

}
