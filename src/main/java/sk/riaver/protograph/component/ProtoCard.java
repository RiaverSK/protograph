package sk.riaver.protograph.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sk.riaver.protograph.MainFrame;
import sk.riaver.protograph.proto.element.EnumDeclaration;
import sk.riaver.protograph.proto.element.ExtensionDeclaration;
import sk.riaver.protograph.proto.element.MessageDeclaration;
import sk.riaver.protograph.proto.element.ProtoFile;
import sk.riaver.protograph.proto.element.ProtoNode;
import sk.riaver.protograph.proto.element.ServiceDeclaration;
import sk.riaver.protograph.proto.token.Comment;
import sk.riaver.protograph.util.Property;
import sk.riaver.protograph.util.Utils;

public class ProtoCard extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final String TREE_PROTO_FILE = "proto file";
	private static final String TREE_SERVICES = "services";
	private static final String TREE_MESSAGES = "messages";
	private static final String TREE_ENUMS = "enums";
	private static final String TREE_EXTENSIONS = "extensions";
	
	private static Log log = LogFactory.getLog(ProtoCard.class);
	private static int cardCount = 0;
	
	private int cardIdx = cardCount++;
	private int startX;
	private int startY;
	private Rectangle startRec;
	private ProtoFile protoFile;
	private MainFrame mainFrame;
	private JTextArea commentArea;
	private JTable propTable;
//	private JPanel mainview;
	private JTabbedPane tabs;
	private JScrollPane servicesTab;
	private JScrollPane messagesTab;
	private JScrollPane enumsTab;
	private JScrollPane extensionsTab;
	
	public ProtoCard(ProtoFile protoFile, MainFrame mainFrame) {
		super(new BorderLayout());
		this.protoFile = protoFile;
		this.mainFrame = mainFrame;
		log.debug(getCardName() + " for " + protoFile.getSourceFile().getAbsolutePath());
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(TREE_PROTO_FILE);
		JTree tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				tree.setSelectionInterval(-1, -1);
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (tn != null) {
					if (tn.isLeaf()) {
						scrollAndViewProtoNode((ProtoNode) tn.getUserObject());
					} else if (TREE_PROTO_FILE.equals(tn.getUserObject())) {
						setProperties(protoFile.getAllProperties());
						setComments(protoFile.getComments());
						requestFocusInWindow(false);
					} else if (TREE_SERVICES.equals(tn.getUserObject())) {
						tabs.setSelectedComponent(servicesTab);
					} else if (TREE_MESSAGES.equals(tn.getUserObject())) {
						tabs.setSelectedComponent(messagesTab);
					} else if (TREE_ENUMS.equals(tn.getUserObject())) {
						tabs.setSelectedComponent(enumsTab);
					} else if (TREE_EXTENSIONS.equals(tn.getUserObject())) {
						tabs.setSelectedComponent(extensionsTab);
					}
				}
			}
		});
		String[] columns = {"name", "value"};
		String[][] values = {{"default", ""}, {"fixed", ""}};
		this.propTable = new JTable(values, columns);
//		this.propTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JSplitPane sidesplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tree), new JScrollPane(this.propTable));
		sidesplit.setResizeWeight(0.5);
		sidesplit.setContinuousLayout(true);
		
