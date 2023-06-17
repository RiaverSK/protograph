package sk.riaver.protograph.proto.element;

public class ScalarValue implements Value {
	
	private String scalarValue;
	
	public String getScalarValue() {
		return this.scalarValue;
	}
	
	public void setScalarValue(String scalarValue) {
		this.scalarValue = scalarValue;
	}
	
	public String toDisplay() {
		return this.scalarValue;
	}

}
