package com.wordsaretoys.forest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.wordsaretoys.rise.geometry.Camera;
import com.wordsaretoys.rise.geometry.Vector;

public class GlView extends GLSurfaceView {

	// gesture detector & listener
	GestureDetector gestureDetector;
	GestureListener gestureListener;
	
	/**
	 * xml-compatible ctor
	 */
	public GlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Shared.glView = this;
		setEGLContextClientVersion(2);
		setRenderer(new Render());
		
		gestureListener = new GestureListener();
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false);
	}
	
	/**
	 * periodic update
	 */
	public void update() {
		String target = "nothing";
		Camera camera = Shared.player.camera;
		Vector p = Shared.debris.locate(camera.position);
		if (p != null) {
			t_p.copy(p).sub(camera.position);
			float dd = t_p.length();
			t_p.norm();
			float dp = t_p.dot(camera.front);
			target = dp + ", " + dd;
		}
		Shared.dbg.set("target", target);
	}
	Vector t_p = new Vector();

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// handle raw events for movement
		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			Shared.player.setMove(true);
			break;
		case MotionEvent.ACTION_UP:
			Shared.player.setMove(false);
			break;
		}
		return gestureDetector.onTouchEvent(e) || super.onTouchEvent(e);
	}

	/**
	 * gesture detector listener
	 */
	class GestureListener extends SimpleOnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent e) {
			// must return true for other events to happen
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e0, MotionEvent e1, float dx, float dy) {
			Shared.player.setLook(dx, dy);
			return true;
		}
		
	}
	
}
