package com.xupoh.collator.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.xupoh.collator.canvas.CanvasButton;
import com.xupoh.collator.canvas.CanvasSpace;
import com.xupoh.collator.canvas.CanvasVerticalButtonDropbox;
import com.xupoh.collator.canvas.UltimateCanvas;
import com.xupoh.collator.parts.common.PinInfo;
import com.xupoh.collator.parts.common.PinModeInfo;
import com.xupoh.collator.parts.housings.AbstractHousing;

public class PinoutCanvas extends UltimateCanvas {

	public AbstractHousing housing = null;

	private CanvasButton resetViewButton;
	private CanvasVerticalButtonDropbox dropbox;
	private List<CanvasButton> pinButtons;

	private Listener onPinModeChangeListener;
	private Listener onPinClickListener;

	public void setOnPinModeChangeListener(Listener listener) {
		onPinModeChangeListener = listener;
	}

	public void setOnPinClickListener(Listener listener) {
		onPinClickListener = listener;
	}

	private void initPinButtons() {
		if (pinButtons != null) {
			pinButtons.clear();
		}

		pinButtons = new ArrayList<>(housing.getPinsCount());

		int x = 0;
		int y = 0;
		boolean isHorizontal = true;

		for (int i = 0; i < housing.getPinsCount(); ++i) {
			if (i < 16) { // Left
				y = 20 * i;
				x = -50;
				isHorizontal = true;
			} else if (i < 32) { // Bottom
				x = 20 * (i - 16);
				y = 320;
				isHorizontal = false;
			} else if (i < 48) { // Right
				x = 320;
				y = 20 * (47 - i);
				isHorizontal = true;
			} else { // Top
				x = 20 * (63 - i);
				y = -50;
				isHorizontal = false;
			}

			int defModId = housing.getPin(i).selectedModeId;

			final CanvasButton btn = new CanvasButton(
					String.valueOf(i + 1) + " = " + defModId, x, y, isHorizontal,
					mouseManager, CanvasSpace.World);
			btn.setForcedLocalWidth(50);
			btn.setForcedLocalHeight(20);

			final int pin = i;
			final PinInfo pinInfo = housing.getPin(pin);

			btn.setOnClickListener(new Listener() {

				@Override
				public void handleEvent(Event event) {
					// TODO: reuse existing CanvasButton[] array inside dropbox, or resize
					// & shrink
					CanvasButton[] dropboxItems = new CanvasButton[pinInfo
							.getModesSize()];

					int j = 0;
					for (PinModeInfo pinMode : pinInfo.getDistinctModes()) {
						dropboxItems[j] = new CanvasButton(pinMode.designation, 0, 0,
								true, mouseManager, CanvasSpace.Screen);
						j++;
					}

					dropbox.assignButtons(dropboxItems);
					dropbox.setIdData(pin);

					final Rectangle rect = btn.getOuterRect();

					int worldX = 0;
					int worldY = 0;

					// Left
					if (pin < 16) {
						worldX = rect.x;
						worldY = rect.y;

						dropbox.pivotTo(
								CanvasVerticalButtonDropbox.PivotSubtract.Horizontal);
					}
					// Bottom
					else if (pin < 32) {
						worldX = rect.x;
						worldY = rect.y + rect.height;

						dropbox.pivotTo(CanvasVerticalButtonDropbox.PivotSubtract.None);
					}
					// Right
					else if (pin < 48) {
						worldX = rect.x + rect.width;
						worldY = rect.y;

						dropbox.pivotTo(CanvasVerticalButtonDropbox.PivotSubtract.None);
					}
					// Top
					else {
						worldX = rect.x;
						worldY = rect.y;

						dropbox.pivotTo(
								CanvasVerticalButtonDropbox.PivotSubtract.Vertical);
					}

					dropbox.setX(worldX);
					dropbox.setY(worldY);
					dropbox.setIsOpen(true);
				}
			});

			pinButtons.add(btn);
			children.add(btn);
		}

	}

	public void updatePinModeView(int id, int mode) {
		pinButtons.get(id).setText((id + 1) + " = " + mode);
	}

	private void initButtons() {
		resetViewButton = new CanvasButton("Reset view", 10, 10, true, mouseManager,
				CanvasSpace.Screen);
		resetViewButton.setOnClickListener(new Listener() {

			@Override
			public void handleEvent(Event event) {
				resetView();
			}
		});

		dropbox = new CanvasVerticalButtonDropbox(100, 60, 100, mouseManager);
		dropbox.setX(-100);
		dropbox.setY(-100);
		dropbox.setIsOpen(false);
		dropbox.setOnItemSelectedListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.keyCode = housing.getPin(event.index)
						.getDistinctModes()[event.keyCode].id;
				updatePinModeView(event.index, event.keyCode);

				onPinModeChangeListener.handleEvent(event);
			}
		});

		children.add(resetViewButton);
		children.add(dropbox);
	}

	private void initEvents() {

		addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				Point p = mouseManager.getMousePointInWorldSpace(affineTransform);

				boolean noButtonsClicked = !dropbox.getOuterRect().contains(p);

				for (int i = 0; i < pinButtons.size(); ++i) {
					if (pinButtons.get(i).getOuterRect().contains(p)) {
						//System.out.println("Clicked pin #" + i);
						final Event evt = new Event();
						evt.index = i;
						if (onPinClickListener != null)
							onPinClickListener.handleEvent(evt);
						dropbox.resetDefferedClose();
						noButtonsClicked = false;
						break;
					}
				}

				// Clicked on free space
				if (noButtonsClicked) {
					// System.out.println("None of pins are clicked");
					dropbox.defferedClose();
				}
			}
		});
	}

	public PinoutCanvas(Composite parent) {
		super(parent);

		initButtons();

		initEvents();
	}

	@Override
	protected void paint(GC gc) {
		super.paint(gc);

		gc.setForeground(
				parentComposite.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));

		StringBuilder sb = new StringBuilder();

		// InWorld positioning

		transform.setElements((float) affineTransform.getScaleX(), 0, 0,
				(float) affineTransform.getScaleY(),
				(float) affineTransform.getTranslateX(),
				(float) affineTransform.getTranslateY());

		gc.setTransform(transform);

		sb = new StringBuilder();
		sb.append("X:");
		sb.append((int) affineTransform.getTranslateX());
		sb.append(",Y:");
		sb.append((int) affineTransform.getTranslateY());
		sb.append(",Zoom:");
		sb.append(((int) (100 * affineTransform.getScaleX())) / 100f);

		gc.drawText(sb.toString(), 10, 10);

		gc.drawRectangle(0, 0, 320, 320);
		gc.drawText("MIK32", 140, 140, SWT.DRAW_TRANSPARENT);

		for (int i = 0; i < pinButtons.size(); ++i) {
			CanvasButton btn = pinButtons.get(i);
			btn.updateStringDimensions(gc);
			btn.draw(getDisplay(), affineTransform, transform, gc);
		}

		// Absolute positioning

		gc.setTransform(null);

		resetViewButton.updateStringDimensions(gc);
		resetViewButton.draw(getDisplay(), affineTransform, transform, gc);

		dropbox.draw(getDisplay(), affineTransform, transform, gc);
	}

	public void setHousing(AbstractHousing housing) {
		this.housing = housing;

		initPinButtons();

		resetView();
	}
}
