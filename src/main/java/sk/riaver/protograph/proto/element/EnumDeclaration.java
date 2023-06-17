package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class EnumDeclaration implements Type, ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private Name name;
	private List<OptionDeclaration> options = new ArrayList<>();
	private List<EnumValueDeclaration> values = new ArrayList<>();
	private List<EnumReservedDeclaration> reserveds = new ArrayList<>();
	private ProtoFile protoFile;
	
	public List<Property> getAllProperties() {
		List<Property> props = new ArrayList<>();
		props.add(new Property(PROP_FULLNAME, this.name.getProcessedName()));
		for (OptionDeclaration opdc : this.options) {
			String opname = "";
			for (Name name : opdc.getNames()) {
				opname += "." + name.toDisplay();
			}
			props.add(new Property(opname.substring(1), opdc.getValue().toDisplay()));
		}
		List<String> names = new ArrayList<>();
		List<String> ranges = new ArrayList<>();
		for (EnumReservedDeclaration erdc : reserveds) {
			for (Name nm : erdc.getNames()) {
				names.add(nm.toDisplay());
			}
			for (TagRange tr : erdc.getRanges()) {
				ranges.add(tr.toDisplay());
			}
		}
		if (!names.isEmpty()) {
			Collections.sort(names);
			String nms = "";
			for (String nm : names) {
				nms += ", " + nm;
			}
			props.add(new Property(PROP_RESERVED_NAMES, nms.substring(2)));
		}
		if (!ranges.isEmpty()) {
			Collections.sort(ranges);
			String rgs = "";
			for (String rg : ranges) {
				rgs += ", " + rg;
			}
			props.add(new Property(PROP_RESERVED_RANGES, rgs.substring(2)));
		}
		return props;
	}
	
	public ElementType getElementType() {
		return ElementType.ENUM;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		ret.addAll(this.values);
		return ret;
	}
	
	public ProtoNode getRefContent() {
		return null;
	}
	
	public ScalarType getScalarType() {
		return null;
	}
	
	@Override
	public String toString() {
		return this.name.toDisplay();
	}
	
	public List<Comment> getComments() {
		return this.comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public Name getName() {
		return this.name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public List<OptionDeclaration> getOptions() {
		return this.options;
	}
	public void addOption(OptionDeclaration option) {
		this.options.add(option);
	}
	public List<EnumValueDeclaration> getEnumValues() {
		return this.values;
	}
	public void addEnumValue(EnumValueDeclaration value) {
		this.values.add(value);
	}
	public List<EnumReservedDeclaration> getEnumReserveds() {
		return this.reserveds;
	}
	public void addEnumReserved(EnumReservedDeclaration reserved) {
		this.reserveds.add(reserved);
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
