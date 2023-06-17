package sk.riaver.protograph.proto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sk.riaver.protograph.proto.element.*;

public final class ProtoProcessor {
	
	private static Log log = LogFactory.getLog(ProtoProcessor.class);
	
	private ProtoProcessor() { }
	
	public static void processProtoFileNames(ProtoFile protoFile) {
		Name parentScope = protoFile.getProtopackage();
		for (EnumDeclaration endc : protoFile.getEnums()) {
			processEnumNames(endc, parentScope, protoFile);
		}
		for (MessageDeclaration msdc : protoFile.getMessages()) {
			processMessageNames(msdc, parentScope, protoFile);
		}
		for (ExtensionDeclaration exdc : protoFile.getExtensions()) {
			processExtensionNames(exdc, parentScope, protoFile);
		}
		for (ServiceDeclaration srdc : protoFile.getServices()) {
			processServiceNames(srdc, parentScope, protoFile);
		}
	}
	
	public static void processEnumNames(EnumDeclaration endc, Name parentScope, ProtoFile protoFile) {
		if (!endc.getName().isProcessed()) {
			endc.getName().setScope(parentScope);
			protoFile.getLocalTypes().put(endc.getName().getProcessedName(), endc);
			endc.setProtoFile(protoFile);
			protoFile.addToAllEnum(endc);
		}
		for (EnumValueDeclaration evdc : endc.getEnumValues()) {
			if (!evdc.getName().isProcessed()) {
				evdc.getName().setScope(parentScope);
				evdc.setProtoFile(protoFile);
			}
		}
	}
	
	public static void processMessageNames(MessageDeclaration msdc, Name parentScope, ProtoFile protoFile) {
		if (!msdc.getName().isProcessed()) {
			msdc.getName().setScope(parentScope);
			protoFile.getLocalTypes().put(msdc.getName().getProcessedName(), msdc);
			msdc.setProtoFile(protoFile);
			protoFile.addToAllMessage(msdc);
		}
		for (EnumDeclaration endc : msdc.getEnums()) {
			processEnumNames(endc, parentScope.concatenate(msdc.getName()), protoFile);
		}
		for (MessageDeclaration mssdc : msdc.getMessages()) {
			processMessageNames(mssdc, parentScope.concatenate(msdc.getName()), protoFile);
		}
		for (FieldDeclaration fldc : msdc.getFields()) {
			processFieldNames(fldc, parentScope.concatenate(msdc.getName()), protoFile);
		}
		for (MapFieldDeclaration mfdc : msdc.getMapfields()) {
			processMapFieldNames(mfdc, parentScope.concatenate(msdc.getName()), protoFile);
		}
		for (GroupDeclaration grdc : msdc.getGroups()) {
			processGroupNames(grdc, parentScope.concatenate(msdc.getName()), protoFile);
		}
		for (OneofDeclaration oodc : msdc.getOneofs()) {
			processOneofNames(oodc, parentScope.concatenate(msdc.getName()), protoFile);
		}
		for (ExtensionDeclaration exdc : msdc.getExtensions()) {
			processExtensionNames(exdc, parentScope.concatenate(msdc.getName()), protoFile);
		}
	}
	
	public static void processFieldNames(FieldDeclaration fldc, Name parentScope, ProtoFile protoFile) {
		if (!fldc.getName().isProcessed()) {
			fldc.getName().setScope(parentScope);
			fldc.setProtoFile(protoFile);
		}
		if (!fldc.getTypeName().isProcessed()) {
			Type recognizedType = processRelativeReference(parentScope, fldc.getTypeName(), protoFile);
			if (recognizedType != null) {
				fldc.setFieldType(recognizedType);
				fldc.getTypeName().setProcessedName(recognizedType.getName().getProcessedName());
			}
		}
	}
	
	public static void processMapFieldNames(MapFieldDeclaration mfdc, Name parentScope, ProtoFile protoFile) {
		if (!mfdc.getName().isProcessed()) {
			mfdc.getName().setScope(parentScope);
			mfdc.setProtoFile(protoFile);
		}
		if (!mfdc.getMapKeyTypeName().isProcessed()) {
			Type recognizedType = processRelativeReference(parentScope, mfdc.getMapKeyTypeName(), protoFile);
			if (recognizedType != null) {
				mfdc.setMapKeyType(recognizedType);
				mfdc.getMapKeyTypeName().setProcessedName(recognizedType.getName().getProcessedName());
			}
		}
		if (!mfdc.getMapValueTypeName().isProcessed()) {
			Type recognizedType = processRelativeReference(parentScope, mfdc.getMapValueTypeName(), protoFile);
			if (recognizedType != null) {
				mfdc.setMapValueType(recognizedType);
				mfdc.getMapValueTypeName().setProcessedName(recognizedType.getName().getProcessedName());
			}
		}
	}
	
