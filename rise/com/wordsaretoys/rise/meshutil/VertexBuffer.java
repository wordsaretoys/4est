package com.wordsaretoys.rise.meshutil;

/**
 * growable floating-point buffer
 * 
 * @namespace rise
 * @class VertexBuffer
 */

public class VertexBuffer {

	public float[] data;
	public int length;
	
	final static private float LN2 = (float)Math.log(2);
	private int limit;
	
	public VertexBuffer() {
		data = new float[16];
		length = 0;
	}
	
	/**
	 * grow the vertex buffer if necessary
	 * @param n number of floats to grow by
	 */
	private void grow(int n) {
		int newSize = length + n;
		if (newSize > limit) {
			// find smallest power of 2 greater than newSize
			limit = (int) Math.pow(2, Math.ceil(Math.log(newSize) / LN2));
			float[] newBuffer = new float[limit];
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
	 * add values to the float buffer
	 * @param x0...xn values to add
	 */
	
	public void set(float x0) {
		grow(1);
		data[length++] = x0;
	}

	public void set(float x0, float x1) {
		grow(2);
		data[length++] = x0;
		data[length++] = x1;
	}
	
	public void set(float x0, float x1, float x2) {
		grow(3);
		data[length++] = x0;
		data[length++] = x1;
		data[length++] = x2;
	}

	public void set(float x0, float x1, float x2, float x3) {
		grow(4);
		data[length++] = x0;
		data[length++] = x1;
		data[length++] = x2;
		data[length++] = x3;
	}
	
	public void set(float x0, float x1, float x2, float x3, float x4) {
		grow(5);
		data[length++] = x0;
		data[length++] = x1;
		data[length++] = x2;
		data[length++] = x3;
		data[length++] = x4;
	}

	public void set(float x0, float x1, float x2, float x3, float x4, float x5) {
		grow(6);
		data[length++] = x0;
		data[length++] = x1;
		data[length++] = x2;
		data[length++] = x3;
		data[length++] = x4;
		data[length++] = x5;
	}

	public void set(float x0, float x1, float x2, float x3, float x4, float x5, float x6) {
		grow(7);
		data[length++] = x0;
		data[length++] = x1;
		data[length++] = x2;
		data[length++] = x3;
		data[length++] = x4;
		data[length++] = x5;
		data[length++] = x6;
	}
}
