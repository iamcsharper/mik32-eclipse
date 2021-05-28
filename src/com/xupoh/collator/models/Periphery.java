package com.xupoh.collator.models;

public class Periphery implements Comparable<Periphery> {
	protected int id;
	protected PeripheryType type;
	protected String canonicalName;

	public Periphery(String originalName, int id, PeripheryType type) {
		this.id = id;
		this.type = type;
		this.canonicalName = originalName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Periphery other = (Periphery) obj;
		if (id != other.id)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public String getCanonicalName() {
		return this.canonicalName;
	}

	@Override
	public String toString() {
		return this.type.toString() + "_" + this.id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PeripheryType getType() {
		return type;
	}

	public void setType(PeripheryType type) {
		this.type = type;
	}

	@Override
	public int compareTo(Periphery o) {
		return this.toString().compareTo(o.toString());
	}
}