//		this.mainview = new JPanel(new GridBagLayout());
//		this.mainview.setAutoscrolls(true);
//		JScrollPane mainscroll = new JScrollPane(this.mainview);
//		mainscroll.getHorizontalScrollBar().setUnitIncrement(10);
//		mainscroll.getVerticalScrollBar().setUnitIncrement(10);
//		this.mainview.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent e) {
//				if (e.getButton() == MouseEvent.BUTTON1) {
//					startX = e.getXOnScreen();
//					startY = e.getYOnScreen();
//					startRec = mainview.getVisibleRect();
//					mainview.setCursor(new Cursor(Cursor.MOVE_CURSOR));
//				}
//			}
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				mainview.setCursor(null);
//			}
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if (e.getButton() == MouseEvent.BUTTON1) {
//					setProperties(schema.getSchemaAttrs());
//					setAnnotations(schema.getAnnotations());
//					requestFocusInWindow(false);
//				}
//			}
//		});
//		this.mainview.addMouseMotionListener(new MouseAdapter() {
//			@Override
//			public void mouseDragged(MouseEvent e) {
//		        int deltaX = startX - e.getXOnScreen();
//		        int deltaY = startY - e.getYOnScreen();
//		        Rectangle r = new Rectangle(startRec.x + deltaX, startRec.y + deltaY, startRec.width, startRec.height);
//		        mainview.scrollRectToVisible(r);
//			}
//		});
		this.tabs = new JTabbedPane();
		
		this.commentArea = new JTextArea();
		this.commentArea.append("Comments:");
		this.commentArea.setEditable(false);
		JSplitPane mainsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.tabs, new JScrollPane(this.commentArea));
		mainsplit.setResizeWeight(1.0);
		mainsplit.setContinuousLayout(true);
		if (mainFrame.getHeight() > 399) {
			mainsplit.setDividerLocation(mainFrame.getHeight() - 200);
		} else {
			mainsplit.setDividerLocation(mainFrame.getHeight() / 2);
		}
		
		JSplitPane cardsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidesplit, mainsplit);
		cardsplit.setContinuousLayout(true);
		cardsplit.setDividerLocation(200);
		this.add(cardsplit, BorderLayout.CENTER);
		
		top.removeAllChildren();
		if (!this.protoFile.getServices().isEmpty()) {
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(TREE_SERVICES);
			for (ProtoNode protoNode : this.protoFile.getServices()) {
				branch.add(new DefaultMutableTreeNode(protoNode));
			}
			top.add(branch);
		}
		if (!this.protoFile.getAllMessages().isEmpty()) {
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(TREE_MESSAGES);
			for (ProtoNode protoNode : this.protoFile.getAllMessages()) {
				branch.add(new DefaultMutableTreeNode(protoNode));
			}
			top.add(branch);
		}
		if (!this.protoFile.getAllEnums().isEmpty()) {
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(TREE_ENUMS);
			for (ProtoNode protoNode : this.protoFile.getAllEnums()) {
				branch.add(new DefaultMutableTreeNode(protoNode));
			}
			top.add(branch);
		}
		if (!this.protoFile.getAllExtensions().isEmpty()) {
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(TREE_EXTENSIONS);
			for (ExtensionDeclaration exdc : this.protoFile.getAllExtensions()) {
				branch.add(new DefaultMutableTreeNode(exdc.getMessageType()));
			}
			top.add(branch);
		}
		((DefaultTreeModel) tree.getModel()).reload();
		
		this.setProperties(this.protoFile.getAllProperties());
		this.setComments(this.protoFile.getComments());
		
		Component show = null;
		if (!this.protoFile.getServices().isEmpty()) {
			this.servicesTab = this.createCardView(this.protoFile.getServices());
			this.tabs.addTab(TREE_SERVICES, this.servicesTab);
			show = this.servicesTab;
		}
		if (!this.protoFile.getAllMessages().isEmpty()) {
			this.messagesTab = this.createCardView(this.protoFile.getAllMessages());
			this.tabs.addTab(TREE_MESSAGES, this.messagesTab);
			if (show == null) {
				show = this.messagesTab;
			}
		}
		if (!this.protoFile.getAllEnums().isEmpty()) {
			this.enumsTab = this.createCardView(this.protoFile.getAllEnums());
			this.tabs.addTab(TREE_ENUMS, this.enumsTab);
			if (show == null) {
				show = this.enumsTab;
			}
		}
		if (!this.protoFile.getAllExtensions().isEmpty()) {
			List<MessageDeclaration> extended = new ArrayList<>();
			for (ExtensionDeclaration exdc : this.protoFile.getAllExtensions()) {
				extended.add((MessageDeclaration) exdc.getMessageType());
			}
			this.extensionsTab = this.createCardView(extended);
			this.tabs.addTab(TREE_EXTENSIONS, this.extensionsTab);
			if (show == null) {
				show = this.extensionsTab;
			}
		}
		if (show != null) {
			this.tabs.setSelectedComponent(show);
		}
//		this.createMainView(nodes);
	}
	
