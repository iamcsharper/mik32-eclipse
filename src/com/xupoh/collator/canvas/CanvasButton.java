package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class CanvasButton implements ICanvasComponent {
	private String str;
	private int x;
	private int y;

	private Rectangle rect;
	private Point stringDimensions;

	private int forcedWidth = 0;
	private int forcedHeight = 0;

	private boolean isHorizontal;

	private boolean isActive = false;

	private MouseManager mouseManager;
	private CanvasSpace space;

	public CanvasButton(String str, int x, int y, boolean isHorizontal,
			MouseManager mouseManager, CanvasSpace space) {
		this.str = str;
		this.x = x;
		this.y = y;
		this.isHorizontal = isHorizontal;

		this.mouseManager = mouseManager;
		this.space = space;

		this.rect = new Rectangle(x, y, 10, 10);
	}

	public void setText(String text) {
		this.str = text;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean getIsHorizontal() {
		return isHorizontal;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void updateStringDimensions(GC gc) {
		stringDimensions = gc.stringExtent(str);

		if (isHorizontal) {
			rect = new Rectangle(x, y, stringDimensions.x + 10, stringDimensions.y + 10);
		} else {
			rect = new Rectangle(x, y, stringDimensions.y + 10, stringDimensions.x + 10);
		}
	}

	private Listener onClickListener;

	public void setOnClickListener(Listener listener) {
		onClickListener = listener;
	}

	private boolean prevWasClicked = false;

	@Override
	public void update() {

	}

	@Override
	public void draw(Display display, AffineTransform affineTransform,
			Transform transform, GC gc) {
		if (isHorizontal) {
			if (forcedWidth > 0) {
				rect.width = forcedWidth;
			}
			if (forcedHeight > 0) {
				rect.height = forcedHeight;
			}
		} else {
			if (forcedWidth > 0) {
				rect.height = forcedWidth;
			}
			if (forcedHeight > 0) {
				rect.width = forcedHeight;
			}
		}

		boolean isMouseDown = mouseManager.isLMBDown();

		final Color preservedColor = gc.getBackground();

		Point mousePos = (space == CanvasSpace.Screen)
				? mouseManager.getMousePointInScreenSpace()
				: mouseManager.getMousePointInWorldSpace(affineTransform);

		boolean isHover = rect.contains(mousePos);

		if (isHover || isActive) {
			if (isMouseDown) {
				if (onClickListener != null && !prevWasClicked) {
					onClickListener.handleEvent(new Event() {
					});
				}

				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			} else {
				gc.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
			}

			gc.fillRectangle(rect);
		} else {

			gc.drawRectangle(rect);
		}

		prevWasClicked = isMouseDown;

		int tx = x + (rect.width - stringDimensions.x) / 2;
		int ty = y + (rect.height - stringDimensions.y) / 2;

		float[] oldData = new float[6];

		if (!isHorizontal) {
			transform.getElements(oldData);

			float offsetX = 0;
			float offsetY = 0;

			transform.translate((tx + offsetX), (ty + offsetY));
			transform.rotate(-90);
			transform.translate(-(tx + offsetX), -(ty + offsetY));

			gc.setTransform(transform);

			tx -= 24;
			ty += 10;
		}

		gc.drawText(str, tx, ty, SWT.NO_BACKGROUND);

		gc.setBackground(preservedColor);

		if (!isHorizontal) {
			transform.setElements(oldData[0], oldData[1], oldData[2], oldData[3],
					oldData[4], oldData[5]);

			gc.setTransform(transform);
		}
	}

	@Override
	public Rectangle getOuterRect() {
		return rect;
	}

	public void setForcedLocalWidth(int width) {
		forcedWidth = width;
	}

	public void setForcedLocalHeight(int height) {
		forcedHeight = height;
	}
}
