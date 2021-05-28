package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

public class CanvasBadge implements ICanvasComponent {

	private Rectangle rect;
	private String text;
	private int offset = -1;
	private int width = 0;

	public int getWidth() {
		return width;
	}

	public CanvasBadge(String text) {
		this.rect = new Rectangle(0, 0, 10, 10);
		this.text = text;
	}

	public void setPosition(int x, int y) {
		this.rect.x = x;
		this.rect.y = y;
	}

	public void setOffset(int w) {
		this.offset = w;
	}

	@Override
	public Rectangle getOuterRect() {
		return rect;
	}

	@Override
	public void update() {

	}

	@Override
	public void draw(Display display, AffineTransform affineTransform,
			Transform transform, GC gc) {

		width = gc.stringExtent(text).x;

		if (offset == -1) {
			offset = width;
		}

		gc.drawText(text, this.rect.x - offset, this.rect.y);
	}
}
