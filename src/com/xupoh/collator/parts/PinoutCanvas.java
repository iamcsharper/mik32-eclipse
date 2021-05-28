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

import com.xupoh.collator.canvas.CanvasBadge;
import com.xupoh.collator.canvas.CanvasButton;
import com.xupoh.collator.canvas.CanvasPowerBus;
import com.xupoh.collator.canvas.CanvasPowerBus.Type;
import com.xupoh.collator.canvas.CanvasSpace;
import com.xupoh.collator.canvas.CanvasVerticalButtonDropbox;
import com.xupoh.collator.canvas.UltimateCanvas;
import com.xupoh.collator.models.Mik32;
import com.xupoh.collator.models.PinInfo;
import com.xupoh.collator.models.PinModeInfo;
import com.xupoh.collator.models.PinType;
import com.xupoh.collator.models.Side;

public class PinoutCanvas extends UltimateCanvas {

	public static final int PIN_WIDTH = 120;

	private Mik32 mik32;

	private CanvasButton resetViewButton;
	private CanvasVerticalButtonDropbox dropbox;
	private List<CanvasButton> pinButtons;

	private Listener onPinClickListener;

	public void setOnPinClickListener(Listener listener) {
		onPinClickListener = listener;
	}

	public PinoutCanvas(Composite parent, Mik32 mik32) {
		super(parent);

		this.mik32 = mik32;

		initPinButtons();
		resetView();
		initButtons();
		initEvents();

		this.mik32.addOnPinModeUpdateListener((int id, int mode, PinInfo info) -> {
			System.out.println("[PinoutCanvas] updating mode!");
			this.updatePinModeView(id, mode, info);
		});
	}

	private void initPinButtons() {
		if (pinButtons != null) {
			pinButtons.clear();
		} else {
			pinButtons = new ArrayList<>(mik32.info.getPins().size());
		}

		int x = 0;
		int y = 0;
		boolean isHorizontal = true;
		Side side;

		for (int i = 0; i < mik32.info.getPins().size(); ++i) {
			CanvasPowerBus bus = null;
			final PinInfo pinInfo = mik32.info.getPins().get(i);

			if (i < 16) { // Left
				y = 20 * i;
				x = -PIN_WIDTH;
				isHorizontal = true;
				side = Side.Left;
			} else if (i < 32) { // Bottom
				x = 20 * (i - 16);
				y = 320;
				isHorizontal = false;
				side = Side.Bottom;
			} else if (i < 48) { // Right
				x = 320;
				y = 20 * (47 - i);
				isHorizontal = true;
				side = Side.Right;
			} else { // Top
				x = 20 * (63 - i);
				y = -PIN_WIDTH;
				isHorizontal = false;
				side = Side.Top;
			}

			if (pinInfo.getModesSize() == 1) {

				if (pinInfo.getSelectedMode().type == PinType.GND) {
					bus = new CanvasPowerBus(Type.Ground, side, "GND");
				} else if (pinInfo.getSelectedMode().type == PinType.Power) {
					bus = new CanvasPowerBus(Type.Power, side,
							pinInfo.getSelectedMode().designation);
				}

			}

			final CanvasButton btn = createPinButton(i, x, y, isHorizontal, side);

			pinButtons.add(btn);
			children.add(btn);

			if (bus != null) {
				int xBus = x;
				int yBus = y;

				if (side == Side.Left) {
					yBus += 10;
				} else if (side == Side.Bottom) {
					xBus += 10;
					yBus += PIN_WIDTH;
				} else if (side == Side.Right) {
					xBus += PIN_WIDTH;
					yBus += 10;
				} else if (side == Side.Top) {
					xBus += 10;
				}
				bus.setPosition(xBus, yBus);
				children.add(bus);
			}
		}

	}

	private CanvasButton createPinButton(int i, int x, int y, boolean isHorizontal,
			Side side) {
		int defModId = mik32.info.getPins().get(i).getSelectedModeId();
		final String numStr = String.valueOf(i + 1);

		final CanvasButton btn = new CanvasButton(numStr + " = " + defModId, x, y,
				isHorizontal, mouseManager, CanvasSpace.World, side);
		btn.setForcedLocalWidth(PIN_WIDTH);
		btn.setForcedLocalHeight(20);

		final int pin = i;
		final PinInfo pinInfo = mik32.info.getPins().get(pin);

		if (pinInfo.isConfigured() || pinInfo.getModesSize() == 1) {
			btn.setText(numStr + " " + pinInfo.getModeById(0).designation);
			btn.setNoChoice(true);
		} else {
			btn.setOnClickListener(new Listener() {

				@Override
				public void handleEvent(Event event) {
					// TODO: reuse existing CanvasButton[] array inside dropbox, or
					// resize
					// & shrink
					CanvasButton[] dropboxItems = new CanvasButton[pinInfo
							.getModesSize()];

					int j = 0;
					for (PinModeInfo pinMode : pinInfo.getDistinctModes()) {
						dropboxItems[j] = new CanvasButton(pinMode.designation, 0, 0,
								true, mouseManager, CanvasSpace.Screen, Side.Top);
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
		}

		return btn;
	}

	public void updatePinModeView(int id, int mode, PinInfo info) {
		final boolean isActive = info.isConfigured();
		final CanvasButton b = pinButtons.get(id);
		b.setText((id + 1) + " = " + mode);

		b.clearBadges();

		if (isActive) {
			b.addBadge(new CanvasBadge(info.getSelectedMode().designation));

			if (Math.random() > 0.6) {
				b.addBadge(new CanvasBadge("40% шанс"));
			}

			if (Math.random() > 0.8) {
				b.addBadge(new CanvasBadge("20% шанс"));
			}
		}

		b.setConfigured(isActive);
	}

	private void initButtons() {
		resetViewButton = new CanvasButton("Reset view", 10, 10, true, mouseManager,
				CanvasSpace.Screen, Side.Top);
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
				event.keyCode = mik32.info.getPins().get(event.index)
						.getDistinctModes()[event.keyCode].id;

				mik32.updatePinMode(event.index, event.keyCode);
			}
		});

		// children.add(resetViewButton);
		// children.add(dropbox);
	}

	private void initEvents() {

		addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				Point p = mouseManager.getMousePointInWorldSpace(affineTransform);

				boolean noButtonsClicked = !dropbox.getOuterRect().contains(p);

				for (int i = 0; i < pinButtons.size(); ++i) {
					if (pinButtons.get(i).getOuterRect().contains(p)) {
						// System.out.println("Clicked pin #" + i);
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

	@Override
	protected void paint(GC gc) {
		if (this.pinButtons == null)
			return;

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
		}

		for (int i = 0; i < children.size(); ++i) {
			children.get(i).update();
		}

		for (int i = 0; i < children.size(); ++i) {
			children.get(i).draw(getDisplay(), affineTransform, transform, gc);
		}

		// Absolute positioning

		gc.setTransform(null);

		resetViewButton.updateStringDimensions(gc);
		resetViewButton.draw(getDisplay(), affineTransform, transform, gc);

		dropbox.draw(getDisplay(), affineTransform, transform, gc);
	}
}
