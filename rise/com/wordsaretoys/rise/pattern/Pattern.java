package com.wordsaretoys.rise.pattern;


/**
 * classes and interfaces that generalize pattern generation 
 */
public class Pattern {
	
	/*
	 * interfaces for real-valued functions
	 */
	
	public interface F1F {
		public float get(float x);
	}

	public interface F2F {
		public float get(float x, float y);
	}
	
	public interface F3F {
		public float get(float x, float y, float z);
	}

	/*
	 * interfaces for integer-valued functions
	 */
	
	public interface I1F {
		public float get(int x);
	}

	public interface I2F {
		public float get(int x, int y);
	}
	
	public interface I3F {
		public float get(int x, int y, int z);
	}
	
	public interface I2I {
		public int get(int x, int y);
	}
	
	/**
	 * cosine interpolation between two points
	 */
	public static float cerp(float y0, float y1, float mu) {
		float m2 = (float)(1 - Math.cos(mu * Math.PI)) * 0.5f;
		return (1 - m2) * y0 + m2 * y1;
	}
	
	/**
	 * cosine interpolation of I2F function
	 */
	public static float ipolate(I2F f, float x, float y) {
		int ix = (int)Math.floor(x);
		float fx = x - ix;
		if (fx < 0) {
			fx += 1;
		}
		
		int iy = (int)Math.floor(y);
		float fy = y - iy;
		if (fy < 0) {
			fy += 1;
		}
		
		float s0 = f.get(ix, iy);
		float s1 = f.get(ix + 1, iy);
		float s2 = f.get(ix, iy + 1);
		float s3 = f.get(ix + 1, iy + 1);
		
		float s01 = cerp(s0, s1, fx);
		float s23 = cerp(s2, s3, fx);
		return cerp(s01, s23, fy);
	}
	
}
