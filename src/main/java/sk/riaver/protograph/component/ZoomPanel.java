package sk.riaver.protograph.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

public class ZoomPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private double scaleFactor;
	private AffineTransform at;
	
	public ZoomPanel(LayoutManager layoutManager) {
		super(layoutManager);
		this.scaleFactor = 1.0;
		this.at = AffineTransform.getScaleInstance(this.scaleFactor, this.scaleFactor);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setTransform(this.at);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		this.at.transform(e.getPoint(), e.getPoint());
		super.processMouseEvent(e);
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		this.at.transform(e.getPoint(), e.getPoint());
		super.processMouseMotionEvent(e);
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		this.at.transform(e.getPoint(), e.getPoint());
		super.processMouseWheelEvent(e);
	}
	
	public void changeScaleFactor(int step) {
		int sign = step < 0 ? -1 : 1;
		this.scaleFactor -= 0.05 * sign * this.scaleFactor;
		if (this.scaleFactor < 0.1) {
			this.scaleFactor = 0.1;
		} else if (this.scaleFactor > 2.0) {
			this.scaleFactor = 2.0;
		}
		this.at = AffineTransform.getScaleInstance(this.scaleFactor, this.scaleFactor);
	}
	
	public AffineTransform getZoomAffineTransform() {
		return this.at;
	}

}
