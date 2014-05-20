package com.wordsaretoys.rise.geometry;


/**
 * 
 * represents a vector in 3-space plus standard operations
 * 
 * @author chris
 *
 */
public class Vector {
	
	public float x;
	public float y;
	public float z;

	/**
	 * constructor, set initial values
	 * @param x, y, z initial values
	 */
	public Vector(float x, float y, float z) {
		set(x,  y,  z);
	}
	
	public Vector() {
		set(0, 0, 0);
	}
	
	/**
	 * set vector components
	 * @param x, y, z components of the vector
	 * @return self
	 */
	public Vector set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * copy components of another vector
	 * @param v, vector to copy from
	 * @return self
	 */
	public Vector copy(Vector v) { 
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}

	/**
	 * add another vector to this one
	 * @param v, vector to add
	 * @return self
	 */
	public Vector add(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}
	
	/**
	 * subtract another vector from this one
	 * @param v, vector to subtract
	 * @return self
	 */
	public Vector sub(Vector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}
	
	/**
	 * multiply this vector by a constant
	 * @param c, constant to multiply
	 * @return self
	 */
	public Vector mul(float c) {
		x *= c;
		y *= c;
		z *= c;
		return this;
	}
	
	/**
	 * divide this vector by a constant
	 * return zero-length vector if constant equals zero
	 * @param c, constant to divide by
	 * @return self
	 */
	public Vector div(float c) {
		if (c != 0) {
			x /= c;
			y /= c;
			z /= c;
		} else {
			set(0, 0, 0);
		}
		return this;
	}
	
	/**
	 * generate additive inverse of the vector
	 * @return self
	 */
	public Vector neg() {
		return set(-this.x, -this.y, -this.z); 
	}
	
	/**
	 * return length of the vector
	 * @return length
	 */
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	/**
	 * return distance between this vector and another
	 * @param v vector
	 * @return distance
	 */
	public float distance(Vector v) {
		return (float) Math.sqrt(distsq(v));
	}
	
	/**
	 * return square of distance between this vector and another
	 * @param v vector
	 * @return distance squared
	 */
	public float distsq(Vector v) {
		float dx = x - v.x;
		float dy = y - v.y;
		float dz = z - v.z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	/**
	 * normalize this vector
	 * @return self
	 */
	public Vector norm() {
		float l = length();
		return div(l);
	}
	
	/**
	 * return dot product between this vector and another
	 * @param v vector
	 * @return dot product
	 */
	public float dot(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	/**
	 * cross this vector with another
	 * @param v vector
	 * @return self
	 */
	public Vector cross(Vector v) {
		float tx = x;
		float ty = y;
		float tz = z;
		x = ty * v.z - tz * v.y;
		y = tz * v.x - tx * v.z;
		z = tx * v.y - ty * v.x;
		return this;
	}
	
	/**
	 * copy vector data to float array
	 * @param a float array (size >= 3)
	 * @return filled array [x, y, z]
	 */
	public float[] toArray(float[] a) {
		a[0] = x;
		a[1] = y;
		a[2] = z;
		return a;
	}
	
	/**
	 * round the vector components to the nearest n
	 * @param n number to round by
	 * @return self
	 */
	public Vector nearest(float n) {
		x = (float) Math.round(x / n) * n;
		y = (float) Math.round(y / n) * n;
		z = (float) Math.round(z / n) * n;
		return this;
	}
	
	/**
	 * transform vector by matrix multiplication
	 * @param m 4x4 matrix laid out in 16-element array
	 * @return self
	 */
	public Vector transform(float[] m) {
		float mx = m[0] * x + m[4] * y + m[8] * z + m[12];
		float my = m[1] * x + m[5] * y + m[9] * z + m[13];
		float mz = m[2] * x + m[6] * y + m[10] * z + m[14];
		float d = m[3] * x + m[7] * y + m[11] * z + m[15];
		return this.set(mx / d, my / d, mz / d);
	}
	
	/**
	 * generate a guaranteed perpendicular vector
	 * for length > 0
	 * @return self
	 */
	public Vector perp() {
		float swp;
		if (x != y) {
			swp = x;
			x = y;
			y = swp;
		} else if (x != z) {
			swp = x;
			x = z;
			z = swp;
		} else {
			swp = y;
			y = z;
			z = swp;
		}
		if (x != 0) {
			x = -x;
		} else if (y != 0) {
			y = -y;
		} else {
			z = -z;
		}
		return this;
	}
	
}
