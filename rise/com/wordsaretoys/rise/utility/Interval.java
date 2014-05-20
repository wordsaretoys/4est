package com.wordsaretoys.rise.utility;

/**
 * convenience class for generating time deltas 
 */
public class Interval {
	
	long time;

	public Interval() {
		time = System.nanoTime();
	}
	
	public float next() {
    	long t = System.nanoTime();
    	float dt = (float)(t - time) * 1e-9f;
		time = t;
    	// reject intervals that are too long
		return (dt < 0.5f) ? dt : 0;
	}
}
