package com.wordsaretoys.rise.geometry;

import java.util.Arrays;

/**
 * miscellaneous operations
 */
public class Geom {

	/**
	 * load orientation vectors into a 4x4 matrix
	 * all vectors should be normalized!
	 */
	public static void loadMatrix(float[] m, Vector front, Vector up, Vector right) {
		Arrays.fill(m, 0);
		m[0] = right.x;
		m[1] = right.y;
		m[2] = right.z;
		m[4] = up.x;
		m[5] = up.y;
		m[6] = up.z;
		m[8] = front.x;
		m[9] = front.y;
		m[10] = front.z;
		m[15] = 1;
	}

	/**
	 * copies rotation components from 4x4 matrix to 3x3 matrix
	 */
	public static void copyM4To3(float[] m3, float[] m4) {
		int i3 = 0, i4 = 0;
		
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
	
	/**
	 * tests intersection between two squares
	 */
	public static boolean squaresIntersect(float x0, float y0, float l0, float x1, float y1, float l1) {
		return 	(x0 < (x1 + l1) && (x0 + l0) > x1) && 
				(y0 < (y1 - l1) && (y0 - l0) > y1);
	}
	
	/**
	 * returns true if point in square
	 * note that sx, sy is the CORNER point
	 */
	public static boolean pointInSquare(float x, float y, float sx, float sy, float sl) {
		return (x >= sx) && (x <= (sx + sl)) && (y >= sy) && (sy <= (sy + sl));
	}
}
