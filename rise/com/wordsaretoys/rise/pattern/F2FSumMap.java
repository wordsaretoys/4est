package com.wordsaretoys.rise.pattern;

import com.wordsaretoys.rise.pattern.I2FMap;
import com.wordsaretoys.rise.pattern.Pattern;

/**
 * map of summed I2F functions
 */
public class F2FSumMap implements Pattern.F2F {

	I2FMap[] srcs;
	float [] freq;
	float [] ampl;
	
	public F2FSumMap(int length, float[] freq, float[] ampl) {
		assert(freq.length == ampl.length);
		srcs = new I2FMap[freq.length];
		for (int i = 0; i < srcs.length; i++) {
			srcs[i] = new I2FMap(length);
			srcs[i].generate();
		}
		this.freq = freq;
		this.ampl = ampl;
	}
	
	@Override
	public float get(float x, float y) {
		float p = 0;
		for (int i = 0; i < srcs.length; i++) {
			p += ampl[i] * Pattern.ipolate(srcs[i], x * freq[i], y * freq[i]);			
		}
		return p;
	}

}
