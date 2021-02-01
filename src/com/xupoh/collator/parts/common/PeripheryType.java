package com.xupoh.collator.parts.common;

import java.util.ArrayList;
import java.util.List;

public enum PeripheryType {
	/**
	 * Hardware based single-wire interface support
	 */
	I2C(1),
	/**
	 * Hardware based Universal Asynchronous Receiever Transmitter interface support  
	 */
	UART(2),
	/**
	 * Hardware based Serial Peripheral Interface support
	 */
	SPI(4),
	/**
	 * Hardware timer suport
	 */
	Timer(8),
	/**
	 * Hardware JTAG debug support
	 */
	TDI(16);
	
	private int _val;
	PeripheryType(int val){
		_val = val;
	}
	
	public int getValue() {
		return _val;
	}
	
	public static List<PeripheryType> parsePeripherals(int val) {
		List<PeripheryType> apList = new ArrayList<PeripheryType>();
	    for (PeripheryType ap : values())
	    {
	      if ((val & ap.getValue()) != 0)
	        apList.add(ap);
	    }
	    return apList;
	}
}