//	private void createMainView(Collection<ProtoNode> nodes) {
//		this.mainview.removeAll();
//		int idx = 0;
//		for (SchemaNode node : nodes) {
//			Element elem = new Element(node, true, false, true, true, this);
//			GridBagConstraints cons = new GridBagConstraints();
//			cons.fill = GridBagConstraints.NONE;
//			cons.anchor = GridBagConstraints.NORTHWEST;
//			cons.gridx = 0;
//			cons.gridy = idx++;
//			cons.weightx = 1.0;
//			if (idx == nodes.size()) {
//				cons.weighty = 1.0;
//			} else {
//				cons.weighty = 0.0;
//			}
//			this.mainview.add(elem, cons);
//		}
//		this.mainview.revalidate();
//	}
	
	private JScrollPane createCardView(List<? extends ProtoNode> nodes) {
		JPanel cardview = new JPanel(new GridBagLayout());
		cardview.setAutoscrolls(true);
		JScrollPane cardscroll = new JScrollPane(cardview);
		cardscroll.getHorizontalScrollBar().setUnitIncrement(10);
		cardscroll.getVerticalScrollBar().setUnitIncrement(10);
		cardview.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					startX = e.getXOnScreen();
					startY = e.getYOnScreen();
					startRec = cardview.getVisibleRect();
					cardview.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				cardview.setCursor(null);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					setProperties(protoFile.getAllProperties());
					setComments(protoFile.getComments());
					requestFocusInWindow(false);
				}
			}
		});
