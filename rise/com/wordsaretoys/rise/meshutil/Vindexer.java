package com.wordsaretoys.rise.meshutil;

import android.util.Log;

/**
 * Index generator for 3D vertexes
 * 
 * implements an octree in a fixed-size array;
 * as the mesh object only allows short ints
 * to be used for indexes, we don't bother to
 * make the array length > 64k
 * 
 * @author chris
 *
 */

public class Vindexer {

	static public final int INDEX_MASK = 0xFFFF;
	static public final int ADD_VERTEX= 0xFFFF0000;
	
	static private final int MAX_INDEX = 65536;
	static private final float MIN_LIMIT = 0.0001f;

	private float[] vertex;
	private int[] child;

	private int length;
	
	private int hits;
	
	/**
	 * constructor, generates fixed array
	 */
	public Vindexer() {
		vertex = new float[3 * MAX_INDEX];
		child = new int[8 * MAX_INDEX];
		reset();
	}
	
	/**
	 * call prior to generating new indexes
	 */
	public void reset() {
		length = 0;
		hits = 0;
	}
	
	/**
	 * check that a specific vertex is in the octree,
	 * create it if it isn't. either way, return the
	 * index of the vertex.
	 * 
	 * if the indexer hasn't seen this vertex yet, I
	 * assume the calling code needs to add it to a
	 * vertex buffer, so the ADD_VERTEX flag is ORed
	 * with the index. typically, you'd do something
	 * like this:
	 * 
	 * 		int result = vindexer.check(x, y, z);
	 * 		int index = result & Vindexer.ADD_VERTEX
	 * 		if (index != result) {
	 * 			// add vertex to buffer
	 * 			vertexbuffer.set(x, y, z);
	 * 		}
	 * 		indexbuffer.set(index);
	 * 
	 * 
	 * @param x, y, z coordinates of vertex
	 * @return index of new or stored vertex, maybe ORed with 
	 * ADD_VERTEX if new vertex (see above)
	 */
	public int check(float x, float y, float z) {
		int vin, cin, index = 0;
		
		while (index < length) {
			
			vin = index * 3;
			cin = index * 8;
			
			float vx = vertex[vin];
			float vy = vertex[vin + 1];
			float vz = vertex[vin + 2];
			
			// if this entry matches our vertex
			if ( (Math.abs(x - vx) < MIN_LIMIT) && (Math.abs(y - vy) < MIN_LIMIT) && (Math.abs(z - vz) < MIN_LIMIT)) {
				// our search is over
				hits++;
				return index;
			}
			
			// traverse to the next child, if available
			int ch = cin + ((x > vx) ? 4 : 0) + ((y > vy) ? 2 : 0) + ((z > vz) ? 1 : 0);
			if (child[ch] != -1) {
				index = child[ch];
			} else {
				child[ch] = (int)length;
				index = length;
			}
			
		}

		// no entries found, create one
		vin = length * 3;
		cin = length * 8;

		vertex[vin] = x;
		vertex[vin + 1] = y;
		vertex[vin + 2] = z;

		for (int i = 0; i < 8; i++) {
			child[cin + i] = -1;
		}
		
		index = length++;
		
		// this should never happen, obviously
		if (length >= MAX_INDEX) {
			throw new RuntimeException("Vindexer exceeded index limits");
		}
		
		// return index with "add vertex" flag in upper word
		return index | ADD_VERTEX;
	}
	
	public void report() {
		int hpi = hits / length;
		Log.i("4est", "vindexer generated " + length + " indexes, with " + hits + " hits (" + hpi + " hits/index)");
	}
	
}