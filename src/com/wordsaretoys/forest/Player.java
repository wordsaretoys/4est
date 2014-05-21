package com.wordsaretoys.forest;

import com.wordsaretoys.rise.geometry.Camera;
import com.wordsaretoys.rise.geometry.Vector;
import com.wordsaretoys.rise.utility.Interval;

/**
 * player state and interaction
 */
public class Player {

	static final float CollideAt = 0.05f;
	static final float TargetAt = -0.9f;
	
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
	
	// map listener
	Map.Listener mapper;
	
	// target object id
	long target;
	
	/**
	 * ctor
	 */
	public Player() {
		camera = new Camera(30, 0.01f, 100);
		mapper = new Map.Listener() {
			Vector op = new Vector();
			@Override
			public void onObject(long id, int what, float x, float y, float z, float r) {
				op.set(x, y, z).sub(camera.position);
				float d = op.length() - r;
				op.norm().neg();
				if (d <= CollideAt) {
					normal.add(op);
				}
				float p = op.dot(camera.front);
				if (p < TargetAt) {
					target = id;
				}
			}
		};
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

		// any collisions or targets in the near distance?
		Vector cp = camera.position;
		target = 0;
		normal.set(0, 0, 0);
		Shared.map.scanVolume(cp.x, cp.y, cp.z, 1, mapper);
		normal.mul(velocity.length());
		Shared.dbg.set("target", target == 0 ? "none" : Long.toString(target));
		
		// determine new velocity and position
		direction.mul(dt);
		velocity.add(direction).add(normal).mul(0.99f);
		camera.move(velocity.x * dt, velocity.y * dt, velocity.z * dt);
		
		Vector p = camera.position;
		Shared.dbg.set("pos", p.x, p.y, p.z, 100);
	}
	
}
