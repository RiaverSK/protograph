package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.component.ElementType;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.proto.token.KeywordEnum;
import sk.riaver.protograph.util.Property;

public class GroupDeclaration implements ProtoNode {
	
	private List<Comment> comments = new ArrayList<>();
	private KeywordEnum cardinality;
	private Name name;
	private String number;
	private List<OptionDeclaration> compactOptions = new ArrayList<>();
	private List<FieldDeclaration> fields = new ArrayList<>();
	private List<MapFieldDeclaration> mapfields = new ArrayList<>();
	private List<GroupDeclaration> groups = new ArrayList<>();
	private List<OneofDeclaration> oneofs = new ArrayList<>();
	private List<OptionDeclaration> options = new ArrayList<>();
	private List<ExtensionRangeDeclaration> extensionRanges = new ArrayList<>();
	private List<MessageReservedDeclaration> messageReserveds = new ArrayList<>();
	private List<MessageDeclaration> messages = new ArrayList<>();
	private List<EnumDeclaration> enums = new ArrayList<>();
	private List<ExtensionDeclaration> extensions = new ArrayList<>();
	private ProtoFile protoFile;
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		for (FieldDeclaration fldc : this.fields) {
			count += fldc.countUnprocessedTypeNames();
		}
		for (MapFieldDeclaration mfdc : this.mapfields) {
			count += mfdc.countUnprocessedTypeNames();
		}
		for (GroupDeclaration grdc : this.groups) {
			count += grdc.countUnprocessedTypeNames();
		}
		for (OneofDeclaration oodc : this.oneofs) {
			count += oodc.countUnprocessedTypeNames();
		}
		for (MessageDeclaration msdc : this.messages) {
			count += msdc.countUnprocessedTypeNames();
		}
		for (ExtensionDeclaration exdc : this.extensions) {
			count += exdc.countUnprocessedTypeNames();
		}
		return count;
	}
	
	public List<Property> getAllProperties() {
		List<Property> props = new ArrayList<>();
		props.add(new Property(PROP_FULLNAME, this.name.getProcessedName()));
		props.add(new Property(PROP_NUMBER, this.number));
		for (OptionDeclaration opdc : this.compactOptions) {
			String opname = "";
			for (Name name : opdc.getNames()) {
				opname += "." + name.toDisplay();
			}
			props.add(new Property(opname.substring(1), opdc.getValue().toDisplay()));
		}
		for (OptionDeclaration opdc : this.options) {
			String opname = "";
			for (Name name : opdc.getNames()) {
				opname += "." + name.toDisplay();
			}
			props.add(new Property(opname.substring(1), opdc.getValue().toDisplay()));
		}
		return props;
	}
	
	public ElementType getElementType() {
		return ElementType.GROUP;
	}
	
	public List<ProtoNode> getComplexContent() {
		List<ProtoNode> ret = new ArrayList<>();
		ret.addAll(this.fields);
		ret.addAll(this.mapfields);
		ret.addAll(this.groups);
		ret.addAll(this.oneofs);
		return ret;
	}
	
	public ProtoNode getRefContent() {
		return null;
	}
	
	public ScalarType getScalarType() {
		return null;
	}
	
	public List<MessageReservedDeclaration> getAllReserveds() {
		List<MessageReservedDeclaration> allreserveds = new ArrayList<>();
		allreserveds.addAll(this.messageReserveds);
		for (GroupDeclaration grdc : this.groups) {
			allreserveds.addAll(grdc.getAllReserveds());
		}
		for (OneofDeclaration oodc : this.oneofs) {
			allreserveds.addAll(oodc.getAllReserveds());
		}
		return allreserveds;
	}
	
	public List<ExtensionRangeDeclaration> getAllExtensions() {
		List<ExtensionRangeDeclaration> allexts = new ArrayList<>();
		allexts.addAll(this.extensionRanges);
		for (GroupDeclaration grdc : this.groups) {
			allexts.addAll(grdc.getAllExtensions());
		}
		for (OneofDeclaration oodc : this.oneofs) {
			allexts.addAll(oodc.getAllExtensions());
		}
		return allexts;
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
	public KeywordEnum getCardinality() {
		return this.cardinality;
	}
	public void setCardinality(KeywordEnum cardinality) {
		this.cardinality = cardinality;
	}
	public Name getName() {
		return this.name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public String getNumber() {
		return this.number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public List<OptionDeclaration> getCompactOptions() {
		return this.compactOptions;
	}
	public void addCompactOption(OptionDeclaration compactOption) {
		this.compactOptions.add(compactOption);
	}
	public List<FieldDeclaration> getFields() {
		return this.fields;
	}
	public void addField(FieldDeclaration field) {
		this.fields.add(field);
	}
	public List<MapFieldDeclaration> getMapfields() {
		return this.mapfields;
	}
	public void addMapfield(MapFieldDeclaration mapfield) {
		this.mapfields.add(mapfield);
	}
	public List<GroupDeclaration> getGroups() {
		return this.groups;
	}
	public void addGroup(GroupDeclaration group) {
		this.groups.add(group);
	}
	public List<OneofDeclaration> getOneofs() {
		return this.oneofs;
	}
	public void addOneof(OneofDeclaration oneof) {
		this.oneofs.add(oneof);
	}
	public List<OptionDeclaration> getOptions() {
		return this.options;
	}
	public void addOption(OptionDeclaration option) {
		this.options.add(option);
	}
	public List<ExtensionRangeDeclaration> getExtensionRanges() {
		return this.extensionRanges;
	}
	public void addExtensionRange(ExtensionRangeDeclaration extensionRange) {
		this.extensionRanges.add(extensionRange);
	}
	public List<MessageReservedDeclaration> getMessageReserveds() {
		return this.messageReserveds;
	}
	public void addMessageReserved(MessageReservedDeclaration messageReserved) {
		this.messageReserveds.add(messageReserved);
	}
	public List<MessageDeclaration> getMessages() {
		return this.messages;
	}
	public void addMessage(MessageDeclaration message) {
		this.messages.add(message);
	}
	public List<EnumDeclaration> getEnums() {
		return this.enums;
	}
	public void addEnum(EnumDeclaration enumdc) {
		this.enums.add(enumdc);
	}
	public List<ExtensionDeclaration> getExtensions() {
		return this.extensions;
	}
	public void addExtension(ExtensionDeclaration extension) {
		this.extensions.add(extension);
	}
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	public void setProtoFile(ProtoFile protoFile) {
		this.protoFile = protoFile;
	}

}
