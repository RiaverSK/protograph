package sk.riaver.protograph.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import sk.riaver.protograph.Global;
import sk.riaver.protograph.proto.element.MapFieldDeclaration;
import sk.riaver.protograph.proto.element.ProtoNode;
import sk.riaver.protograph.proto.element.TypeChooser;
import sk.riaver.protograph.util.Utils;

public class ElementContent extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ProtoNode protoNode;
	private ProtoCard protoCard;
	private JLabel namelabel;
	
	public ElementContent(Element parent, ProtoNode node, ProtoCard card, boolean root) {
		super(new GridBagLayout());
		this.protoNode = node;
		this.protoCard = card;
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		if (node.getElementType() == ElementType.TYPECHOOSER) {
			Map<String, List<ProtoNode>> typemap = Global.getTypeMap();
			DefaultMutableTreeNode top = new DefaultMutableTreeNode("package");
			JTree tree = new JTree(top);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode tn = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (tn != null) {
						if (tn.isLeaf()) {
							((TypeChooser) node).setAnyType((ProtoNode) tn.getUserObject());
							parent.changeColapse(false);
						}
					}
				}
			});
			for (String pcg : typemap.keySet()) {
				DefaultMutableTreeNode branch = new DefaultMutableTreeNode(pcg);
				top.add(branch);
				for (ProtoNode prnd : typemap.get(pcg)) {
					branch.add(new DefaultMutableTreeNode(prnd));
				}
			}
			((DefaultTreeModel) tree.getModel()).reload();
			GridBagConstraints cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 0;
			this.add(new JScrollPane(tree), cons);
		} else {
			int rowidx = 0;
			String nodename = node.getName().toDisplay();
			if (root) {
				nodename = node.getName().getProcessedName();
			} else {
				nodename += " " + Utils.getCardinality(node);
			}
			this.namelabel = new JLabel(nodename);
			this.namelabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			this.namelabel.setFocusable(true);
			this.namelabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (e.isControlDown() && root)  {
							protoCard.findAllOccurences(protoNode);
						} else {
							protoCard.viewProtoNode(protoNode);
							namelabel.requestFocusInWindow();
						}
					}
				}
			});
			this.namelabel.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					namelabel.setOpaque(false);
					namelabel.repaint();
				}
				@Override
				public void focusGained(FocusEvent e) {
					namelabel.setOpaque(true);
					namelabel.setBackground(Color.CYAN);
					namelabel.repaint();
				}
			});
			GridBagConstraints cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = rowidx++;
			this.add(this.namelabel, cons);
			Border border = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
					BorderFactory.createEmptyBorder(1, 1, 1, 1));
			String type = "type = ";
			if (node.getScalarType() != null) {
				type += node.getScalarType().toString();
			} else if (node.getRefContent() != null) {
				type += node.getRefContent().getElementType().name().toLowerCase() + ": " + node.getRefContent().toString();
			} else {
				type = node.getElementType().name().toLowerCase();
			}
			JLabel typelabel = new JLabel(type);
			typelabel.setBorder(border);
			typelabel.setFocusable(true);
			typelabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (e.isControlDown() && protoNode.getRefContent() != null) {
							protoCard.switchAndViewProtoNode(protoNode.getRefContent());
						} else {
							protoCard.viewProtoNode(protoNode.getRefContent() == null ? protoNode : protoNode.getRefContent());
							typelabel.requestFocusInWindow();
						}
					}
				}
			});
			typelabel.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					typelabel.setOpaque(false);
					typelabel.repaint();
				}
				@Override
				public void focusGained(FocusEvent e) {
					typelabel.setOpaque(true);
					typelabel.setBackground(Color.CYAN);
					typelabel.repaint();
				}
			});
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = rowidx++;
			this.add(typelabel, cons);
			
			if (node.getElementType() == ElementType.MAPFIELD) {
				MapFieldDeclaration mfdc = (MapFieldDeclaration) node;
				String key = "key = " + mfdc.getMapKeyType().toString();
				JLabel keylabel = new JLabel(key);
				keylabel.setBorder(border);
				keylabel.setFocusable(true);
				keylabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							protoCard.viewProtoNode(protoNode);
							keylabel.requestFocusInWindow();
						}
					}
				});
				keylabel.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {
						keylabel.setOpaque(false);
						keylabel.repaint();
					}
					@Override
					public void focusGained(FocusEvent e) {
						keylabel.setOpaque(true);
						keylabel.setBackground(Color.CYAN);
						keylabel.repaint();
					}
				});
				cons = new GridBagConstraints();
				cons.fill = GridBagConstraints.HORIZONTAL;
				cons.anchor = GridBagConstraints.CENTER;
				cons.gridx = 0;
				cons.gridy = rowidx++;
				this.add(keylabel, cons);
			}
		}
	}
	
	public void grabElementFocus() {
		if (this.namelabel != null) {
			this.namelabel.requestFocusInWindow();
		}
	}

}
