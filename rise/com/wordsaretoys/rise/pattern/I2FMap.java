package com.wordsaretoys.rise.pattern;

import java.util.Random;


/**
 * maps all I2 to random floats
 */
public class I2FMap implements Pattern.I2F {

	static float MI = 1f / (float) Integer.MAX_VALUE;
	
	int[] data;
	int modu;
	
	public I2FMap(int length) {
		data = new int[length];
		modu = length - 1;
	}
	
	public void generate() {
		Random rng = new Random();
		for (int i = 0; i < data.length; i++) {
			data[i] = Math.abs(rng.nextInt());
		}
	}

	@Override
	public float get(int x, int y) {
		int fx = data[x & modu];
		int fy = data[(fx + y) & modu];
		return (float) fy * MI;
	}
}
