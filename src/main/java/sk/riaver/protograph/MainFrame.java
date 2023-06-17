package sk.riaver.protograph;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sk.riaver.protograph.component.ProtoCard;
import sk.riaver.protograph.proto.element.ExtensionDeclaration;
import sk.riaver.protograph.proto.element.ProtoFile;
import sk.riaver.protograph.proto.element.ProtoNode;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "ProtoGraph";
	
	private static Log log = LogFactory.getLog(MainFrame.class);
	
	private JMenu window;
	private JPanel cardPane;
	
	public MainFrame() {
		super(TITLE);
		log.debug("Starting " + TITLE + "...");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.cardPane = new JPanel(new CardLayout());
		this.cardPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				if (cardPane.getComponentCount() > 0) {
					for (Component cmp : cardPane.getComponents()) {
						cmp.setSize(cardPane.getSize());
					}
				}
			}
		});
		this.getContentPane().add(this.cardPane, BorderLayout.CENTER);
		
		JMenu filemenu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open ...");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(true);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (Global.getLastDirectory() != null) {
					File lastdir = new File(Global.getLastDirectory());
					if (lastdir.exists() && lastdir.isDirectory()) {
						fc.setCurrentDirectory(lastdir);
					}
				}
				int retval = fc.showOpenDialog(MainFrame.this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					processFiles(fc.getSelectedFiles());
				}
				Global.setLastDirectory(fc.getCurrentDirectory().getAbsolutePath());
			}
		});
		filemenu.add(open);
		JMenuItem close = new JMenuItem("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component comp = null;
				if (cardPane.getComponentCount() > 0) {
					for (int i = 0; i < cardPane.getComponentCount(); i++) {
						Component cmp = cardPane.getComponent(i);
						if (cmp.isShowing()) {
							comp = cmp;
							break;
						}
					}
				}
				if (comp != null) {
					cardPane.remove(comp);
					removeMenuItem(((ProtoCard) comp).getProtoFile());
					if (cardPane.getComponentCount() == 0) {
						Global.getLoadedFiles().clear();
						MainFrame.this.setTitle(TITLE);
						MainFrame.this.revalidate();
						MainFrame.this.repaint();
					} else {
						for (int i = 0; i < cardPane.getComponentCount(); i++) {
							Component cmp = cardPane.getComponent(i);
							if (cmp.isShowing()) {
								comp = cmp;
								break;
							}
						}
						MainFrame.this.setTitle(TITLE + " - " + ((ProtoCard) comp).getProtoFile().getSourceFile().getName());
						MainFrame.this.revalidate();
						MainFrame.this.repaint();
					}
				}
			}
		});
		filemenu.add(close);
		JMenuItem closeall = new JMenuItem("Close all");
		closeall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardPane.removeAll();
				window.removeAll();
				Global.getLoadedFiles().clear();
				MainFrame.this.setTitle(TITLE);
				MainFrame.this.revalidate();
				MainFrame.this.repaint();
			}
		});
		filemenu.add(closeall);
		filemenu.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		filemenu.add(exit);
		
		this.window = new JMenu("Window");
		
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainFrame.this, TITLE + " version " + Global.getVersion()
						+ "\nrelease date " + Global.getBuildTime() + "\nauthor Riaver\nlicense GPL3",
						"About " + TITLE, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		help.add(about);
		
		JMenuBar mainBar = new JMenuBar();
		mainBar.add(filemenu);
		mainBar.add(this.window);
		mainBar.add(help);
		this.setJMenuBar(mainBar);
		
		this.pack();
		this.setSize(960, 600);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		log.debug("Running");
	}
	
	public void processCommandLine() {
		if (Global.getCommandLineArgs() != null && Global.getCommandLineArgs().length > 0) {
			List<File> files = new ArrayList<>();
			for (String fileName : Global.getCommandLineArgs()) {
				File file = new File(fileName);
				if (file.exists() && file.isFile()) {
					files.add(file);
				}
			}
			if (files.size() > 0) {
				this.processFiles(files.toArray(new File[1]));
			}
		}
	}
	
	private void processFiles(File[] files) {
		if (files != null && files.length > 0) {
			List<String> names = new ArrayList<>();
			boolean added = false;
			for (File file : files) {
				if (Global.getProtoFileByFilename(file.getAbsolutePath()) == null) {
					if (Global.loadProtoFile(file) == null) {
						JOptionPane.showMessageDialog(this, "Error occurs while processing file:\n"
								+ Global.getLastNotProcessedProtoFile() + "\nInspect /tmp/protograph.log for details.",
							"Processing failed", JOptionPane.ERROR_MESSAGE);
					} else {
						added = true;
						names.add(file.getAbsolutePath());
					}
				}
			}
			if (added) {
				Global.setCommandLineArgs(names.toArray(new String[1]));
				this.window.removeAll();
				this.process();
			}
		}
	}
	
	private void process() {
		String showCard = null;
		String showfile = "";
		File firstfile = new File(Global.getCommandLineArgs()[0]);
		for (String pcg : Global.getPackages().keySet()) {
			JMenu pcgmenu = new JMenu(pcg);
			this.window.add(pcgmenu);
			for (ProtoFile proto : Global.getPackages().get(pcg)) {
				ProtoCard card = new ProtoCard(proto, this);
				String filename = proto.getSourceFile().getName();
				JMenuItem buton = new JMenuItem(filename);
				buton.setToolTipText(proto.getSourceFile().getAbsolutePath());
				buton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						CardLayout cl = (CardLayout) cardPane.getLayout();
						cl.show(cardPane, card.getCardName());
						setTitle(TITLE + " - " + filename);
					}
				});
				pcgmenu.add(buton);
				this.cardPane.add(card, card.getCardName());
				if (proto.getSourceFile().getAbsolutePath().equals(firstfile.getAbsolutePath())) {
					showCard = card.getCardName();
					showfile = filename;
					Global.setLastDirectory(firstfile.getParentFile().getAbsolutePath());
				}
			}
		}
		CardLayout cl = (CardLayout) this.cardPane.getLayout();
		cl.show(this.cardPane, showCard);
		this.setTitle(TITLE + " - " + showfile);
		this.cardPane.revalidate();
		this.cardPane.repaint();
	}
	
	private void removeMenuItem(ProtoFile protoFile) {
		log.debug("removing " + protoFile.getProtopackage().toDisplay() + ": " + protoFile.getSourceFile().getName());
		JMenu packageMenu = null;
		JMenuItem buton = null;
		outer:
		for (Component comp : this.window.getMenuComponents()) {
			JMenu menu = (JMenu) comp;
			log.debug(menu.getText());
			if (menu.getText().equals(protoFile.getProtopackage().toDisplay())) {
				log.debug("found package menu");
				packageMenu = menu;
				for (Component itm : menu.getMenuComponents()) {
					JMenuItem item = (JMenuItem) itm;
					if (item.getToolTipText().equals(protoFile.getSourceFile().getAbsolutePath())) {
						buton = item;
						log.debug("remove buton");
						break outer;
					}
				}
			}
		}
		if (packageMenu != null) {
			if (buton != null) {
				packageMenu.remove(buton);
			}
			if (packageMenu.getComponentCount() == 0) {
				this.window.remove(packageMenu);
			}
		}
	}
	
	public void switchAndViewProtoNode(ProtoNode node) {
		for (Component comp : this.cardPane.getComponents()) {
			ProtoCard card = (ProtoCard) comp;
			if (card.getProtoFile().equals(node.getProtoFile())) {
				CardLayout cl = (CardLayout) this.cardPane.getLayout();
				cl.show(this.cardPane, card.getCardName());
				setTitle(TITLE + " - " + card.getProtoFile().getSourceFile().getName());
				card.scrollAndViewProtoNode(node);
				break;
			}
		}
	}
	
	public List<List<ProtoNode>> findAllOccurences(ProtoNode nodeToFind) {
		log.debug("finding all occurences for " + nodeToFind.getElementType() + ":" + nodeToFind.toString());
		List<List<ProtoNode>> result = new ArrayList<List<ProtoNode>>();
		for (List<ProtoFile> pcg : Global.getPackages().values()) {
			for (ProtoFile protoFile : pcg) {
				for (ProtoNode protonode : protoFile.getServices()) {
					this.findAllOccurences(protonode, nodeToFind, new ArrayList<ProtoNode>(), "", result, true);
				}
				for (ProtoNode protonode : protoFile.getEnums()) {
					this.findAllOccurences(protonode, nodeToFind, new ArrayList<ProtoNode>(), "", result, true);
				}
				for (ProtoNode protonode : protoFile.getMessages()) {
					this.findAllOccurences(protonode, nodeToFind, new ArrayList<ProtoNode>(), "", result, true);
				}
				for (ExtensionDeclaration exdc : protoFile.getExtensions()) {
					for (ProtoNode protonode : exdc.getFields()) {
						this.findAllOccurences(protonode, nodeToFind, new ArrayList<ProtoNode>(), "", result, true);
					}
					for (ProtoNode protonode : exdc.getGroups()) {
						this.findAllOccurences(protonode, nodeToFind, new ArrayList<ProtoNode>(), "", result, true);
					}
				}
			}
		}
		if (!result.isEmpty()) {
			//TODO
			for(List<ProtoNode> reslist : result) {
				String res = "";
				for (ProtoNode node : reslist) {
					res += " > " + node.getElementType() + ":" + node.toString();
				}
				log.debug(res);
			}
		}
		return result;
	}
	
	private void findAllOccurences(ProtoNode node, ProtoNode nodeToFind, List<ProtoNode> line, String parentObjects,
			List<List<ProtoNode>> result, boolean root) {
		List<ProtoNode> nline = new ArrayList<ProtoNode>(line);
		nline.add(node);
		if (node.equals(nodeToFind)) {
			if (nline.size() > 1) {
				result.add(nline);
			}
			return;
		}
		String parentNames = parentObjects;
		if (node.getRefContent() != null) {
			String self = " " + node.getRefContent().getElementType()
					+ ":" + node.getRefContent().toString() + " ";
			if (parentNames.contains(self)) {
				return;
			} else {
				parentNames += self;
			}
		} else if (node.getName() != null && root) {
			parentNames += " " + node.getElementType() + ":" + node.toString() + " ";
		}
		if (node.getRefContent() != null) {
			this.findAllOccurences(node.getRefContent(), nodeToFind, nline, parentNames, result, false);
		} else
		if (!node.getComplexContent().isEmpty()) {
			for (ProtoNode protonode : node.getComplexContent()) {
				this.findAllOccurences(protonode, nodeToFind, nline, parentNames, result, false);
			}
		}
//		if (!node.getOptions().isEmpty()) {
//			for (ProtoNode protonode : node.getOptions().values()) {
//				this.findAllOccurences(protonode, nodeToFind, nline, parentNames, result, false);
//			}
//		}
	}

}
