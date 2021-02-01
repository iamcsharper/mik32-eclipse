package com.xupoh.collator.parts.common;

public class PinModeInfo {
	public int id;

	public String designation;

	public PinType type;

	public String description;

	public PinModeInfo(int id, String designation, PinType type, String description) {
		super();
		this.id = id;
		this.designation = designation;
		this.type = type;
		this.description = description;
	}
}