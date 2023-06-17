package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

public class ListValue implements Value {
	
	private List<Value> values;
	
	public List<Value> getValues() {
		return this.values;
	}
	
	public void addValue(Value value) {
		if (this.values == null) {
			this.values = new ArrayList<>();
		}
		this.values.add(value);
	}
	
	public String toDisplay() {
		String ret = "";
		for (Value val : this.values) {
			ret += "," + val.toDisplay();
		}
		if (!ret.isEmpty()) {
			ret = ret.substring(1);
		}
		return "{" + ret + "}";
	}

}
