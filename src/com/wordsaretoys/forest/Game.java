package com.wordsaretoys.forest;

import com.wordsaretoys.rise.utility.Interval;

/**
 * manages game state
 */
public class Game {

	static float TimeoutPeriod = 100;
	
	// darkness timeout
	float timeout;
	
	// interval
	Interval interval = new Interval();
	
	/**
	 * ctor, set intial state
	 */
	public Game() {
		reset();
	}
	
	/**
	 * reset state after level completion
	 */
	public void reset() {
		timeout = TimeoutPeriod;
	}
	
	/**
	 * update state
	 */
	public void update() {
		timeout -= interval.next();
		if (timeout < 0) {
			reset();
		}
	}

	/**
	 * get normalized timeout value
	 * @return 0..1 timeout
	 */
	public float getNormalTime() {
		return timeout / TimeoutPeriod;
	}
}
