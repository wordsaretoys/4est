package com.wordsaretoys.rise.pattern;

import java.util.Random;


/**
 * maps all I2 to random ints
 */
public class I2IMap implements Pattern.I2I {

	int[] data;
	int modu;
	
	public I2IMap(int length) {
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
	public int get(int x, int y) {
		int fx = data[x & modu];
		return data[(fx + y) & modu];
	}
}
