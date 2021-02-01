package com.xupoh.collator.parts.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;

public class GlobalConstants {
	public static final GridData LAYOUT_GRID_FILL = new GridData(GridData.FILL_BOTH);

	public static final List<String> JTAG_NAMES = new ArrayList<>();

	static {
		JTAG_NAMES.add("TDO");
		JTAG_NAMES.add("TRSTn");
		JTAG_NAMES.add("TMS");
		JTAG_NAMES.add("TCK");
		JTAG_NAMES.add("TDI");
	}
}
