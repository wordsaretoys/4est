package com.wordsaretoys.rise.geometry;


/**
 * represents a location plus orientation
 * may be used as a base class for game objects
 */

public class Mote {

	public Vector position;
	public Quaternion rotor;

	public Vector front;
	public Vector up;
	public Vector right;

	public float rotations[];
	public float transpose[];
	public float modelview[];
	
	private Quaternion qx;
	private Quaternion qy;
	private Quaternion qz;
	
	/**
	 * constructor, allocate and init fields
	 */
	public Mote() {
		position = new Vector();
		rotor = new Quaternion(0, 0, 0, 1);
		
		front = new Vector();
		up = new Vector();
		right = new Vector();
		
		rotations = new float[16];
		transpose = new float[16];
		modelview = new float[16];
		
		qx = new Quaternion();
		qy = new Quaternion();
		qz = new Quaternion();
		
		rotations[0] = rotations[5] = rotations[10] = rotations[15] = 1;
		transpose[0] = transpose[5] = transpose[10] = transpose[15] = 1;
		modelview[0] = modelview[5] = modelview[10] = modelview[15] = 1;
		
		turn(0, 0, 0);
		move(0, 0, 0);
	}
	
	/**
	 * turn mote on each axis by specified amounts
	 * @param x, y, z angles in radians
	 */
	public void turn(float x, float y, float z) {
		// rotate by specified amounts
		qx.setFromAxisAngle(1, 0, 0, x);
		qy.setFromAxisAngle(0, 1, 0, y);
		qz.setFromAxisAngle(0, 0, 1, z);
		rotor.copy(qx.mul(qy).mul(qz).mul(rotor).norm());

		// generate rotation matrix
		rotor.toMatrix(rotations);

		// generate orientation vectors
		// (front vector inverted for LH coordinate system)
		right.set(rotations[0], rotations[4], rotations[8]);
		up.set(rotations[1], rotations[5], rotations[9]);
		front.set(rotations[2], rotations[6], rotations[10]).neg();

		// generate transpose matrix
		transpose[0] = rotations[0];
		transpose[1] = rotations[4];
		transpose[2] = rotations[8];

		transpose[4] = rotations[1];
		transpose[5] = rotations[5];
		transpose[6] = rotations[9];

		transpose[8] = rotations[2];
		transpose[9] = rotations[6];
		transpose[10] = rotations[10];

		// copy to modelview w/ transformed position
		modelview[0] = rotations[0];
		modelview[1] = rotations[1];
		modelview[2] = rotations[2];

		modelview[4] = rotations[4];
		modelview[5] = rotations[5];
		modelview[6] = rotations[6];

		modelview[8] = rotations[8];
		modelview[9] = rotations[9];
		modelview[10] = rotations[10];
		
		updateModelview();
	}
	
	/**
	 * translate position by specified vector
	 */
	public void move(float x, float y, float z) {
		// translate by specified vector
		position.x += x;
		position.y += y;
		position.z += z;
		updateModelview();
	}
	
	/**
	 * updates modelview matrix with transformed position
	 */
	private void updateModelview() {
		float px = position.x;
		float py = position.y;
		float pz = position.z;
		modelview[12] = -(modelview[0] * px + modelview[4] * py + modelview[8] * pz);
		modelview[13] = -(modelview[1] * px + modelview[5] * py	+ modelview[9] * pz);
		modelview[14] = -(modelview[2] * px + modelview[6] * py + modelview[10] * pz);
	}
}
