package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class UltimateCanvas extends Canvas {

	protected Composite parentComposite;
	protected AffineTransform affineTransform;
	protected float zoom = 1f;

	// The timer interval in milliseconds
	private static final int TIMER_INTERVAL = 5;
	private Runnable runner;

	protected MouseManager mouseManager;

	protected List<ICanvasComponent> children = new ArrayList<>();

	private void initGraphics() {
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		affineTransform = AffineTransform.getTranslateInstance(
				getDisplay().getBounds().width / 2, getDisplay().getBounds().height / 2);
	}

	private void initEvents() {
		addControlListener(new ControlAdapter() { /* resize listener */
			public void controlResized(ControlEvent event) {
				// TODO: redraw on invalidate
			}
		});

		addListener(SWT.MouseMove, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mouseManager.mouseMoveHandler(event);

				if (mouseManager.isDragging()) {
					affineTransform.preConcatenate(mouseManager.getSetDragMatrix());
				}
			}
		});

		addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				mouseManager.mouseDownHandler(e);
			}
		});

		addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				mouseManager.mouseUpHandler(e);
			}
		});

		addListener(SWT.MouseVerticalWheel, new Listener() {

			@Override
			public void handleEvent(Event event) {
				mouseManager.mouseScrollHandler(event);

				zoom = mouseManager.calcRequiredZoomFactor();

				// System.out.println("Zoom is " + zoom);

				affineTransform.preConcatenate(mouseManager.getSetToMouseOriginMatrix());
				affineTransform.preConcatenate(AffineTransform.getScaleInstance(
						zoom / affineTransform.getScaleX(),
						zoom / affineTransform.getScaleY()));
				affineTransform
						.preConcatenate(mouseManager.getInvSetToMouseOriginMatrix());
			}

		});

		addPaintListener(new PaintListener() { /* paint listener */
			public void paintControl(PaintEvent event) {
				paint(event.gc);
			}
		});
	}

	public UltimateCanvas(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED | SWT.BORDER);
		this.parentComposite = parent;

		initGraphics();

		mouseManager = new MouseManager();

		initEvents();

		// Set up the timer for the animation
		runner = new Runnable() {
			public void run() {
				if (!isDisposed() && !getShell().isDisposed()) {
					for (ICanvasComponent comp : children) {
						comp.update();
					}

					redraw();
					getDisplay().timerExec(TIMER_INTERVAL, this);
				}
			}
		};

		// Launch the timer
		getDisplay().timerExec(TIMER_INTERVAL, runner);
	}

	protected final Transform transform = new Transform(getDisplay());

	protected void paint(GC gc) {
		gc.setAdvanced(true);
	}

	public void resetView() {
		affineTransform = new AffineTransform();
		affineTransform.preConcatenate(
				AffineTransform.getTranslateInstance((420) / 2, (420) / 2));

		mouseManager.resetZoom();
	}
}
