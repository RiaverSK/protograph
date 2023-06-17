package sk.riaver.protograph.proto.element;

public class FieldValue implements Value {
	
	private Name name;
	private Value value;
	
	public Name getName() {
		return this.name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public Value getValue() {
		return this.value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	
	public String toDisplay() {
		return this.name.toDisplay() + ":" + this.value.toDisplay();
	}

}
