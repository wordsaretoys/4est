package com.wordsaretoys.rise.pattern;

import java.util.Random;


/**
 * maps all I2 to random 0 or 1 value
 * using specified cutoff threshold
 */
public class I2FCutMap implements Pattern.I2F {

	static float MI = 1f / (float) Integer.MAX_VALUE;
	
	int[] data;
	int modu;
	float level;
	
	public I2FCutMap(int length, float level) {
		data = new int[length];
		modu = length - 1;
		this.level = level;
	}
	
	public void generate() {
		Random rng = new Random();
		for (int i = 0; i < data.length; i++) {
			data[i] = rng.nextInt();
		}
	}

	@Override
	public float get(int x, int y) {
		int fx = data[x & modu];
		int fy = data[(fx + y) & modu];
		return ((fy * MI) > level) ? 1 : 0;
	}
}
