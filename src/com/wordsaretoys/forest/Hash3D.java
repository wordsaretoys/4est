package com.wordsaretoys.forest;

import java.util.Random;

/**
 * provides a random 3d hash map
 */
public class Hash3D {

	public static class Cell {
		private int offset;
		public float odds;
		public long seed;
	}
	
	Cell[] map;
	int f0, f1, f2;
	int length, modulus;
	
	public Hash3D(int len) {
		length = len;
		modulus = length - 1;
		map = new Cell[length];
		
		Random rng = new Random();
		f0 = rng.nextInt(length);
		f1 = rng.nextInt(length);
		f2 = rng.nextInt(length);
		
		for (int i = 0; i < map.length; i++) {
			Cell cell = new Cell();
			cell.offset = rng.nextInt(length);
			cell.odds = rng.nextFloat();
			cell.seed = rng.nextLong();
			map[i] = cell;
		}
	}
	
	public Cell get(float x, float y, float z) {
		int a = map[(int)(x * f0) & modulus].offset;
		int b = map[(int)(y * f1 + a) & modulus].offset;
		return map[(int)(z * f2 + b) & modulus];
	}
	
}
