package com.wordsaretoys.rise.geometry;

import android.opengl.GLES20;

/**
 * maintain a camera: a special case of mote
 * that implements a projection matrix
 * 
 * @author chris
 *
 */
public class Camera extends Mote {

	public float viewFactor;
	public float[] projector;
	
	/**
	 * constructor
	 */
	public Camera(float viewAngle, float nearLimit, float farLimit) {
		viewFactor = 1f / (float) Math.tan(viewAngle * Math.PI / 180f);
		// set fixed matrix elements
		projector = new float[16];
		float d = (float)(nearLimit - farLimit);
		projector[0] = (float)viewFactor;
		projector[5] = (float)viewFactor;
		projector[10] = (float) ((farLimit + nearLimit) / d);
		projector[11] = -1;
		projector[14] = (float)(2 * nearLimit * farLimit / d);
	}
	
	/**
	 * set camera aspect ratio and viewport
	 * @param width, height dimensions of viewport
	 */
	public void size(int width, int height) {
		float aspectRatio = (float)width / (float)height;
		projector[0] = (float)viewFactor / aspectRatio;
		GLES20.glViewport(0, 0, width, height);
	}
	
}
