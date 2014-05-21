package com.wordsaretoys.forest;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.wordsaretoys.rise.utility.Needle;

/**
 * rendering object for gl surface
 * provides timer, gl & worker thread 
 */
public class Render implements GLSurfaceView.Renderer {

	// worker thread
	Worker worker;

	// model objects
	Debris debris;
	Skybox skybox;

	// ui/ai callback functor
	Runnable update;
	
	// frame timing
	long lastTime;
	int frames;
	
	/**
	 * ctor
	 */
	public Render() {
		worker = new Worker();
		
		update = new Runnable() {
			public void run() {
				Shared.glView.update();
			}
		};
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0.75f, 0.75f, 0.75f, 1);
		GLES20.glDisable(GLES20.GL_BLEND);
		
		// create anything that requires GL context
		skybox = new Skybox();
		// including objects need by others
		Shared.debris = debris = new Debris();
		
		worker.start();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Shared.player.camera.size(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		// update game state and player kinematics
		Shared.rotors.update();
		Shared.player.update();

		// update any android UI elements
		((Activity) Shared.context).runOnUiThread(update);

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		debris.draw();
		skybox.draw();
		
		// handle frame counting / timing
    	long t = System.nanoTime();
		frames++;
		if (t - lastTime >= 1e9) {
			Shared.dbg.set("fps", frames);
			frames = 0;
			lastTime = t;
		}
	}
	
	/**
	 * worker thread for background generation
	 */
	class Worker extends Needle {

		public Worker() {
			super("builder", 100);
		}

		@Override
		public void run() {
			resume();
			while (inPump()) {
				debris.update();
			}
		}
		
	}
}
