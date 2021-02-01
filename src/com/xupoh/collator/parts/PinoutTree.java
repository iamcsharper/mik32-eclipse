package com.xupoh.collator.parts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.xupoh.collator.parts.common.PinInfo;
import com.xupoh.collator.parts.common.PinModeInfo;
import com.xupoh.collator.parts.housings.Mik32;

public class PinoutTree extends Tree {

	@Override
	protected void checkSubclass() {

	}

	private Mik32 mik32;

	public PinoutTree(Composite parent, Mik32 mik32) {
		super(parent, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL);

		this.mik32 = mik32;

		for (String key : mik32.interfacesToPins.keySet()) {
			final List<Integer> pins = mik32.interfacesToPins.get(key);

			TreeItem iItem = new TreeItem(this, 0);
			iItem.setText(key);

			for (int i = 0; i < pins.size(); i++) {
				final int pinId = pins.get(i);
				final PinInfo pin = mik32.getPin(pinId - 1);
				final PinModeInfo info = pin.findModeForName(key);

				if (info == null) {
					System.out.println(
							"Null info found for pin #" + pinId + " mode " + key);
				}

				final TreeItem jItem = new TreeItem(iItem, 0);
				jItem.setText("Pin #" + pinId + " - " + info.designation);
				final boolean checked = info.id == pin.selectedModeId;
				if (checked) {
					iItem.setChecked(true);
				}
				jItem.setChecked(checked);
			}
		}
	}

	public void updateCheckboxes() {
		int i = 0;
		for (String key : mik32.interfacesToPins.keySet()) {
			final List<Integer> pins = mik32.interfacesToPins.get(key);
			final TreeItem periphItem = this.getItem(i);

			boolean isOneChecked = false;

			for (int j = 0; j < pins.size(); j++) {
				final int pinId = pins.get(j);
				final PinInfo pin = mik32.getPin(pinId - 1);
				final PinModeInfo info = pin.findModeForName(key);

				final TreeItem pinItem = periphItem.getItem(j);

				final boolean checked = info.id == pin.selectedModeId;

				if (checked) {
					isOneChecked = true;
				}

				pinItem.setChecked(checked);
			}

			periphItem.setChecked(isOneChecked);

			i++;
		}
	}
}
