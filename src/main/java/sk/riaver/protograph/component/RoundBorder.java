package sk.riaver.protograph.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

public class RoundBorder extends AbstractBorder {
	
	private static final long serialVersionUID = 1L;
	
	private Color color;
	private int thickness;
	
	public RoundBorder(Color color) {
		this.color = color;
		this.thickness = 3;
	}
	
	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(this.thickness, this.thickness, this.thickness, this.thickness);
	}
	
	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.bottom = this.thickness;
		insets.top = this.thickness;
		insets.left = this.thickness;
		insets.right = this.thickness;
		return insets;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(this.color);
		g2.drawRoundRect(x, y, width-1, height-1, 18, 18);
	}

}
