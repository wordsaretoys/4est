package com.wordsaretoys.forest;

import java.util.Random;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.wordsaretoys.rise.geometry.Vector;
import com.wordsaretoys.rise.glwrapper.Mesh;
import com.wordsaretoys.rise.glwrapper.Shader;
import com.wordsaretoys.rise.glwrapper.Texture;
import com.wordsaretoys.rise.meshutil.IndexBuffer;
import com.wordsaretoys.rise.meshutil.VertexBuffer;
import com.wordsaretoys.rise.utility.Asset;
import com.wordsaretoys.rise.utility.Misc;

public class Debris {

	static float Refresh = 8f;
	
	static float Range = 40f;

	VertexBuffer vertex;
	IndexBuffer index;
	Mesh mesh;
	
	Shader shader;

	Texture texture;
	int texId, rotorsId;
	
	Vector last = new Vector(Integer.MAX_VALUE, 0, 0);
	Vector ppos = Shared.player.camera.position;

	Random rng = new Random();
	
	/**
	 * ctor; creates GL objects
	 * must be called in GL context
	 */
	public Debris() {
		
		shader = new Shader();
		shader.build(
        	Asset.getTextAsset(Shared.context, "shader/debrisVert.glsl"),
        	Asset.getTextAsset(Shared.context, "shader/debrisFrag.glsl")
        );

		Bitmap noise = Asset.getBitmapAsset(Shared.context, "texture/noise.jpg");
		
		texture = new Texture(noise, noise, noise, noise, noise, noise);
		texId = shader.getUniformId("image");
		rotorsId = shader.getUniformId("rotations");

		int[] attr = {
				shader.getAttributeId("center"), 3,
				shader.getAttributeId("normal"), 3,
				shader.getAttributeId("rotor"), 1,
				shader.getAttributeId("position"), 3,
				shader.getAttributeId("texturec"), 3,
		};
		mesh = new Mesh(attr);
			
		vertex = new VertexBuffer();
		index = new IndexBuffer();
	}

	/**
	 * update mesh
	 * call in worker thread
	 */
	public void update() {
		// have we moved far enough to regen mesh?
		if (last.distance(ppos) < Refresh) {
			return;
		}
		last.copy(ppos);

		vertex.reset();
		index.reset();

		Shared.map.scanVolume(ppos.x, ppos.y, ppos.z, 20, new Map.Listener() {
			@Override
			public void onObject(long id, int what, float x, float y, float z, float r) {
				if (what == Map.Shard) {
					model(id, x, y, z, r);
				}
			}
		});
		
		Shared.glView.queueEvent(new Runnable() {
			@Override
			public void run() {
				mesh.build(vertex, index);
				Log.d("debris", "built " + vertex.length / mesh.stride + " vertices");
			}
		});
		
	}

	/**
	 * draw the mesh
	 */
	public void draw() {
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		shader.activate();
		shader.setMatrix("modelview", Shared.player.camera.modelview);
		shader.setMatrix("projector", Shared.player.camera.projector);
		GLES20.glUniformMatrix3fv(rotorsId, Rotors.RotorCount, false, Shared.rotors.matrixes, 0);
		
		texture.bind(0, texId);
		mesh.draw();
		GLES20.glDisable(GLES20.GL_CULL_FACE);
	}

	/**
	 * generates a single chunk of debris
	 */
	private void model(long seed, float cx, float cy, float cz, float mr) {
		rng.setSeed(seed);
		int k = vertex.length / mesh.stride;
		int d = rng.nextInt(Rotors.RotorCount); 
		int sides = (int)(6 + rng.nextInt(6));
		
		Vector n = new Vector();
		
		// top and bottom axis points
		vertex.set(cx, cy, cz, 0, 1, 0, d);
		vertex.set(0, mr, 0, 0, 1, 0);
		vertex.set(cx, cy, cz, 0, -1, 0, d);
		vertex.set(0, -mr, 0, 0, -1, 0);
		
		// go around the ring
		for (int i = 0, j = k + 2; i < sides; i++) {
			
			float ir = (float)i / (float)(sides - 1);
			float ang0 = (float)(2 * Math.PI * ir);
			float r, h;
			if (i == 0 || i == sides - 1) {
				r = mr * 0.5f;
				h = r;
			} else {
				r = mr * Misc.scale(rng.nextFloat(), 0.25f, 1);
				h = mr * Misc.scale(rng.nextFloat(), 0.25f, 1);
			}
			float x0 = (float)Math.cos(ang0);
			float z0 = (float)Math.sin(ang0);
			float x = r * x0;
			float z = r * z0;
			float tx = x / mr;
			float ty = h / mr;
			float tz = z / mr;

			// generate two vertexes to comprise top and bottom surfaces
			n.set(x0, 1, z0).norm();
			vertex.set(cx, cy, cz, n.x, n.y, n.z, d);
			vertex.set(x, h, z, tx, ty, tz);
			
			n.set(x0, -1, z0).norm();
			vertex.set(cx, cy, cz, n.x, n.y, n.z, d);
			vertex.set(x, -h, z, tx, -ty, tz);

			if (i > 0) {
				index.set(k, j + 2, j);
				index.set(k + 1, j + 1, j + 3);
				index.set(j, j + 2, j + 3);
				index.set(j, j + 3, j + 1);
				j += 2;
			}
			
		}
	}	
}
