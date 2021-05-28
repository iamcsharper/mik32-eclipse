package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

import com.xupoh.collator.util.SWT2Dutil;

public class MouseManager {

	private static final float ZOOM_SPEED = 1 / 6f;
	private static final float PAN_SPEED = 1f;

	private int mouseX, mouseY;
	private Point mousePoint;
	private int startX, startY;
	private int lastX, lastY;

	private boolean lmbMouseDown = false;
	private boolean rmbMouseDown = false;
	private boolean wheelMouseDown = false;

	public MouseManager() {
		this.mouseX = 0;
		this.mouseY = 0;

		this.mousePoint = new Point(0, 0);
	}

	private float lastSetX = 0;
	private float lastSetY = 0;

	private float sumWheelCounts = 0;

	private static final float K = 2;
	private static final float M = 2f;
	private static final float m = 0.3f;
	private static final float a1 = 1 - m;
	private static final float b1 = (float) (Math.log(10 / m - 10) / (5 * Math.log(K)));
	private static final float a2 = 1 - M;
	private static final float b2 = (float) (b1 * a1 / a2);

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public float calcRequiredZoomFactor() {
		return calculate_zoom(sumWheelCounts);
	}

	private float calculate_zoom(float x) {
		if (Math.abs(x) < 0.001f)
			return 1;

		if (x <= 0) {
			return (float) (m + a1 * Math.pow(K, b1 * x));
		}

		return (float) (M + a2 * Math.pow(K, b2 * x));
	}

	public void mouseMoveHandler(Event event) {
		mouseX = event.x;
		mouseY = event.y;

		mousePoint.x = mouseX;
		mousePoint.y = mouseY;
	}

	public void mouseScrollHandler(Event e) {
		float fac = e.count * ZOOM_SPEED;

		sumWheelCounts += fac;

		if (sumWheelCounts < -7) {
			sumWheelCounts = -7;
		}

		if (sumWheelCounts > 7) {
			sumWheelCounts = 7;
		}
	}

	public void mouseDownHandler(Event e) {
		switch (e.button) {
		default:
		case 1:
			//System.out.println("LMB pressed");
			lmbMouseDown = true;

			startX = e.x - lastX;
			startY = e.y - lastY;
			break;
		case 2:
			//System.out.println("Wheel pressed");
			wheelMouseDown = true;
			break;
		case 3:
			//System.out.println("RMB pressed");
			rmbMouseDown = true;
			break;
		}
	}

	public void mouseUpHandler(Event e) {
		switch (e.button) {
		default:
		case 1:
			//System.out.println("LMB pressed");
			lmbMouseDown = false;

			lastX = e.x - startX;
			lastY = e.y - startY;

			break;
		case 2:
			//System.out.println("Wheel pressed");
			wheelMouseDown = false;
			break;
		case 3:
			//System.out.println("RMB pressed");
			rmbMouseDown = false;
			break;
		}
	}

	public boolean isLMBDown() {
		return lmbMouseDown;
	}

	public boolean isWheelDown() {
		return wheelMouseDown;
	}

	public boolean isRMBDown() {
		return rmbMouseDown;
	}

	// TODO: delta check
	public boolean isDragging() {
		return lmbMouseDown;
	}

	public float getDragX() {
		return PAN_SPEED * (mouseX - startX);
	}

	public float getDragY() {
		return PAN_SPEED * (mouseY - startY);
	}

	public AffineTransform getSetToMouseOriginMatrix() {
		return AffineTransform.getTranslateInstance(-mouseX, -mouseY);
	}

	public AffineTransform getSetDragMatrix() {
		float Tx = getDragX();
		float Ty = getDragY();

		AffineTransform result = AffineTransform.getTranslateInstance(Tx - lastSetX, Ty - lastSetY);

		lastSetX = Tx;
		lastSetY = Ty;

		return result;
	}

	public AffineTransform getInvSetToMouseOriginMatrix() {
		return AffineTransform.getTranslateInstance(mouseX, mouseY);
	}

	public Point getMousePointInScreenSpace() {
		return mousePoint;
	}

	public Point getMousePointInWorldSpace(AffineTransform transform) {
		return SWT2Dutil.inverseTransformPoint(transform, mousePoint);
	}

	public void resetZoom() {
		sumWheelCounts = 0;
	}
}
