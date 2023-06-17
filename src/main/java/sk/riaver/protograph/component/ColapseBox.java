package sk.riaver.protograph.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class ColapseBox extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private boolean colapsed;
	private Element parent;
	
	public ColapseBox(Element parentEl, boolean colap) {
		super();
		this.parent = parentEl;
		this.colapsed = colap;
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.isControlDown() && colapsed) {
						parent.expanseAll();
					} else {
						parent.changeColapse(!colapsed);
					}
				}
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(11, 11);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(11, 11);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(11, 11);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 10, 10);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 10, 10);
		g.drawLine(3, 5, 7, 5);
		if (this.colapsed) {
			g.drawLine(5, 3, 5, 7);
		}
	}
	
	public void setColapsed(boolean colapsed) {
		this.colapsed = colapsed;
		this.repaint();
	}

}
