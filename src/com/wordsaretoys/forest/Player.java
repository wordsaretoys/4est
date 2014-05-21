package com.wordsaretoys.forest;

import com.wordsaretoys.rise.geometry.Camera;
import com.wordsaretoys.rise.geometry.Vector;
import com.wordsaretoys.rise.utility.Interval;

/**
 * player state and interaction
 */
public class Player {

	// player eye-line camera
	public Camera camera;
	
	// looking and moving state
	Vector looking = new Vector();
	boolean moving;

	// timer interval
	Interval interval = new Interval();
	
	// movement handling vectors
	Vector direction = new Vector();
	Vector velocity = new Vector();
	Vector normal = new Vector();
	
	/**
	 * ctor
	 */
	public Player() {
		camera = new Camera(30, 0.01f, 100);
	}
	
	public void setLook(float dx, float dy) {
		looking.set(-0.5f * dy, -0.5f * dx, 0);
	}
	
	public void setMove(boolean on) {
		moving = on;
	}

	/**
	 * updates player kinematic state
	 * call on every GL frame
	 */
	public void update() {
		
		float dt = interval.next();
		
		// rotate camera (with roll stabilization)
		camera.turn(0.25f * looking.x * dt, 0.25f * looking.y * dt, 10 * camera.right.y * dt);
		looking.mul(0.9f);
		
		// move camera if thrusting
		direction.copy(camera.front).mul(moving ? 1 : 0);
		
		// determine new velocity and position
		direction.mul(dt);
		velocity.add(direction).add(normal).mul(0.99f);
		camera.move(velocity.x * dt, velocity.y * dt, velocity.z * dt);
		
		Vector p = camera.position;
		Shared.dbg.set("pos", p.x, p.y, p.z, 100);
	}
	
}
