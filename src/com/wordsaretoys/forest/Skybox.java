package com.wordsaretoys.forest;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;

import com.wordsaretoys.rise.glwrapper.Mesh;
import com.wordsaretoys.rise.glwrapper.Shader;
import com.wordsaretoys.rise.glwrapper.Texture;
import com.wordsaretoys.rise.meshutil.IndexBuffer;
import com.wordsaretoys.rise.meshutil.VertexBuffer;
import com.wordsaretoys.rise.utility.Asset;

/**
 * generates and displays the skybox
 */
public class Skybox {

	static float Radius = 50;
	
	Shader shader;

	Texture texture;
	int texId;

	Mesh mesh;
	
	Random rng = new Random();
	
	/**
	 * ctor; creates GL objects
	 * must be called in GL context
	 */
	public Skybox() {
		
		shader = new Shader();
		shader.build(
        	Asset.getTextAsset(Shared.context, "shader/skyboxVert.glsl"),
        	Asset.getTextAsset(Shared.context, "shader/skyboxFrag.glsl")
        );

		Bitmap wrap = makeMain();
		Bitmap end0 = makeEnds();
		Bitmap end1 = makeEnds();
		
		texture = new Texture(
				Bitmap.createBitmap(wrap, 0, 0, 512, 512),
				end0,
				Bitmap.createBitmap(wrap, 1536, 0, 512, 512),
				Bitmap.createBitmap(wrap, 1024, 0, 512, 512),
				end1,
				Bitmap.createBitmap(wrap, 512, 0, 512, 512));
		texId = shader.getUniformId("image");
		
		int[] attr = {
				shader.getAttributeId("position"), 3,
				shader.getAttributeId("texturec"), 3,
		};
		mesh = new Mesh(attr);
		
		VertexBuffer vertex = new VertexBuffer();
		IndexBuffer index = new IndexBuffer();

		vertex.set(-Radius, -Radius, -Radius, -1, -1, -1);
		vertex.set(-Radius, Radius, -Radius, -1, 1, -1);
		vertex.set(Radius, -Radius, -Radius, 1, -1, -1);
		vertex.set(Radius, Radius, -Radius, 1, 1, -1);
		vertex.set(Radius, -Radius, Radius, 1, -1, 1);
		vertex.set(Radius, Radius, Radius, 1, 1, 1);
		vertex.set(-Radius, -Radius, Radius, -1, -1, 1);
		vertex.set(-Radius, Radius, Radius, -1, 1, 1);
		
		index.set(0, 3, 1, 0, 2, 3);
		index.set(2, 5, 3, 2, 4, 5);
		index.set(4, 7, 5, 4, 6, 7);
		index.set(6, 1, 7, 6, 0, 1);
		index.set(3, 5, 7, 3, 7, 1);
		index.set(2, 6, 4, 2, 0, 6);
		
		mesh.build(vertex, index);
	}

	/**
	 * draw the mesh
	 */
	public void draw() {
		shader.activate();
		shader.setMatrix("rotations", Shared.player.camera.rotations);
		shader.setMatrix("projector", Shared.player.camera.projector);
		
		texture.bind(0, texId);
		mesh.draw();
	}

	Bitmap makeMain() {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.FILL);
		
		Bitmap b = Bitmap.createBitmap(2048, 512, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(b);
		
		for (int i = 0; i < 4000; i++) {
			canvas.drawPoint(rng.nextInt(2048), rng.nextInt(512), p);
		}
		
		int[] rgc = { 0xffffffcc, 0xffeeeebb, 0 };
		float[] rgp = {0, 0.5f, 1 };
		RadialGradient rg = new RadialGradient(256, 256, 256, rgc, rgp, TileMode.CLAMP);
		p.setShader(rg);
		canvas.drawCircle(256, 256, 256, p);
		
		p.setShader(null);
		p.setColor(0x3fffffff);
		for (int i = 0; i < 8192; i++) {
			float cx = rng.nextInt(2048);
			float cy = 256 + rng.nextInt(64) - 32;
			float r = rng.nextFloat() * 8;
			int k = rng.nextInt(256);
			p.setColor(0x3f000000 | k | (k >> 8) | (k >> 16));
			canvas.drawCircle(cx, cy, r, p);
		}
		
		return b;
	}
	
	Bitmap makeEnds() {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.FILL);
		
		Bitmap b = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(b);
		for (int i = 0; i < 1000; i++) {
			canvas.drawPoint(rng.nextInt(512), rng.nextInt(512), p);
		}
		return b;
	}
}
