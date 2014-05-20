package com.wordsaretoys.rise.meshutil;

/**
 * growable short int buffer
 * 
 * @namespace rise
 * @class IndexBuffer
 */

public class IndexBuffer {

	public short[] data;
	public int length;
	
	final static private double LN2 = Math.log(2);
	private int limit;
	
	public IndexBuffer() {
		data = new short[16];
		length = 0;
	}
	
	/**
	 * grow the vertex buffer if necessary
	 * @param n number of shorts to grow by
	 */
	private void grow(int n) {
		int newSize = length + n;
		if (newSize > limit) {
			// find smallest power of 2 greater than newSize
			limit = (int) Math.pow(2, Math.ceil(Math.log(newSize) / LN2));
			short[] newBuffer = new short[limit];
			System.arraycopy(data, 0, newBuffer, 0, length);
			data = newBuffer;
		}
	}
	
	/**
	 * reset the mesh for use with a new data set
	 */
	public void reset() {
		length = 0;
	}
	
	/**
	 * add values to the buffer
	 * @param i0...in values to add
	 */
	
	public void set(int i0) {
		grow(1);
		data[length++] = (short)i0;
	}
	
	public void set(int i0, int i1, int i2) {
		grow(3);
		data[length++] = (short)i0;
		data[length++] = (short)i1;
		data[length++] = (short)i2;
	}

	public void set(int i0, int i1, int i2, int i3, int i4, int i5) {
		grow(6);
		data[length++] = (short)i0;
		data[length++] = (short)i1;
		data[length++] = (short)i2;
		data[length++] = (short)i3;
		data[length++] = (short)i4;
		data[length++] = (short)i5;
	}
}
