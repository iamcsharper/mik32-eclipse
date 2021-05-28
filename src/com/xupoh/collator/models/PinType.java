package com.xupoh.collator.models;

public enum PinType {
	Input, Output, IO, Power, GND, OSC;

	@Override
	public String toString() {
		switch (this) {
		case Input:
			return "����";
		case Output:
			return "�����";
		case IO:
			return "����-�����";
		case Power:
			return "�������";
		case GND:
			return "�����";
		case OSC:
			return "���.";
		}

		return "Unknown pintype";
	}
}