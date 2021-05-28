package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.xupoh.collator.models.Side;

public class CanvasButton implements ICanvasComponent {
	private String str;
	private int x;
	private int y;

	private static final Event emptyEvent = new Event();

	private Rectangle rect;
	private Point stringDimensions;

	private int forcedWidth = 0;
	private int forcedHeight = 0;

	private boolean isHorizontal;

	private boolean isActive = false;
	private boolean isConfigured = false;
	private boolean isNoChoice = false;

	private MouseManager mouseManager;
	private CanvasSpace space;

	private List<CanvasBadge> badges = new ArrayList<>();
	private Side side;

	public CanvasButton(String str, int x, int y, boolean isHorizontal,
			MouseManager mouseManager, CanvasSpace space, Side side) {
		this.str = str;
		this.x = x;
		this.y = y;
		this.isHorizontal = isHorizontal;
		this.side = side;

		this.mouseManager = mouseManager;
		this.space = space;

		this.rect = new Rectangle(x, y, 10, 10);
	}

	public void addBadge(CanvasBadge badge) {
		this.badges.add(badge);
	}

	public void clearBadges() {
		this.badges.clear();
	}

	public void setText(String text) {
		this.str = text;
	}

	public boolean isConfigured() {
		return isConfigured;
	}

	public boolean isNoChoice() {
		return isNoChoice;
	}

	public void setNoChoice(boolean isNoChoice) {
		this.isNoChoice = isNoChoice;
	}

	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
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
		if (stringDimensions == null)
			return;

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
					onClickListener.handleEvent(emptyEvent);
				}

				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			} else {
				gc.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
			}

			gc.fillRectangle(rect);
		} else if (isNoChoice) {
			gc.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
			gc.fillRectangle(rect);
		} else if (isConfigured) {
			gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
			gc.fillRectangle(rect);
		} else {
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			gc.fillRectangle(rect);
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.drawRectangle(rect);
		}

		prevWasClicked = isMouseDown;

		int tx, ty;

		if (isHorizontal) {
			// -----------
			// | Text |
			// -----------
			tx = x + (rect.width - stringDimensions.x) / 2;
			ty = y + (rect.height - stringDimensions.y) / 2;
		} else {
			// -------
			// | 1 |
			// | 1 |
			// | 1 |
			// | 1 |
			// -------
			tx = x + (rect.width - stringDimensions.y) / 2;
			ty = y + (rect.height - stringDimensions.x) / 2;
		}

		float[] oldData = new float[6];

		if (!isHorizontal) {
			transform.getElements(oldData);

			transform.translate(tx, ty);
			transform.rotate((float) (-90));
			transform.translate(-tx, -ty);

			gc.setTransform(transform);

			tx -= stringDimensions.x;
//			ty += 10;
		}

		gc.drawText(str, tx, ty, SWT.DRAW_TRANSPARENT);

		int spacing = side == Side.Top ? 20 : 10;

		int sumOffset = spacing;

		for (int i = 0; i < badges.size(); i++) {
			final CanvasBadge badge = badges.get(i);
			int off = -sumOffset;

			if (side == Side.Right || side == Side.Top) {
				badge.setOffset(0);
				off = rect.width + sumOffset;
			} else if (side == Side.Bottom) {
				off -= 70;
			}

			sumOffset += badge.getWidth() + spacing;

			badge.setPosition(x + off, ty);
			badge.draw(display, affineTransform, transform, gc);
		}

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
