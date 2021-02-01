package com.xupoh.collator.parts.common;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinInfo {
	private int pinId;

	private int portNum = -1;
	private int gpioId = -1;

	public int getId() {
		return pinId;
	}

	public int getGpioId() {
		return gpioId;
	}

	public int getPortNum() {
		return portNum;
	}

	public void setId(int id) {
		this.pinId = id;
	}

	public void setGpioId(int id) {
		this.gpioId = id;
	}

	public void setPortNum(int port) {
		this.portNum = port;
	}

	private HashMap<Integer, PinModeInfo> modesInfo = new HashMap<>();

	public int getModesSize() {
		return modesInfo.size();
	}

	public PinModeInfo[] getDistinctModes() {
		PinModeInfo[] result = new PinModeInfo[modesInfo.size()];
		final Object[] copy = modesInfo.values().toArray();

		for (int i = 0; i < copy.length; ++i) {
			result[i] = (PinModeInfo) copy[i];
		}

		return result;
	}

	public PinModeInfo getModeById(int id) {
		return modesInfo.get(id);
	}

	public int selectedModeId = 0;

	public String analogFunction = null;

	public PinInfo(int pin_id, String analogFunction) {
		super();

		this.pinId = pin_id;

		if (analogFunction != null && analogFunction.length() > 1) {
			this.analogFunction = analogFunction;
		}
	}

	final Pattern gpioRegexp = Pattern.compile("^GPIO(\\d)+_([\\d]+)", Pattern.MULTILINE);

	public PinInfo addMode(PinModeInfo modeInfo) throws IllegalArgumentException {
		// First one
		if (modesInfo.size() == 0) {
			System.out.println("Set default mode of " + pinId + " to " + modeInfo.id);
			selectedModeId = modeInfo.id;
		}

		if (modesInfo.size() >= 3) {
			throw new IllegalArgumentException(
					"More than 3 pinmodes are disallowed due to manufacturing issues.");
		}

		modesInfo.put(modeInfo.id, modeInfo);

		if (modeInfo.type == PinType.IO && modeInfo.designation.contains("GPIO")) {
			final Matcher matcher = gpioRegexp.matcher(modeInfo.designation);

			if (matcher.find()) {
				this.portNum = Integer.parseInt(matcher.group(1));
				this.gpioId = Integer.parseInt(matcher.group(2));
			} else {
				throw new IllegalArgumentException(
						"A GPIO pin does not have an appropriate format of GPIO{NUM}_{NUM}: "
								+ modeInfo.designation);
			}
		}

		return this;
	}

	public PinModeInfo findModeForName(String name) {
		for (PinModeInfo info : modesInfo.values()) {
			if (name == "JTAG") {
				// System.out.println("We are finding JTAG, got ")
				if (GlobalConstants.JTAG_NAMES.contains(info.designation))
					return info;
			} else if (info.designation.contains(name))
				return info;
		}

		return null;
	}

	public String[] createModesStrings() {
		String[] result = new String[modesInfo.size()];

		for (int i = 0; i < modesInfo.size(); ++i) {
			result[i] = modesInfo.get(i).designation;
		}

		return result;
	}

	public PinModeInfo getSelectedMode() {
		return this.modesInfo.get(this.selectedModeId);
	}
}