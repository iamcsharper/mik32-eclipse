package com.xupoh.collator.logic;

import org.eclipse.swt.widgets.Composite;

import com.xupoh.collator.models.Mik32;
import com.xupoh.collator.parts.PinoutTree;

public class PinoutTreeController {
	private PinoutTree view;

	private Mik32 mik32;

	public PinoutTreeController(Composite parent, Mik32 mik32) {
		this.mik32 = mik32;
		this.view = new PinoutTree(parent, mik32);
	}

	public PinoutTree getView() {
		return view;
	}
}
