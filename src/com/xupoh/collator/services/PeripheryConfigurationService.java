package com.xupoh.collator.services;

import java.util.List;

import com.xupoh.collator.MCUInfo;
import com.xupoh.collator.models.GlobalConstants;
import com.xupoh.collator.models.Periphery;
import com.xupoh.collator.models.PeripheryType;
import com.xupoh.collator.models.PinInfo;

public class PeripheryConfigurationService {

	public static final PeripheryConfigurationService instance = new PeripheryConfigurationService();

	public void tryConfigure(MCUInfo mcu, Periphery periphery)
			throws IllegalArgumentException {
		switch (periphery.getType()) {
		case I2C:
			System.out.println("Configuring the i2c periphery!");
			break;
		case SPI:
			System.out.println("Configuring the spi periphery!");
			
			final List<PinInfo> list = mcu.getPeripheralsToPins().get(periphery);
			
			for (PinInfo pin : list) {
				System.out.println("* " + pin.getId() + " = " + pin.getSelectedMode().designation);
			}
			break;
		case TDI:
			System.out.println("Configuring the tdi periphery!");
			break;
			
		case Timer16:
			System.out.println("Configuring the timer16 periphery!");
			break;
		case Timer32:
			System.out.println("Configuring the timer32 periphery!");
			break;
		case UART:
			System.out.println("Configuring the uart periphery!");
			break;
		default:
			throw new IllegalArgumentException(
					"Unsupported periphery: " + periphery.getCanonicalName());
		}
	}

	public Periphery parsePeripheryByDesignation(String des) {
		// I2C - contains I2C{NUM}
		// UART - contains UART{NUM}
		// SPI - contains SPI{NUM}
		// Timer32 - contains Timer32_{NUM}
		// Timer16 - contains Timer16_{NUM}
		// SPIFI - contains SPIFI
		// JTAG - TDO, TRSTn, TMS, TCK, TDI

		int id = 0;
		PeripheryType type = PeripheryType.None;
		String[] splitted = des.split("_");

		if (des.startsWith("I2C")) {
			id = Integer.parseInt(splitted[0].substring(3));
			type = PeripheryType.I2C;
		} else if (des.startsWith("UART")) {
			id = Integer.parseInt(splitted[0].substring(4));
			type = PeripheryType.UART;
		} else if (des.startsWith("SPI0") || des.startsWith("SPI1")
				|| des.startsWith("SPI2") || des.startsWith("SPI3")) {
			id = Integer.parseInt(splitted[0].substring(3));
			type = PeripheryType.SPI;
		} else if (des.startsWith("Timer16")) {
			id = Integer.parseInt(splitted[1]);
			type = PeripheryType.Timer16;
		} else if (des.startsWith("Timer32")) {
			id = Integer.parseInt(splitted[1]);
			type = PeripheryType.Timer32;
		} else if (des.startsWith("GPIO")) {
			id = Integer.parseInt(splitted[0].substring(4));
			type = PeripheryType.GPIO;
		} else if (GlobalConstants.JTAG_NAMES.contains(des)) {
			type = PeripheryType.TDI;
		} else {
			System.err.println(
					"[PeripheryConfigureService] Check if its correct, unknown periphery: "
							+ des);
		}

		return new Periphery(des, id, type);
	}
}
