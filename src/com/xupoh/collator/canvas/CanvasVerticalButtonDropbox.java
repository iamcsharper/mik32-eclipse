package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.xupoh.collator.util.SWT2Dutil;

public class CanvasVerticalButtonDropbox implements ICanvasComponent {
	private boolean isOpen = false;

	private CanvasButton[] buttons = new CanvasButton[] {};

	private Listener itemSelectedListener;

	private int x, y;
	private int width;

	private int idData = -1;

	public void setIdData(int data) {
		this.idData = data;
	}

	public int getIdData() {
		return this.idData;
	}

	private PivotSubtract pivot = PivotSubtract.None;

	public enum PivotSubtract {
		Horizontal, Vertical, None
	}

	private MouseManager mouseManager;

	public CanvasVerticalButtonDropbox(int x, int y, int width,
			MouseManager mouseManager) {
		this.x = x;
		this.y = y;
		this.width = width;

		this.mouseManager = mouseManager;
	}

	public void assignButtons(CanvasButton[] buttons) {
		this.buttons = buttons;

		for (int i = 0; i < buttons.length; ++i) {
			CanvasButton button = buttons[i];

			// if (button.getOuterRect().contains(mouseManager.))
			final int id = i;
			button.setOnClickListener(new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (!isOpen)
						return;

					noneOfDropboxTriggersClickedFlag = false;

					event.index = idData;
					event.keyCode = id;

					if (itemSelectedListener != null) {
						itemSelectedListener.handleEvent(event);
					}
				}
			});
		}
	}

	public void setOnItemSelectedListener(Listener listener) {
		this.itemSelectedListener = listener;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void toggle() {
		isOpen = !isOpen;
	}

	public void setIsOpen(boolean value) {
		this.isOpen = value;
	}

	private int calcHeight() {
		int height = 0;

		for (int i = 0; i < buttons.length; ++i) {
			height += buttons[i].getOuterRect().height;
		}

		return height;
	}

	@Override
	public Rectangle getOuterRect() {
		return new Rectangle(x, y, width, calcHeight());
	}

	@Override
	public void draw(Display display, AffineTransform affineTransform,
			Transform transform, GC gc) {
		if (!isOpen)
			return;

		for (int i = 0; i < buttons.length; ++i) {
			CanvasButton button = buttons[i];
			button.updateStringDimensions(gc);
		}

		Point p = new Point(x, y);

		if (pivot == PivotSubtract.Horizontal) {
			p.x = (int) (p.x - width / affineTransform.getScaleX());
		}
		if (pivot == PivotSubtract.Vertical) {
			p.y = (int) (p.y - calcHeight() / affineTransform.getScaleY());
		}

		Point screenSpaceP = SWT2Dutil.transformPoint(affineTransform, p);

		boolean isAnyClicked = false;

		for (int i = 0; i < buttons.length; ++i) {
			CanvasButton button = buttons[i];

			button.setX(screenSpaceP.x);
			button.setY(screenSpaceP.y + button.getOuterRect().height * i);
			button.setForcedLocalWidth(width);

			if (mouseManager.isLMBDown() && button.getOuterRect()
					.contains(mouseManager.getMousePointInScreenSpace())) {
				isAnyClicked = true;
			}

			button.draw(display, affineTransform, transform, gc);
		}

		if (!isAnyClicked && noneOfDropboxTriggersClickedFlag) {
			resetDefferedClose();

			isOpen = false;
		}
	}

	public void pivotTo(PivotSubtract pivot) {
		this.pivot = pivot;
	}

	private boolean noneOfDropboxTriggersClickedFlag = false;

	public void defferedClose() {
		noneOfDropboxTriggersClickedFlag = true;
	}

	@Override
	public void update() {

	}

	public void resetDefferedClose() {
		noneOfDropboxTriggersClickedFlag = false;
	}
}