//		cardview.addMouseWheelListener(new MouseWheelListener() {
//			@Override
//			public void mouseWheelMoved(MouseWheelEvent e) {
//				if (e.isControlDown()) {
////					cardview.changeScaleFactor(e.getWheelRotation());
////					cardview.revalidate();
////					cardview.repaint();
//				} else {
//					cardscroll.dispatchEvent(e);
//				}
//			}
//		});
		cardview.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
		        int deltaX = startX - e.getXOnScreen();
		        int deltaY = startY - e.getYOnScreen();
		        Rectangle r = new Rectangle(startRec.x + deltaX, startRec.y + deltaY, startRec.width, startRec.height);
		        cardview.scrollRectToVisible(r);
			}
		});
		int idx = 0;
		for (ProtoNode node : nodes) {
			Element elem = new Element(node, true, false, true, true, this, null);
			GridBagConstraints cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.NORTHWEST;
			cons.gridx = 0;
			cons.gridy = idx++;
			cons.weightx = 1.0;
			if (idx == nodes.size()) {
				cons.weighty = 1.0;
			} else {
				cons.weighty = 0.0;
			}
			cardview.add(elem, cons);
		}
		cardview.revalidate();
		return cardscroll;
	}
	
	public String getCardName() {
		return "protoCard" + cardIdx;
	}
	
	public ProtoFile getProtoFile() {
		return this.protoFile;
	}
	
	private void setComments(List<Comment> comments) {
		String comms = "";
		if (!comments.isEmpty()) {
			for (Comment comm : comments) {
				comms += Utils.prepareComment(comm.getText()) + "\n";
			}
		}
		this.commentArea.setText(comms);
		this.commentArea.revalidate();
	}
	
	private void setProperties(List<Property> props) {
		DefaultTableModel dataModel = new TableModel();
		dataModel.addColumn("name");
		dataModel.addColumn("value");
		for (Property prop : props) {
			String[] vals = new String[2];
			vals[0] = prop.getName();
			vals[1] = prop.getValue();
			dataModel.addRow(vals);
		}
		this.propTable.setModel(dataModel);
		this.propTable.doLayout();
	}
	
	public void switchAndViewProtoNode(ProtoNode node) {
		if (this.protoFile.equals(node.getProtoFile())) {
			this.scrollAndViewProtoNode(node);
		} else {
			this.mainFrame.switchAndViewProtoNode(node);
		}
	}
	
	public void findAllOccurences(ProtoNode node) {
		List<List<ProtoNode>> result = this.mainFrame.findAllOccurences(node);
		if (!result.isEmpty()) {
			JPanel cardview = new JPanel(new GridBagLayout());
			cardview.setAutoscrolls(true);
			JScrollPane cardscroll = new JScrollPane(cardview);
			cardscroll.getHorizontalScrollBar().setUnitIncrement(10);
			cardscroll.getVerticalScrollBar().setUnitIncrement(10);
			cardview.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						startX = e.getXOnScreen();
						startY = e.getYOnScreen();
						startRec = cardview.getVisibleRect();
						cardview.setCursor(new Cursor(Cursor.MOVE_CURSOR));
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					cardview.setCursor(null);
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						setProperties(new ArrayList<>());
						setComments(new ArrayList<>());
						requestFocusInWindow(false);
						if (e.isControlDown()) {
							tabs.remove(cardscroll);
						}
					}
				}
			});
			cardview.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
			        int deltaX = startX - e.getXOnScreen();
			        int deltaY = startY - e.getYOnScreen();
			        Rectangle r = new Rectangle(startRec.x + deltaX, startRec.y + deltaY, startRec.width, startRec.height);
			        cardview.scrollRectToVisible(r);
				}
			});
			int idx = 0;
			for (List<ProtoNode> res : result) {
				GridBagConstraints cons = new GridBagConstraints();
				cons.fill = GridBagConstraints.NONE;
				cons.anchor = GridBagConstraints.NORTHWEST;
				cons.gridx = 0;
				cons.gridy = idx++;
				cons.weightx = 1.0;
				if (idx == result.size()) {
					cons.weighty = 1.0;
				} else {
					cons.weighty = 0.0;
				}
				cardview.add(this.createPushElement(res), cons);
			}
			cardview.revalidate();
			this.tabs.addTab(node.getClass().getName() + ": " + node.getName().toDisplay(), cardscroll);
			this.tabs.setSelectedComponent(cardscroll);
			setProperties(new ArrayList<>());
			setComments(new ArrayList<>());
		}
	}
	
	private Element createPushElement(List<ProtoNode> nodes) {
		Element parent = null;
		Element ret = null;
		for (ProtoNode schnode : nodes) {
			if (parent == null) {
				parent = new Element(schnode, true, true, true, true, this, null);
				ret = parent;
			} else {
				parent = parent.pushChild(schnode);
			}
		}
		parent.hiddenColapseBox();
		return ret;
	}
	
	public void scrollAndViewProtoNode(ProtoNode node) {
		JPanel panel = null;
		if (node instanceof ServiceDeclaration) {
			panel = (JPanel) this.servicesTab.getViewport().getComponent(0);
			this.tabs.setSelectedComponent(this.servicesTab);
		} else
		if (node instanceof MessageDeclaration) {
			panel = (JPanel) this.messagesTab.getViewport().getComponent(0);
			this.tabs.setSelectedComponent(this.messagesTab);
		} else
		if (node instanceof EnumDeclaration) {
			panel = (JPanel) this.enumsTab.getViewport().getComponent(0);
			this.tabs.setSelectedComponent(this.enumsTab);
		} else
		if (node instanceof ExtensionDeclaration) {
			panel = (JPanel) this.extensionsTab.getViewport().getComponent(0);
			this.tabs.setSelectedComponent(this.extensionsTab);
		}
		for (Component comp : panel.getComponents()) {
			Element elm = (Element) comp;
			if (elm.getProtoNode().equals(node)) {
				elm.setFocusToElementContent();
				panel.scrollRectToVisible(elm.getBounds());
				this.viewProtoNode(node);
				break;
			}
		}
	}
	
	public void viewProtoNode(ProtoNode node) {
		DefaultTableModel dataModel = new TableModel();
		dataModel.addColumn("name");
		dataModel.addColumn("value");
		String longname = "";
		String longvalue = "";
		for (Property prop : node.getAllProperties()) {
			String[] vals = new String[2];
			vals[0] = prop.getName();
			vals[1] = prop.getValue();
			dataModel.addRow(vals);
			if (prop.getName() != null && prop.getName().length() > longname.length()) {
				longname = prop.getName();
			}
			if (prop.getValue() != null && prop.getValue().length() > longvalue.length()) {
				longvalue = prop.getValue();
			}
		}
		this.propTable.setModel(dataModel);
//		int namewidth = this.propTable.getDefaultRenderer(
//				dataModel.getColumnClass(0)).getTableCellRendererComponent(this.propTable, longname, false, false, 0, 0).getPreferredSize().width;
//		int valuewidth = this.propTable.getDefaultRenderer(
//				dataModel.getColumnClass(1)).getTableCellRendererComponent(this.propTable, longvalue, false, false, 0, 1).getPreferredSize().width;
//		this.propTable.setPreferredSize(new Dimension(namewidth + valuewidth, this.propTable.getPreferredSize().height));
//		this.propTable.getColumnModel().getColumn(0).setPreferredWidth(namewidth);
//		this.propTable.getColumnModel().getColumn(1).setPreferredWidth(valuewidth);
		this.setComments(node.getComments());
	}

}
