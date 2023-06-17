package sk.riaver.protograph.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Line extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private int width = 25;
	private boolean horizontal;
	private boolean verticalUpper;
	private boolean verticalLower;
	
	public Line(boolean visible, boolean first, boolean last) {
		super();
		this.horizontal = visible;
		if (visible) {
			this.verticalUpper = !first;
			this.verticalLower = !last;
		}
	}
	
	public Line(int width, boolean visible, boolean first, boolean last) {
		super();
		this.width = width;
		this.horizontal = visible;
		if (visible) {
			this.verticalUpper = !first;
			this.verticalLower = !last;
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.width, this.width);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(this.width, 1);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(this.width, Integer.MAX_VALUE);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.horizontal) {
			int height = this.getHeight();
			g.setColor(Color.BLACK);
			g.drawLine(0, height/2, this.getWidth(), height/2);
			if (this.verticalUpper) {
				g.drawLine(0, 0, 0, height/2);
			}
			if (this.verticalLower) {
				g.drawLine(0, height, 0, height/2);
			}
		}
	}
	
	public void setColapsed(boolean colapsed) {
		this.horizontal = !colapsed;
		this.repaint();
	}

}
