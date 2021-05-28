package com.xupoh.collator.logic;

import org.eclipse.swt.widgets.Composite;

import com.xupoh.collator.models.Mik32;
import com.xupoh.collator.parts.PinoutCanvas;

public class PinoutCanvasController {
	private PinoutCanvas view;
	private Mik32 mik32;

	public PinoutCanvasController(Composite parent, Mik32 mik32) {
		this.mik32 = mik32;
		this.view = new PinoutCanvas(parent, mik32);
	}

	public PinoutCanvas getView() {
		return view;
	}
}
