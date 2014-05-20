package com.wordsaretoys.rise.utility;

public class Misc {

	/**
	 * clamp a number to limits
	 * 
	 * @param x number to clamp
	 * @param l number representing lower bound
	 * @param u number representing upper bound
	 * @return clamped value
	 */
	
	static public float clamp(float x, float l, float u) {
		if (x < l)
				return l;
		if (x > u)
				return u;
		return x;
	}
	
	/**
	 * scale a number to specified limits
	 * 
	 * @param x number in range (0..1)
	 * @param l lower bound
	 * @param u upper bound
	 * @return scaled value
	 */
	
	static public float scale(float x, float l, float u) {
		return l + (u - l) * x;
	}

	/**
	 * generate a hash value for a floating point vector
	 */
	static public long hash(float x, float y, float z, long w) {
		return 	(long)(x * Long.MAX_VALUE) ^ (long)(y * Long.MAX_VALUE) ^ 
				(long)(z * Long.MAX_VALUE) ^ (long)(w * Long.MAX_VALUE);
	}
	
}
