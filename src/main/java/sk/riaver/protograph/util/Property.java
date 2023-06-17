package sk.riaver.protograph.util;

public class Property {
	
	private String name;
	private String value;
	
	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}

}
