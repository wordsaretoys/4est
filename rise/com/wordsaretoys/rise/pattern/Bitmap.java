package com.wordsaretoys.rise.pattern;

import java.util.Random;

/**
 * provides utility methods for creating texture bitmaps
 */
public class Bitmap {

	static int Black = toRGBA(0, 0, 0, 1);
	
	/**
	 * convert 4 color values to RGBA value
	 */
	public static int toRGBA(float r, float g, float b, float a) {
		int br = (int)(r * 255) & 0xff;
		int bg = (int)(g * 255) & 0xff;
		int bb = (int)(b * 255) & 0xff;
		int ba = (int)(a * 255) & 0xff;
		return br + (bg << 8) + (bb << 16) + (ba << 24);
	}

	/**
	 * interpolate between two RGBA color values
	 */
	public static int mixRGBA(int c1, int c2, float mu) {
		float um = 1 - mu;
	
		int r1 = c1 & 0xff;
		int g1 = (c1 >> 8) & 0xff;
		int b1 = (c1 >> 16) & 0xff;
		int a1 = (c1 >> 24) & 0xff;
	
		int r2 = c2 & 0xff;
		int g2 = (c2 >> 8) & 0xff;
		int b2 = (c2 >> 16) & 0xff;
		int a2 = (c2 >> 24) & 0xff;
		
		int r = (int)(um * r1 + mu * r2) & 0xff;
		int g = (int)(um * g1 + mu * g2) & 0xff;
		int b = (int)(um * b1 + mu * b2) & 0xff;
		int a = (int)(um * a1 + mu * a2) & 0xff;
		
		return r + (g << 8) + (b << 16) + (a << 24);
	}

	/**
	 * scale the color elements of an RGBA value by specified factor
	 * will NOT scale alpha channel
	 */
	public static int scaleRGBA(int c, float s) {
		int r = c & 0xff;
		int g = (c >> 8) & 0xff;
		int b = (c >> 16) & 0xff;
		int a = (c >> 24) & 0xff;
		
		r = (int)(r * s) & 0xff;
		g = (int)(g * s) & 0xff;
		b = (int)(b * s) & 0xff;
		
		return r + (g << 8) + (b << 16) + (a << 24);
	}
	
	/**
	 * returns interpolated color from a color table and normalized value
	 */
	public static int getColor(int[] table, float value) {
		float f = value * (table.length - 1);
		int i = (int)(f);
		float mu = f - i;
		return mixRGBA(table[i], table[i + 1], mu);
	}
	
	/**
	 * recursively evolve a color pattern onto a bitmap
	 */
	public static class Divider {
		
		Random rng = new Random();
		int[] bitmap;
		int size;
		int[] color;
		float coeff;
		
		public int[] apply(int s, int[] c) {
			bitmap = new int[s * s];
			size = s;
			color = c;
			coeff = 1f / (float)(Math.log(s) / Math.log(2));
			descend(0, 0, size, 0);
			return bitmap;
		}
		
		void descend(int x, int y, int sz, float t) {
			t += rng.nextFloat();
			if (sz == 1) {
				int i = y * size + x;
				bitmap[i] = getColor(color, t * coeff);
				return;
			}
			sz = sz >> 1;
			descend(x, y, sz, t);
			descend(x + sz, y, sz, t);
			descend(x + sz, y + sz, sz, t);
			descend(x, y + sz, sz, t);
		}
		
	}

	/**
	 * recursively evolve a mixing pattern onto a bitmap
	 */
	public static class MixMaster {
		
		Random rng = new Random();
		int[] bitmap;
		float coeff;
		int size;
		int cut;
		
		public int[] apply(int s, int c) {
			bitmap = new int[s * s];
			size = s;
			cut = c;
			coeff = 1f / (float)Math.floor(Math.log(c) / Math.log(2));
			descend(0, 0, size, 0, 0, 0, 0);
			return bitmap;
		}
		
		void descend(int x, int y, int sz, float r, float g, float b, float a) {
			if (sz < cut) {
				r += rng.nextFloat();
				g += rng.nextFloat();
				b += rng.nextFloat();
				a += rng.nextFloat();
			}
			if (sz == 1) {
				int i = y * size + x;
				bitmap[i] = toRGBA(r * coeff, g * coeff, b * coeff, a * coeff);
				return;
			}
			sz = sz >> 1;
			descend(x, y, sz, r, g, b, a);
			descend(x + sz, y, sz, r, g, b, a);
			descend(x + sz, y + sz, sz, r, g, b, a);
			descend(x, y + sz, sz, r, g, b, a);
		}
		
	}
	
}
