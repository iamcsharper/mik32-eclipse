package com.xupoh.collator.models;

public enum PinType {
	Input, Output, IO, Power, GND, OSC;

	@Override
	public String toString() {
		switch (this) {
		case Input:
			return "Вход";
		case Output:
			return "Выход";
		case IO:
			return "Вход-выход";
		case Power:
			return "Питание";
		case GND:
			return "Земля";
		case OSC:
			return "Осц.";
		}

		return "Unknown pintype";
	}
}