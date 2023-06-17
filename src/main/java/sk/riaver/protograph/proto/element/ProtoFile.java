package sk.riaver.protograph.proto.element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;

public class ProtoFile {
	
	public static final String DEFAULT_PACKAGE = "";
	public static final String DEFAULT_SYNTAX = "proto2";
	
	private File sourceFile;
	private String syntax;
	private Name protopackage;
	private List<Comment> comments = new ArrayList<>();
	private List<ImportDeclaration> imports = new ArrayList<>();
	private List<OptionDeclaration> options = new ArrayList<>();
	private List<MessageDeclaration> messages = new ArrayList<>();
	private List<EnumDeclaration> enums = new ArrayList<>();
	private List<ExtensionDeclaration> extensions = new ArrayList<>();
	private List<MessageDeclaration> allmessages = new ArrayList<>();
	private List<EnumDeclaration> allenums = new ArrayList<>();
	private List<ExtensionDeclaration> allextensions = new ArrayList<>();
	private List<ServiceDeclaration> services = new ArrayList<>();
	private Map<String, Type> localTypes = new HashMap<>();
	private Map<String, Type> visibleTypes = new HashMap<>();
	
	public ProtoFile() {
		this.protopackage = Name.DEFAULT_PACKAGE_NAME;
		this.syntax = DEFAULT_SYNTAX;
		this.visibleTypes.putAll(ScalarType.getScalarTypes());
	}
	
	public int countUnprocessedTypeNames() {
		int count = 0;
		for (MessageDeclaration msdc : this.messages) {
			count += msdc.countUnprocessedTypeNames();
		}
		for (ExtensionDeclaration exdc : this.extensions) {
			count += exdc.countUnprocessedTypeNames();
		}
		for (ServiceDeclaration srdc : this.services) {
			count += srdc.countUnprocessedTypeNames();
		}
		return count;
	}
	
	public List<Property> getAllProperties() {
		List<Property> props = new ArrayList<>();
		props.add(new Property("syntax", this.syntax));
		props.add(new Property("package", this.protopackage.toDisplay()));
		for (OptionDeclaration opdc : this.options) {
			String opname = "";
			for (Name name : opdc.getNames()) {
				opname += "." + name.toDisplay();
			}
			props.add(new Property(opname.substring(1), opdc.getValue().toDisplay()));
		}
		return props;
	}
	
	public boolean isProto2Syntax() {
		return "proto2".equals(this.syntax);
	}
	public boolean isProto3Syntax() {
		return "proto3".equals(this.syntax);
	}
	public File getSourceFile() {
		return this.sourceFile;
	}
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getSyntax() {
		return this.syntax;
	}
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	public Name getProtopackage() {
		return this.protopackage;
	}
	public void setProtopackage(Name protopackage) {
		this.protopackage = protopackage;
	}
	public List<Comment> getComments() {
		return this.comments;
	}
	public List<ImportDeclaration> getImports() {
		return this.imports;
	}
	public void addImport(ImportDeclaration importdecl) {
		this.imports.add(importdecl);
	}
	public List<OptionDeclaration> getOptions() {
		return this.options;
	}
	public void addOption(OptionDeclaration option) {
		this.options.add(option);
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
	public void addEnum(EnumDeclaration enumdecl) {
		this.enums.add(enumdecl);
	}
	public List<ExtensionDeclaration> getExtensions() {
		return this.extensions;
	}
	public void addExtension(ExtensionDeclaration extension) {
		this.extensions.add(extension);
	}
	public List<MessageDeclaration> getAllMessages() {
		return this.allmessages;
	}
	public void addToAllMessage(MessageDeclaration message) {
		this.allmessages.add(message);
	}
	public List<EnumDeclaration> getAllEnums() {
		return this.allenums;
	}
	public void addToAllEnum(EnumDeclaration enumdecl) {
		this.allenums.add(enumdecl);
	}
	public List<ExtensionDeclaration> getAllExtensions() {
		return this.allextensions;
	}
	public void addToAllExtension(ExtensionDeclaration extension) {
		this.allextensions.add(extension);
	}
	public List<ServiceDeclaration> getServices() {
		return this.services;
	}
	public void addService(ServiceDeclaration service) {
		this.services.add(service);
	}
	public Map<String, Type> getLocalTypes() {
		return this.localTypes;
	}
	public void setLocalTypes(Map<String, Type> localTypes) {
		this.localTypes = localTypes;
	}
	public Map<String, Type> getVisibleTypes() {
		return this.visibleTypes;
	}
	public void setVisibleTypes(Map<String, Type> visibleTypes) {
		this.visibleTypes = visibleTypes;
	}

}
