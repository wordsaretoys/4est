package com.wordsaretoys.rise.utility;

public class Misc {

	static final long prime1 = 961748941L;
	static final long prime2 = 961750903L;
	static final long prime3 = 961813667L;
	static final long prime4 = 982449059L;
	
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
		return 	(long)(x * prime1) ^ (long)(y * prime2) ^ 
				(long)(z * prime3) ^ (long)(w * prime4);
	}

	/**
	 * copies rotation components from 4x4 matrix(es) to 3x3 matrix(es)
	 * @param m3 array representing 3x3 matrix(es)
	 * @param m3offset offset into 3x3 matrix array
	 * @param m4 array representing 4x4 matrix(es)
	 * @param m4offset offset into 4x4 matrix array
	 */
	static public void copyM4To3(float[] m3, int m3offset, float[] m4, int m4offset) {
		int i3 = m3offset;
		int i4 = m4offset;
		
		m3[i3++] = m4[i4++];
		m3[i3++] = m4[i4++];
		m3[i3++] = m4[i4++];
		i4++;
		m3[i3++] = m4[i4++];
		m3[i3++] = m4[i4++];
		m3[i3++] = m4[i4++];
		i4++;
		m3[i3++] = m4[i4++];
		m3[i3++] = m4[i4++];
		m3[i3++] = m4[i4++];
	}
	
}
