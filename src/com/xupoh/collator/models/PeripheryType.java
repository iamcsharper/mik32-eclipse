package com.xupoh.collator.models;

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
	 * Hardware 16-bit timer support
	 */
	Timer16(8),
	/**
	 * Hardware 32-bit timer support
	 */
	Timer32(16),
	/**
	 * Hardware JTAG debug support
	 */
	TDI(32),
	
	/**
	 * Configurable GPIO
	 */
	GPIO(64),
	
	/**
	 * No peripheral, just Power/GND
	 */
	None(64);
	
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
