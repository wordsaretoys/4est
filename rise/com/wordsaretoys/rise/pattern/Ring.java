package com.wordsaretoys.rise.pattern;

import java.util.Random;


/**
 * represent a group of pattern generators
 * that derive from a single linear buffer
 * 
 * provides fast lookups suitable for bitmaps
 */
public class Ring implements Pattern.F2F {

	float[] data;
	int modu;
	
	public Ring(int length) {
		data = new float[length];
		modu = length - 1;
	}
	
	public void generate(Random rng, int minP, int maxP, int minT, int maxT) {
		boolean inPeak = false;
		int count = 0;
		int total = 0;
		float peak0 = 0;
		float peak1 = 0;
		
		for (int i = 0; i < data.length; i++, count++) {
			// if we've reached the end of the current phase
			if (count == total) {
				// flip the phase
				inPeak = !inPeak;
				// derive new period & peak (if necessary)
				if (inPeak) {
					total = minP + rng.nextInt(maxP - minP);
				} else {
					total = minT + rng.nextInt(maxT - minT);
					peak0 = peak1;
					peak1 = rng.nextFloat();
				}
				count = 0;
			}
			// if we're in a peak
			if (inPeak) {
				// give me that peak
				data[i] = peak1;
			} else {
				// otherwise, give me the peak-peak transition
				float mu = (float) count / (float) total;
				float m2 = (float)(1 - Math.cos(mu * Math.PI)) * 0.5f;
				data[i] = (1 - m2) * peak0 + m2 * peak1;
			}
		}
	}

	@Override
	public float get(float x, float y) {
		float fx = data[ (int)(data.length * x) & modu ];
		float fy = data[ (int)(data.length * y) & modu ];
		return data[ (int)(data.length * (fx + fy)) & modu];
	}
}
