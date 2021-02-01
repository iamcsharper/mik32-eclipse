package com.xupoh.collator.parts.housings;

import java.util.HashMap;
import java.util.List;

import com.xupoh.collator.parts.common.PinInfo;

public abstract class AbstractHousing {
	public final String name;
	protected List<PinInfo> pins;
	public HashMap<String, List<Integer>> interfacesToPins;

	/**
	 * Unsafe (in terms of reactiveness) operation, required only for raw data
	 * binding
	 * 
	 * @see org.eclipse.jface.viewers.TableViewer
	 * @return
	 */
	public List<PinInfo> getPins() {
		return pins;
	}

	public AbstractHousing(String name) {
		this.name = name;
	}

	public abstract int getPinsCount();

	public PinInfo getPin(int i) {
		return pins.get(i);
	}
}
