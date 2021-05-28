package com.xupoh.collator.logic;

import org.eclipse.swt.widgets.Composite;

import com.xupoh.collator.models.Mik32;
import com.xupoh.collator.parts.PinoutTable;
import com.xupoh.collator.parts.PinoutTree;
import com.xupoh.collator.parts.SampleView;

public class PinoutTableController {
	private PinoutTable view;

	private Mik32 mik32;

	public PinoutTableController(Composite parent, Mik32 mik32) {
		this.mik32 = mik32;
		this.view = new PinoutTable(parent, mik32);
	}

	public PinoutTable getView() {
		return view;
	}

	public void onGenPadClick() {
		final int a = mik32.generatePort(0);
		final int b = mik32.generatePort(1);
		final int c = mik32.generatePort(2);
		// System.out.println("Generated results: ");

		StringBuilder sb = new StringBuilder();
		sb.append("PortA = ");
		sb.append(Integer.toHexString(a));
		sb.append("\nPortB = ");
		sb.append(Integer.toHexString(b));
		sb.append("\nPortC = ");
		sb.append(Integer.toHexString(c));

		SampleView.instance.showMessage(sb.toString());
	}
}
