package sk.riaver.protograph.component;

import java.awt.Dimension;

import javax.swing.JPanel;

public class Gap extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private int gapSize;
	
	public Gap(int gapSize) {
		super();
		this.gapSize = gapSize;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.gapSize, this.gapSize);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(this.gapSize, this.gapSize);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(this.gapSize, this.gapSize);
	}

}
