package com.wordsaretoys.forest;

import java.util.Random;

import com.wordsaretoys.rise.utility.Misc;

/**
 * represents the debris map and the attributes of its components
 */

public class Map {

	public static final int Engine = 0;
	public static final int Shard = 1;

	public interface Listener {
		public void onObject(long id, int what, float x, float y, float z, float r);
	}
	
	static final float CellSize = 0.5f;
	
	static final float[] probabilityMap = { 0.009f, 0.05f };
	static final float[] maxRadius = { 0.05f, 0.2f };
	
	Random rng = new Random();
	
	/**
	 * scans the specified volume for objects
	 * calls Listener.onObject for each found
	 */
	public void scanVolume(float cx, float cy, float cz, float r, Listener l) {
		
		float x0 = (float)Math.floor((cx - r) / CellSize) * CellSize;
		float y0 = (float)Math.floor((cy - r) / CellSize) * CellSize;
		float z0 = (float)Math.floor((cz - r) / CellSize) * CellSize;
		float x1 = (float)Math.floor((cx + r) / CellSize) * CellSize;
		float y1 = (float)Math.floor((cy + r) / CellSize) * CellSize;
		float z1 = (float)Math.floor((cz + r) / CellSize) * CellSize;
		float hs = CellSize * 0.5f;
		
		if (y0 < -2) y0 = -2;
		if (y1 > 2) y1 = 2;
		
		for (float x = x0 + hs; x < x1; x += CellSize) {
			for (float y = y0 + hs; y < y1; y += CellSize) {
				for (float z = z0 + hs; z < z1; z += CellSize) {
					
					rng.setSeed(Misc.hash(x, y, z, 0));
					float select = rng.nextFloat();
					for (int i = 0; i < probabilityMap.length; i++) {
						if (select < probabilityMap[i]) {
							
							float mr = maxRadius[i];
							
							float ox = x + hs * Misc.scale(rng.nextFloat(), -1, 1);
							float oy = y + hs * Misc.scale(rng.nextFloat(), -1, 1);
							float oz = z + hs * Misc.scale(rng.nextFloat(), -1, 1);
							
							long id = Misc.hash(ox, oy, oz, i);
							
							l.onObject(id, i, ox, oy, oz, mr);
							
							break;
						}
					}
					
				}
			}
		}
		
	}
	
}