	public static void processGroupNames(GroupDeclaration grdc, Name parentScope, ProtoFile protoFile) {
		if (!grdc.getName().isProcessed()) {
			grdc.getName().setScope(parentScope);
			grdc.setProtoFile(protoFile);
		}
		for (EnumDeclaration endc : grdc.getEnums()) {
			processEnumNames(endc, parentScope.concatenate(grdc.getName()), protoFile);
		}
		for (MessageDeclaration mssdc : grdc.getMessages()) {
			processMessageNames(mssdc, parentScope.concatenate(grdc.getName()), protoFile);
		}
		for (FieldDeclaration fldc : grdc.getFields()) {
			processFieldNames(fldc, parentScope.concatenate(grdc.getName()), protoFile);
		}
		for (MapFieldDeclaration mfdc : grdc.getMapfields()) {
			processMapFieldNames(mfdc, parentScope.concatenate(grdc.getName()), protoFile);
		}
		for (GroupDeclaration grrdc : grdc.getGroups()) {
			processGroupNames(grrdc, parentScope.concatenate(grdc.getName()), protoFile);
		}
		for (OneofDeclaration oodc : grdc.getOneofs()) {
			processOneofNames(oodc, parentScope.concatenate(grdc.getName()), protoFile);
		}
		for (ExtensionDeclaration exdc : grdc.getExtensions()) {
			processExtensionNames(exdc, parentScope.concatenate(grdc.getName()), protoFile);
		}
	}
	
	public static void processOneofNames(OneofDeclaration oodc, Name parentScope, ProtoFile protoFile) {
		if (!oodc.getName().isProcessed()) {
			oodc.getName().setScope(parentScope);
			oodc.setProtoFile(protoFile);
		}
		for (FieldDeclaration fldc : oodc.getFields()) {
			processFieldNames(fldc, parentScope, protoFile);
		}
		for (GroupDeclaration grdc : oodc.getGroups()) {
			processGroupNames(grdc, parentScope, protoFile);
		}
	}
	
	public static void processExtensionNames(ExtensionDeclaration exdc, Name parentScope, ProtoFile protoFile) {
		if (!exdc.getMessageName().isProcessed()) {
			Type recognizedType = processRelativeReference(parentScope, exdc.getMessageName(), protoFile);
			if (recognizedType != null) {
				exdc.setMessageType(recognizedType);
				exdc.getMessageName().setProcessedName(recognizedType.getName().getProcessedName());
				if (recognizedType instanceof MessageDeclaration) {
					((MessageDeclaration) recognizedType).addExtendedBy(exdc);
				}
				exdc.setProtoFile(protoFile);
				protoFile.addToAllExtension(exdc);
			}
		}
		for (FieldDeclaration fldc : exdc.getFields()) {
			processFieldNames(fldc, parentScope, protoFile);
		}
		for (GroupDeclaration grdc : exdc.getGroups()) {
			processGroupNames(grdc, parentScope, protoFile);
		}
	}
	
	public static void processServiceNames(ServiceDeclaration srdc, Name parentScope, ProtoFile protoFile) {
		if (!srdc.getName().isProcessed()) {
			srdc.getName().setScope(parentScope);
			srdc.setProtoFile(protoFile);
		}
		for (MethodDeclaration mtdc : srdc.getMethods()) {
			if (!mtdc.getName().isProcessed()) {
				mtdc.getName().setScope(parentScope.concatenate(srdc.getName()));
				mtdc.setProtoFile(protoFile);
			}
			if (!mtdc.getInputTypeName().isProcessed()) {
				Type recognizedType = processRelativeReference(parentScope, mtdc.getInputTypeName(), protoFile);
				if (recognizedType != null) {
					mtdc.setInputType(recognizedType);
					mtdc.getInputTypeName().setProcessedName(recognizedType.getName().getProcessedName());
				}
			}
			if (!mtdc.getOutputTypeName().isProcessed()) {
				Type recognizedType = processRelativeReference(parentScope, mtdc.getOutputTypeName(), protoFile);
				if (recognizedType != null) {
					mtdc.setOutputType(recognizedType);
					mtdc.getOutputTypeName().setProcessedName(recognizedType.getName().getProcessedName());
				}
			}
		}
	}
	
	public static Type processRelativeReference(Name parentScope, Name relativ, ProtoFile protoFile) {
		Type recognized = null;
		if (relativ.isSimpleName() && relativ.getKey() != null && protoFile.getVisibleTypes().containsKey(relativ.getSimpleName())) {
			recognized = protoFile.getVisibleTypes().get(relativ.getSimpleName());
		} else {
			Name scope = new Name();
			scope.addQualifiedName("toberemoved");
			scope = parentScope.concatenate(scope);
			do {
				scope = scope.subtractLast();
				String candidate = scope.toTypeName() + "." + relativ.toTypeName();
				if (protoFile.getLocalTypes().containsKey(candidate)) {
					recognized = protoFile.getLocalTypes().get(candidate);
					break;
				} else if (protoFile.getVisibleTypes().containsKey(candidate)) {
					recognized = protoFile.getVisibleTypes().get(candidate);
					break;
				}
			} while (!scope.isDefaultPackage());
		}
		if (recognized == null) {
			log.debug("Unknown type: " + relativ.toDisplay());
		}
		return recognized;
	}

}
