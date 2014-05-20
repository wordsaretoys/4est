package com.wordsaretoys.rise.geometry;

import java.util.Arrays;

/**
 * provides a simple orthographic projection
 */
public class Ortho {

	public float[] projector;
	
	public Ortho() {
		projector = new float[16];
		projector[0] = projector[5] = projector[10] = projector[15] = 1f;
	}
	
	public void size(float width, float height, float depth) {
		Arrays.fill(projector, 0f);
		float left = -width / 2f;
		float right = width / 2f;
		float top = -height / 2f;
		float bottom = height / 2f;
		float far = -depth / 2f;
		float near = depth / 2f;
		projector[0] = 2f / (right - left);
		projector[3] = -(right + left) / (right - left);
		projector[5] = 2f / (top - bottom);
		projector[7] = -(top + bottom) / (top - bottom);
		projector[10] = -2f / (far - near);
		projector[11] = -(far + near) / (far - near);
		projector[15] = 1f;
	}
	
}
