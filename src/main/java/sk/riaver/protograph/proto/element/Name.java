package sk.riaver.protograph.proto.element;

import java.util.ArrayList;
import java.util.List;

import sk.riaver.protograph.proto.token.KeywordEnum;

public class Name {
	
	public static final Name DEFAULT_PACKAGE_NAME = new Name();
	
	private KeywordEnum key;
	private List<String> qualifiedNames = new ArrayList<>();
	private String url;
	private boolean fullyQualified;
	private boolean extensionName;
	private String processedName;
	
	static {
		DEFAULT_PACKAGE_NAME.qualifiedNames.add(ProtoFile.DEFAULT_PACKAGE);
		DEFAULT_PACKAGE_NAME.processedName = ProtoFile.DEFAULT_PACKAGE;
		DEFAULT_PACKAGE_NAME.fullyQualified = true;
	}
	
	public boolean isSimpleName() {
		return this.qualifiedNames.size() == 1;
	}
	
	public boolean isKeyword() {
		return this.key != null;
	}
	
	public boolean isQualifiedName() {
		return this.qualifiedNames != null && !this.qualifiedNames.isEmpty();
	}
	
	public boolean isTypeUrl() {
		return this.url != null;
	}
	
	public boolean isProcessed() {
		return this.processedName != null;
	}
	
	public String toDisplay() {
		String name = "";
//		if (this.isTypeUrl()) {
//			String names = "";
//			if (this.isSimpleName()) {
//				names = "." + this.simpleName;
//			} else if (this.isQualifiedName()) {
//				for (String n : this.qualifiedNames) {
//					names += "." + n;
//				}
//			}
//			name = this.url + "/" + names.substring(1);
//		} else
		if (this == DEFAULT_PACKAGE_NAME) {
			return "<default>";
		}
		if (this.isSimpleName()) {
			name = this.getSimpleName();
		} else if (this.isQualifiedName()) {
			for (String n : this.qualifiedNames) {
				name += "." + n;
			}
			name = name.substring(1);
		}
		if ("".equals(name)) {
			name = "!!!InvalidName!!!";
		}
		if (this.isFullyQualified()) {
			name = "." + name;
		}
		if (this.isExtensionName()) {
			name = "(" + name + ")";
		}
		return name;
	}
	
	public String getSimpleName() {
		return this.qualifiedNames.get(0);
	}
	public List<String> getQualifiedNames() {
		return this.qualifiedNames;
	}
	public void addQualifiedName(String qualifiedName) {
		if (this.qualifiedNames == null) {
			this.qualifiedNames = new ArrayList<>();
		}
		this.qualifiedNames.add(qualifiedName);
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isFullyQualified() {
		return this.fullyQualified;
	}
	public void setFullyQualified(boolean fullyQualified) {
		this.fullyQualified = fullyQualified;
		if (fullyQualified) {
//			if (this.isTypeUrl()) {
//				
//			} else
			if (this.isSimpleName()) {
				this.processedName = "." + this.getSimpleName();
			} else if (this.isQualifiedName()) {
				String name = "";
				for (String n : this.qualifiedNames) {
					name += "." + n;
				}
				this.processedName = name;
			}
		}
	}
	public boolean isExtensionName() {
		return this.extensionName;
	}
	public void setExtensionName(boolean extensionName) {
		this.extensionName = extensionName;
	}
	public String getProcessedName() {
		return this.processedName;
	}
	public void setProcessedName(String processedName) {
		this.processedName = processedName;
	}
	public KeywordEnum getKey() {
		return this.key;
	}
	public void setKey(KeywordEnum key) {
		this.key = key;
	}
	public void setScope(Name scope) {
		this.processedName = scope.toTypeName() + "." + this.toTypeName();
	}
	public String toTypeName() {
		String ret = "";
		for (String nm : this.qualifiedNames) {
			ret += "." + nm;
		}
		if (!this.fullyQualified && ret.length() > 0) {
			ret = ret.substring(1);
		} else if (".".equals(ret)) {
			ret = "";
		}
		return ret;
	}
	public Name concatenate(Name apendix) {
		Name ret = new Name();
		if (!this.isSimpleName() || !"".equals(this.qualifiedNames.get(0))) {
			ret.qualifiedNames.addAll(this.qualifiedNames);
		}
		ret.qualifiedNames.addAll(apendix.qualifiedNames);
		ret.fullyQualified = true;
		return ret;
	}
	public Name subtractLast() {
		Name ret = new Name();
		if (this.qualifiedNames.size() > 1) {
			ret.qualifiedNames.addAll(this.qualifiedNames);
			ret.qualifiedNames.remove(ret.qualifiedNames.size()-1);
			ret.fullyQualified = true;
		} else {
			ret = DEFAULT_PACKAGE_NAME;
		}
		return ret;
	}
	public boolean isDefaultPackage() {
		return this.isSimpleName() && ProtoFile.DEFAULT_PACKAGE.equals(this.qualifiedNames.get(0));
	}

}
