package com.wordsaretoys.forest;

import java.util.Random;

import android.opengl.Matrix;

import com.wordsaretoys.rise.geometry.Vector;
import com.wordsaretoys.rise.utility.Interval;
import com.wordsaretoys.rise.utility.Misc;

/**
 * provides an array of random rotations for animations
 */

public class Rotors {

	public static final int RotorCount = 48;

	class AngularVelocity {
		float x;
		float y;
		float z;
		float w;
	}

	AngularVelocity[] angv;
	float[] rotations, matrixes;

	Interval interval;
	double time;
	
	public Rotors() {

		Random rng = new Random();
		
		rotations = new float[RotorCount * 16];
		for (int i = 0; i < RotorCount; i++) {
			Matrix.setIdentityM(rotations, i * 16);
		}
		
		angv = new AngularVelocity[RotorCount];
		Vector p = new Vector();
		for (int i = 0; i < RotorCount; i++) {
			p.set(	2 * rng.nextFloat() - 1, 
					2 * rng.nextFloat() - 1, 
					2 * rng.nextFloat() - 1 ).norm();
			angv[i] = new AngularVelocity();
			angv[i].x = p.x;
			angv[i].y = p.y;
			angv[i].z = p.z;
			angv[i].w = rng.nextFloat() * 100;
		}
		
		matrixes = new float[RotorCount * 9];

		interval = new Interval();
		time = 10 * Math.random();
	}
	
	public void update() {
		time += interval.next();
		for (int i = 0; i < RotorCount; i++) {
			AngularVelocity av = angv[i];
			Matrix.setRotateM(rotations, i * 16, (float)(av.w * time), av.x, av.y, av.z);
			Misc.copyM4To3(matrixes, i * 9, rotations, i * 16);
		}
	}
	
}
