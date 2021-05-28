package com.xupoh.collator.models;

public enum Side {
	Left, Right, Top, Bottom;
	
	public boolean isHorizontal() {
		return this == Left || this == Right;
	}

	public Side getOpposite() {
		if (this == Left)
			return Right;

		if (this == Right)
			return Left;

		if (this == Top)
			return Bottom;

		return Top;
	}
}
