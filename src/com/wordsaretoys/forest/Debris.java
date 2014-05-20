package com.wordsaretoys.forest;

import java.util.HashMap;
import java.util.Random;

import android.opengl.GLES20;

import com.wordsaretoys.forest.Hash3D.Cell;
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
	static float Step = 10f;
	static float Odds = 0.25f;

	VertexBuffer vertex;
	IndexBuffer index;
	Mesh mesh;
	
	Hash3D map;
	HashMap<Long, Object> complete;

	Shader shader;

	Texture texture;
	int texId;
	
	int fadeLightId, fadeDistanceId;

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

		texture = new Texture(
				Asset.getImageAsset(Shared.context, "texture/debris-nx.jpg"),
				Asset.getImageAsset(Shared.context, "texture/debris-ny.jpg"),
				Asset.getImageAsset(Shared.context, "texture/debris-nz.jpg"),
				Asset.getImageAsset(Shared.context, "texture/debris-px.jpg"),
				Asset.getImageAsset(Shared.context, "texture/debris-py.jpg"),
				Asset.getImageAsset(Shared.context, "texture/debris-pz.jpg"),
				512, 512);
		texId = shader.getUniformId("image");
		
		fadeLightId = shader.getUniformId("fadeLight");
		fadeDistanceId = shader.getUniformId("fadeDistance");
		
		map = new Hash3D(16384);
		complete = new HashMap<Long, Object>();

		int[] attr = {
				shader.getAttributeId("center"), 3,
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
			
		float cx = (float) Math.floor(ppos.x / Step) * Step;
		float cy = (float) Math.floor(ppos.y / Step) * Step;
		float cz = (float) Math.floor(ppos.z / Step) * Step;

		// iterate over the visible range in blocks
		for (float dx = -Range + Step / 2; dx < Range; dx += Step) {
			float x = cx + dx;
			for (float dy = -Range + Step / 2; dy < Range; dy += Step) {
				float y = cy + dy;
				for (float dz = -Range + Step / 2; dz < Range; dz += Step) {
					float z = cz + dz;
					Cell cell = map.get(x, y, z);
					if (cell.odds < Odds) {
						model(cell, x, y, z);
					}
				}
			}
		}
		
		Shared.glView.queueEvent(new Runnable() {
			public void run() {
				mesh.build(vertex, index);
			}
		});
		
	}

	/**
	 * find the closest debris object, if any
	 * @param p position to search for
	 * @return object coordinates or null if no object in block
	 */
	public Vector locate(Vector p) {
		float cx = (float) Math.floor(p.x / Step) * Step + Step / 2;
		float cy = (float) Math.floor(p.y / Step) * Step + Step / 2;
		float cz = (float) Math.floor(p.z / Step) * Step + Step / 2;
		Cell cell = map.get(cx, cy, cz);
		if (cell.odds < Odds) {
			t_p.set(cx, cy, cz);
			long id = Misc.hash(cx, cy, cz, 0);
			return !complete.containsKey(id) ? t_p : null;
		}
		return null;
	}
	Vector t_p = new Vector();
	
	/**
	 * draw the mesh
	 */
	public void draw() {
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		shader.activate();
		shader.setMatrix("modelview", Shared.player.camera.modelview);
		shader.setMatrix("projector", Shared.player.camera.projector);
		
		float t = Shared.game.getNormalTime();
		GLES20.glUniform1f(fadeLightId, 0.75f * t * t * t);
		GLES20.glUniform1f(fadeDistanceId, 40 * t);

		texture.bind(0, texId);
		mesh.draw();
		GLES20.glDisable(GLES20.GL_CULL_FACE);
	}

	/**
	 * generates a single chunk of debris
	 */
	private void model(Cell cell, float x, float y, float z) {
		rng.setSeed(cell.seed);
		int k = vertex.length / mesh.stride;
		int sides = (int)(6 + rng.nextInt(6));
		float mr = rng.nextFloat();
		
		// top and bottom axis points
		vertex.set(x, y, z);
		vertex.set(0, mr, 0, 0, 1, 0);
		vertex.set(x, y, z);
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
			float x0 = (float) Math.cos(ang0);
			float z0 = (float) Math.sin(ang0);
			float px = r * x0;
			float pz = r * z0;
			float tx = px / mr;
			float ty = h / mr;
			float tz = pz / mr;

			// generate two vertexes to comprise top and bottom surfaces
			vertex.set(x, y, z);
			vertex.set(px, h, pz, tx, ty, tz);
			
			vertex.set(x, y, z);
			vertex.set(px, -h, pz, tx, -ty, tz);

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
