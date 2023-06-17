package sk.riaver.protograph.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sk.riaver.protograph.proto.element.ProtoNode;

public class Element extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ProtoNode node;
	private ProtoCard protoCard;
	private ElementContent mainContent;
	private ColapseBox colapseBox;
	private Line postLine;
	private JPanel parent;
	private JPanel children;
	
	public Element(ProtoNode node, boolean root, boolean otherFile, boolean first, boolean last,
			ProtoCard card, String parentObjects) {
		super(new GridBagLayout());
		this.node = node;
		this.protoCard = card;
		this.parent = this;
		GridBagConstraints cons;
		
		if (root || otherFile) {
			Line preLine = new Line(14, !root, first, last);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.VERTICAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 0;
			this.parent.add(preLine, cons);
			
			JPanel inner = new JPanel(new GridBagLayout());
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 0;
			inner.add(new Gap(5), cons);
			JPanel framed = new JPanel(new GridBagLayout());
			framed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.VERTICAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 1;
			inner.add(framed, cons);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 2;
			inner.add(new Gap(5), cons);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 1;
			cons.gridy = 0;
			this.parent.add(inner, cons);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 2;
			cons.gridy = 0;
			this.parent.add(new Gap(5), cons);
			
			if (otherFile) {
				JLabel title = new JLabel(node.getProtoFile().getSourceFile().getName());
				title.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));
				cons = new GridBagConstraints();
				cons.fill = GridBagConstraints.NONE;
				cons.anchor = GridBagConstraints.LINE_START;
				cons.gridx = 0;
				cons.gridy = 0;
				framed.add(title, cons);
				JPanel titled = new JPanel(new GridBagLayout());
				cons = new GridBagConstraints();
				cons.fill = GridBagConstraints.NONE;
				cons.anchor = GridBagConstraints.LINE_START;
				cons.gridx = 0;
				cons.gridy = 1;
				framed.add(titled, cons);
				JLabel foot = new JLabel(node.getProtoFile().getSourceFile().getName());
				foot.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(1, 0, 0, 1, Color.GRAY),
						BorderFactory.createEmptyBorder(1, 1, 1, 1)));
				cons = new GridBagConstraints();
				cons.fill = GridBagConstraints.NONE;
				cons.anchor = GridBagConstraints.LINE_START;
				cons.gridx = 0;
				cons.gridy = 2;
				framed.add(foot, cons);
				
				this.parent = titled;
			} else {
				this.parent = framed;
			}
			
			preLine = new Line(11, !root, true, true);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.VERTICAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 0;
			this.parent.add(preLine, cons);
		} else {
			Line preLine = new Line(!root, first, last);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.VERTICAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 0;
			this.parent.add(preLine, cons);
		}
		
		if (node.getElementType() != ElementType.EXTENSION
				&& (root || (node.getElementType() != ElementType.MESSAGE
							&& node.getElementType() != ElementType.ENUM))) {
			JPanel inner = new JPanel(new GridBagLayout());
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 0;
			inner.add(new Gap(5), cons);
			this.mainContent = new ElementContent(this, node, card, root);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 1;
			inner.add(this.mainContent, cons);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 0;
			cons.gridy = 2;
			inner.add(new Gap(5), cons);
			
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 1;
			cons.gridy = 0;
			this.parent.add(inner, cons);
		}
		
		if (!node.getComplexContent().isEmpty()) {
			boolean colapsed = true;
			if (node.getElementType() == ElementType.EXTENSION
					|| (!root && (node.getElementType() == ElementType.MESSAGE
								|| node.getElementType() == ElementType.ENUM))) {
				colapsed = false;
			}
			String parentNames = parentObjects;
			if (colapsed && parentNames != null) {
				colapsed = false;
				if (node.getRefContent() != null) {
					String self = " " + node.getRefContent().getElementType()
							+ ":" + node.getRefContent().toString() + " ";
					if (parentNames.contains(self)) {
						colapsed = true;
					} else {
						parentNames += self;
					}
				}
			}
			
			if (node.getElementType() != ElementType.EXTENSION
					&& (root || (node.getElementType() != ElementType.MESSAGE
								&& node.getElementType() != ElementType.ENUM))) {
				if (this.node.getElementType() != ElementType.TYPECHOOSER) {
					this.colapseBox = new ColapseBox(this, colapsed);
					cons = new GridBagConstraints();
					cons.fill = GridBagConstraints.NONE;
					cons.anchor = GridBagConstraints.CENTER;
					cons.gridx = 2;
					cons.gridy = 0;
					this.parent.add(this.colapseBox, cons);
				}
				
				this.postLine = new Line(14, !colapsed, true, true);
				cons = new GridBagConstraints();
				cons.fill = GridBagConstraints.VERTICAL;
				cons.anchor = GridBagConstraints.CENTER;
				cons.gridx = 3;
				cons.gridy = 0;
				this.parent.add(this.postLine, cons);
			}
			
			if (!colapsed) {
				this.createChildren(parentNames);
			}
		} else {
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 2;
			cons.gridy = 0;
			this.parent.add(new Gap(10), cons);
		}
	}
	
	public void changeColapse(boolean colapsed) {
		this.changeColapse(colapsed, null);
	}
	
	private void changeColapse(boolean colapsed, String parentObjects) {
		if (this.node.getElementType() == ElementType.TYPECHOOSER) {
			if (parentObjects != null) {
				return;
			}
			if (this.children != null) {
				this.parent.remove(this.children);
				this.children = null;
			}
		} else {
			this.colapseBox.setColapsed(colapsed);
		}
		this.postLine.setColapsed(colapsed);
		if (this.children == null) {
			this.createChildren(parentObjects);
			this.revalidate();
		}
		this.children.setVisible(!colapsed);
		this.repaint();
	}
	
	public void expanseAll() {
		this.changeColapse(false, "");
	}
	
	public ProtoNode getProtoNode() {
		return this.node;
	}
	
	public void setFocusToElementContent() {
		this.mainContent.grabElementFocus();
		if (this.colapseBox != null) {
			changeColapse(false);
		}
	}
	
	private void createChildren(String parentObjects) {
		this.children = new JPanel(new GridBagLayout());
		int idx = 0;
		GridBagConstraints cons = new GridBagConstraints();
		List<ProtoNode> content = this.node.getComplexContent();
		for (ProtoNode child : content) {
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.LINE_START;
			cons.weightx = 1.0;
			cons.gridx = 0;
			cons.gridy = idx++;
			this.children.add(new Element(child, false, !this.node.getProtoFile().getSourceFile().getAbsolutePath().equals(child.getProtoFile().getSourceFile().getAbsolutePath()),
					idx == 1, idx == content.size(), this.protoCard, parentObjects), cons);
		}
		
		cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.NONE;
		cons.anchor = GridBagConstraints.CENTER;
		cons.gridx = 4;
		cons.gridy = 0;
		this.parent.add(this.children, cons);
	}
	
	public Element pushChild(ProtoNode schnode) {
		if (this.children == null) {
			this.children = new JPanel(new GridBagLayout());
		} else {
			this.children.removeAll();
		}
		GridBagConstraints cons = new GridBagConstraints();
		if (this.colapseBox == null) {
			this.parent.remove(this.parent.getComponentCount() - 1);
			this.colapseBox = new ColapseBox(this, false);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.NONE;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 2;
			cons.gridy = 0;
			this.parent.add(this.colapseBox, cons);
			
			this.postLine = new Line(14, true, true, true);
			cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.VERTICAL;
			cons.anchor = GridBagConstraints.CENTER;
			cons.gridx = 3;
			cons.gridy = 0;
			this.parent.add(this.postLine, cons);
		}
		cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.NONE;
		cons.anchor = GridBagConstraints.LINE_START;
		cons.weightx = 1.0;
		cons.gridx = 0;
		cons.gridy = 0;
		Element ret = new Element(schnode, false, !this.node.getProtoFile().getSourceFile().getAbsolutePath().equals(schnode.getProtoFile().getSourceFile().getAbsolutePath()),
				true, true, this.protoCard, null);
		this.children.add(ret, cons);
		cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.NONE;
		cons.anchor = GridBagConstraints.CENTER;
		cons.gridx = 4;
		cons.gridy = 0;
		this.parent.add(this.children, cons);
		
		this.colapseBox.setColapsed(false);
		this.colapseBox.setVisible(false);
		this.postLine.setColapsed(false);
		return ret;
	}
	
	public void hiddenColapseBox() {
		if (this.colapseBox != null) {
			this.colapseBox.setVisible(false);
		}
	}

}
