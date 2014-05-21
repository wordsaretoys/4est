package com.wordsaretoys.forest;

import android.content.Context;

import com.wordsaretoys.rise.utility.Dbg;
import com.wordsaretoys.forest.R;

/**
 * shared state between objects
 */
public class Shared {

	// player state
	static Player player;
	
	// game state
	static Game game;
	
	// debris map
	static Debris debris;
	
	// objects map
	static Map map;
	
	// rotational states
	static Rotors rotors;
	
	// objects with UI state; created on activity create
	static Context context;
	static Dbg dbg;
	static GlView glView;

	/**
	 * ctor, only call if shared object doesn't exist
	 */
	public Shared() {
		player = new Player();
		game = new Game();
		map = new Map();
		rotors = new Rotors();
	}
	
	/**
	 * call in activity create
	 */
	public void onCreate(Context context) {
		Shared.context = context;
		dbg = new Dbg(context, R.id.debugText);
	}
	
}
