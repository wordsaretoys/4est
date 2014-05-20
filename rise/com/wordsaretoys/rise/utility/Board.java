package com.wordsaretoys.rise.utility;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.view.Surface;

import com.wordsaretoys.rise.glwrapper.Texture;

/**
 * billboard helper class, encapsulates surface/texture objects
 */
public class Board {

	public Texture texture;
	public SurfaceTexture surfaceTexture;
	public Surface surface;
	public Canvas canvas;
	
	public Board(int width, int height) {
		texture = new Texture();
		surfaceTexture = new SurfaceTexture(texture.getId());
		surfaceTexture.setDefaultBufferSize(width, height);
		surface = new Surface(surfaceTexture);
		
	}
	
	public Canvas lock() {
		canvas = surface.lockCanvas(null);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		return canvas;
	}
	
	public void unlock() {
		surface.unlockCanvasAndPost(canvas);
		surfaceTexture.updateTexImage();
	}
	
	public void release() {
		texture.release();
		surface.release();
		surfaceTexture.release();
	}
	
	public void draw(int bcolor, String title, float size) {
		lock();
		
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		canvas.drawColor(bcolor);

		Paint brush = new Paint();
		brush.setColor(0xffffffff);
		brush.setStyle(Style.STROKE);
		brush.setStrokeWidth(4f);
		
		canvas.drawRect(2, 2, w - 2, h - 2,	brush);

		brush.setStyle(Style.FILL_AND_STROKE);
		brush.setTextAlign(Paint.Align.CENTER);
		brush.setTypeface(Typeface.SANS_SERIF);
		brush.setTextSize(size);
		
		canvas.drawText(title, w >> 1, h >> 1, brush);
		
		unlock();
	}
	
}
