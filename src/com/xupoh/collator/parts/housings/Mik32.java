package com.xupoh.collator.parts.housings;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.xupoh.collator.fs.DocumentLoader;
import com.xupoh.collator.parts.common.HousingInfo;
import com.xupoh.collator.parts.common.PinInfo;
import com.xupoh.collator.parts.common.PinModeInfo;
import com.xupoh.collator.parts.common.PinType;

public class Mik32 extends AbstractHousing {
	public Mik32() {
		super("MIK32 LQFP64");

		try {
			HousingInfo info = DocumentLoader.parseHousing("/mik32_description.xml");
			pins = info.pinsInfo;
			interfacesToPins = info.interfacesToPins;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int generatePort(int portNum) {
		int result = 0;

		for (PinInfo pin : this.pins) {
			if (pin.getPortNum() != portNum)
				continue;

			final PinModeInfo selectedMode = pin.getSelectedMode();

			if (selectedMode.type == PinType.GND || selectedMode.type == PinType.Power)
				continue;

			if (portNum == 2) {
				System.out.println("Pin #" + pin.getId() + " has a selected mode of "
						+ selectedMode.id);
			}

			result |= selectedMode.id << (pin.getGpioId() * 2);
		}

		return result;
	}

	@Override
	public int getPinsCount() {
		return 64;
	}
}
