package com.wordsaretoys.rise.geometry;


/**
 * represent quaternion in 3-space plus standard operations
 * 
 * @author chris
 *
 */
public class Quaternion {
	
	public float x;
	public float y;
	public float z;
	public float w;
	
	/**
	 * create new quaternion
	 * @param x, y, z rotation axis
	 * @param w rotation around axis
	 */
	public Quaternion(float x, float y, float z, float w) {
		set(x, y, z, w);
	}
	
	public Quaternion() {
		set(0, 0, 0, 0);
	}
	
	/**
	 * set the components of the quaternion
	 * @param x, y, z rotation axis
	 * @param w rotation around axis
	 * @return self
	 */
	public Quaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	
	/**
	 * copy components from another quaternion
	 * @param q quaternion
	 * @return self
	 */
	public Quaternion copy(Quaternion q) { 
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
		return this;
	}
	
	/**
	 * multiply this quaternion by another
	 * @param q quaternion
	 * @return self
	 */
	public Quaternion mul(Quaternion q) {
		float tx = x;
		float ty = y;
		float tz = z;
		float tw = w;
		x = tw * q.x + tx * q.w + ty * q.z - tz * q.y;
		y = tw * q.y + ty * q.w + tz * q.x - tx * q.z;
		z = tw * q.z + tz * q.w + tx * q.y - ty * q.x;
		w = tw * q.w - tx * q.x - ty * q.y - tz * q.z;
		return this;
	}
	
	/**
	 * generate inverse of quaternion
	 * @return self
	 */
	public Quaternion neg() {
		return set(-x, -y, -z, w); 
	}
	
	/**
	 * normalize the quaternion
	 * @return self
	 */
	public Quaternion norm() {
		float mag = (float)Math.sqrt(x * x + y * y + z * z + w * w);
		return set(x / mag, y / mag, z / mag, w / mag);
	}
	
	/**
	 * generate matrix from quaternion
	 * @param m 4x4 matrix laid out in 16-element array
	 * @return filled array
	 */
	public float[] toMatrix(float[] m) {
		m[0] = (float)(1.0 - 2.0 * (y * y + z * z));
		m[1] = (float)(2.0 * (x * y + z * w));
		m[2] = (float)(2.0 * (x * z - y * w));
		m[3] = 0;
		m[4] = (float)(2.0 * (x * y - z * w));
		m[5] = (float)(1.0 - 2.0 * (x * x + z * z));
		m[6] = (float)(2.0 * (z * y + x * w));
		m[7] = 0;
		m[8] = (float)(2.0 * (x * z + y * w));
		m[9] = (float)(2.0 * (y * z - x * w));
		m[10] = (float)(1.0 - 2.0 * (x * x + y * y));
		m[11] = 0;
		m[12] = 0;
		m[13] = 0;
		m[14] = 0;
		m[15] = 1;
		return m;
	}
	
	/**
	 * generate matrix from quaternion
	 * @param m 3x3 matrix laid out in 9-element array
	 * @return filled array
	 */
	public float[] toMatrix3(float[] m) {
		m[0] = (float)(1.0 - 2.0 * (y * y + z * z));
		m[1] = (float)(2.0 * (x * y + z * w));
		m[2] = (float)(2.0 * (x * z - y * w));
		m[3] = (float)(2.0 * (x * y - z * w));
		m[4] = (float)(1.0 - 2.0 * (x * x + z * z));
		m[5] = (float)(2.0 * (z * y + x * w));
		m[6] = (float)(2.0 * (x * z + y * w));
		m[7] = (float)(2.0 * (y * z - x * w));
		m[8] = (float)(1.0 - 2.0 * (x * x + y * y));
		return m;
	}

	/**
	 * generate quaternion from axis-angle representation
	 * @param x, y, z rotation axis
	 * @param ang rotation angle (in radians)
	 * @return self
	 */
	public Quaternion setFromAxisAngle(float x, float y, float z, float ang) {
		float ha = (float) Math.sin(ang / 2.0);
		return set(x * ha, y * ha, z * ha, (float) Math.cos(ang / 2.0));
	}
	
	/**
	 * smooth interpolation between two quaternions
	 * @param a first quaternion
	 * @param b second quaternion
	 * @param m interpolation factor
	 * @return self
	 */
	public Quaternion slerp(Quaternion a, Quaternion b, float m) {
		x += m * (b.x - a.x);
		y += m * (b.y - a.y);
		z += m * (b.z - a.z);
		w += m * (b.w - a.w);
		return norm();
	}

}
