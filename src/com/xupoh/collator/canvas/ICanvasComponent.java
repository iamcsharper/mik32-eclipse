package com.xupoh.collator.canvas;

import java.awt.geom.AffineTransform;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

public interface ICanvasComponent {
	Rectangle getOuterRect();

	void update();

	void draw(Display display, AffineTransform affineTransform, Transform transform,
			GC gc);
}
