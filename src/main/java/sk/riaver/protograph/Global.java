package sk.riaver.protograph;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sk.riaver.protograph.proto.ProtoParser;
import sk.riaver.protograph.proto.ProtoProcessor;
import sk.riaver.protograph.proto.ProtoTokenizer;
import sk.riaver.protograph.proto.element.ImportDeclaration;
import sk.riaver.protograph.proto.element.ProtoFile;
import sk.riaver.protograph.proto.element.ProtoNode;

public class Global {
	
	private static Log log = LogFactory.getLog(Global.class);
	
	private static Map<String, ProtoFile> loadedFiles = new HashMap<>();
	private static Map<String, List<ProtoFile>> packages = new HashMap<>();
	private static String[] commandLineArgs;
	private static String lastNotProcessedProtoFile;
	private static String lastDirectory;
	private static String version;
	private static String buildTime;
	
	private Global() { }
	
	public static ProtoFile loadProtoFile(File file) {
		char[] chars = new char[(int) file.length()];
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			fr.read(chars);
		} catch (Exception e) {
			log.error("File reading failed: " + file.getName(), e);
			throw new RuntimeException("File reading failed: " + file.getName());
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception ex) { }
			}
		}
		ProtoFile protofile = ProtoParser.parseProtofile(new ProtoTokenizer(chars));
		protofile.setSourceFile(file);
		
		if (protofile.getImports().size() > 0) {
			for (ImportDeclaration imdc : protofile.getImports()) {
				File imfile = findImportFile(protofile.getSourceFile(), imdc.getFilename());
				ProtoFile impfile = null;
				if (imfile != null) {
					if (loadedFiles.containsKey(imfile.getAbsolutePath())) {
						impfile = loadedFiles.get(imfile.getAbsolutePath());
					} else {
						impfile = loadProtoFile(imfile);
					}
				}
				if (impfile == null) {
					return null;
				}
				imdc.setProtoFile(impfile);
				if (imdc.isPublic()) {
					protofile.getLocalTypes().putAll(impfile.getLocalTypes());
				} else {
					protofile.getVisibleTypes().putAll(impfile.getLocalTypes());
				}
			}
		}
		
		int oldcount = Integer.MAX_VALUE;
		int newcount = Integer.MAX_VALUE;
		do {
			oldcount = newcount;
			ProtoProcessor.processProtoFileNames(protofile);
			newcount = protofile.countUnprocessedTypeNames();
		} while (newcount < oldcount && newcount > 0);
		log.debug("Unrecognized names count: " + newcount);
		if (newcount > 0) {
			lastNotProcessedProtoFile = file.getName();
//			throw new RuntimeException("Unrecognized types");
			return null;
		}
		loadedFiles.put(file.getAbsolutePath(), protofile);
		List<ProtoFile> pcg = packages.get(protofile.getProtopackage().toDisplay());
		if (pcg == null) {
			pcg = new ArrayList<>();
			packages.put(protofile.getProtopackage().toDisplay(), pcg);
		}
		pcg.add(protofile);
		return protofile;
	}
	
	public static File findImportFile(File sourceFile, String importFileName) {
		File ret = null;
		// try to find import file in source file directory/ies
		File dir = sourceFile.getAbsoluteFile();
		do {
			dir = dir.getParentFile();
			log.debug("Looking in: " + dir.getPath());
			ret = new File(dir.getAbsolutePath() + "/" + importFileName);
		} while ((!ret.exists() || !ret.isFile()) && dir.getParent() != null);
		if (!ret.exists() || !ret.isFile()) {
			ret = null;
		}
		if (ret == null) {
			// try to find import file in proto directory
			ret = new File("proto/" + importFileName);
			if (!ret.exists() || !ret.isFile()) {
				ret = null;
			}
		}
		if (ret == null) {
			lastNotProcessedProtoFile = importFileName;
			log.debug("Import file cannot be reached: " + importFileName);
		}
		return ret;
	}
	
	public static Map<String, ProtoFile> getLoadedFiles() {
		return loadedFiles;
	}
	
	public static Map<String, List<ProtoFile>> getPackages() {
		return packages;
	}

	public static String[] getCommandLineArgs() {
		return commandLineArgs;
	}

	public static void setCommandLineArgs(String[] args) {
		commandLineArgs = args;
	}
	
	public static void setLastNotProcessedProtoFile(String protoFileName) {
		lastNotProcessedProtoFile = protoFileName;
	}
	
	public static String getLastNotProcessedProtoFile() {
		return lastNotProcessedProtoFile;
	}

	public static String getLastDirectory() {
		return lastDirectory;
	}

	public static void setLastDirectory(String lastDir) {
		lastDirectory = lastDir;
	}
	
	public static ProtoFile getProtoFileByFilename(String filename) {
		return loadedFiles.get(filename);
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static void setVersion(String version) {
		Global.version = version;
	}
	
	public static String getBuildTime() {
		return buildTime;
	}
	
	public static void setBuildTime(String buildTime) {
		Global.buildTime = buildTime;
	}
	
	public static Map<String, List<ProtoNode>> getTypeMap() {
		Map<String, List<ProtoNode>> map = new HashMap<>();
		for (String pcg : packages.keySet()) {
			List<ProtoNode> nodes = map.get(pcg);
			if (nodes == null) {
				nodes = new ArrayList<>();
				map.put(pcg, nodes);
			}
			for (ProtoFile protoFile : packages.get(pcg)) {
				nodes.addAll(protoFile.getAllMessages());
				nodes.addAll(protoFile.getAllEnums());
			}
		}
		return map;
	}

}
