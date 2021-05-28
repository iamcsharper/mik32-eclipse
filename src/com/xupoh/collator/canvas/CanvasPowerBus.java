package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import com.xupoh.collator.models.Side;

public class CanvasPowerBus implements ICanvasComponent {
	public enum Type {
		Power, Ground
	}

	private Rectangle rect;
	private Type type;
	private Side pointsAt;

	private String str;

	private static final int LENGTH = 50;

	public CanvasPowerBus(final Type type, final Side pointsAtSide, final String str) {
		this.rect = new Rectangle(0, 0, 8, 8);
		this.type = type;
		this.pointsAt = pointsAtSide;

		this.str = str;
	}

	public void setPosition(int x, int y) {
		this.rect.x = x;
		this.rect.y = y;
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

		final int thickness = gc.getLineWidth();

		Point wh = gc.stringExtent(str);
		int xOffset = 0, yOffset = 0;

		float[] oldData = new float[6];
		transform.getElements(oldData);

		gc.setLineWidth(2);

		// Drawing base lines

		switch (pointsAt) {
		case Bottom:
			xOffset = -wh.y / 2;
			yOffset = (LENGTH + wh.x + 12);
			gc.drawLine(this.rect.x, this.rect.y, this.rect.x, this.rect.y + LENGTH);
			break;
		case Left:
			xOffset = -(LENGTH + wh.x + 2);
			gc.drawLine(this.rect.x, this.rect.y, this.rect.x - LENGTH, this.rect.y);
			break;
		case Right:
			xOffset = (LENGTH + 2);
			gc.drawLine(this.rect.x, this.rect.y, this.rect.x + LENGTH, this.rect.y);
			break;
		case Top:
			xOffset = -wh.y / 2;
			yOffset = -(LENGTH-2);
			gc.drawLine(this.rect.x, this.rect.y, this.rect.x, this.rect.y - LENGTH);
			break;
		}

		// Drawing arrows OR bars

		if (this.type == Type.Ground) {
			switch (pointsAt) {
			case Bottom:
				gc.drawLine(this.rect.x - LENGTH / 4, this.rect.y + LENGTH,
						this.rect.x + LENGTH / 4, this.rect.y + LENGTH);
				break;
			case Left:
				gc.drawLine(this.rect.x - LENGTH, this.rect.y - LENGTH / 4,
						this.rect.x - LENGTH, this.rect.y + LENGTH / 4);
				break;
			case Right:
				gc.drawLine(this.rect.x + LENGTH, this.rect.y - LENGTH / 4,
						this.rect.x + LENGTH, this.rect.y + LENGTH / 4);
				break;
			case Top:
				gc.drawLine(this.rect.x - LENGTH / 4, this.rect.y - LENGTH,
						this.rect.x + LENGTH / 4, this.rect.y - LENGTH);
				break;
			}
		} else {
			switch (pointsAt) {
			case Bottom:
				gc.drawLine(this.rect.x, this.rect.y + LENGTH, this.rect.x - 8,
						this.rect.y + LENGTH - 12);
				gc.drawLine(this.rect.x, this.rect.y + LENGTH, this.rect.x + 8,
						this.rect.y + LENGTH - 12);
				break;
			case Left:
				gc.drawLine(this.rect.x - LENGTH, this.rect.y, this.rect.x - LENGTH + 12,
						this.rect.y + 8);
				gc.drawLine(this.rect.x - LENGTH, this.rect.y, this.rect.x - LENGTH + 12,
						this.rect.y - 8);
				break;
			case Right:
				gc.drawLine(this.rect.x + LENGTH, this.rect.y, this.rect.x + LENGTH - 12,
						this.rect.y + 8);
				gc.drawLine(this.rect.x + LENGTH, this.rect.y, this.rect.x + LENGTH - 12,
						this.rect.y - 8);
				break;
			case Top:
				gc.drawLine(this.rect.x, this.rect.y - LENGTH, this.rect.x - 8,
						this.rect.y - LENGTH + 12);
				gc.drawLine(this.rect.x, this.rect.y - LENGTH, this.rect.x + 8,
						this.rect.y - LENGTH + 12);
				break;
			}
		}

		gc.setLineWidth(thickness);

		if (!pointsAt.isHorizontal()) {
			transform.translate(this.rect.x + xOffset, this.rect.y - wh.y / 2 + yOffset);
			transform.rotate(-90);	
			transform.translate(-(this.rect.x+xOffset), -(this.rect.y - wh.y / 2 + yOffset));
			

//			wh.x = wh.x ^ wh.y;
//			wh.y = wh.x ^ wh.y;
//			wh.x = wh.x ^ wh.y;
		}

		gc.setTransform(transform);

		gc.drawText(str, this.rect.x + xOffset, this.rect.y - wh.y / 2 + yOffset);

		transform.setElements(oldData[0], oldData[1], oldData[2], oldData[3], oldData[4],
				oldData[5]);

		gc.setTransform(transform);
	}
}
