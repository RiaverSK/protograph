package sk.riaver.protograph;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.SwingUtilities;

public class ProtoGraphApp {
	
	public static void main(String[] args) throws Exception {
		Global.setCommandLineArgs(args);
		String version;
		String buildTime;
		InputStream manifestStream = ProtoGraphApp.class.getResourceAsStream("/META-INF/MANIFEST.MF");
		try {
			Manifest manifest = new Manifest(manifestStream);
			Attributes attributes = manifest.getMainAttributes();
			version = attributes.getValue("Implementation-Version");
			buildTime = attributes.getValue("Build-Time");
		} finally {
			if (manifestStream != null) {
				manifestStream.close();
			}
		}
		Global.setVersion(version);
		Global.setBuildTime(buildTime);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createMainFrame();
			}
		});
	}
	
	public static void createMainFrame() {
		MainFrame mf = new MainFrame();
		mf.setVisible(true);
		mf.processCommandLine();
	}

}
