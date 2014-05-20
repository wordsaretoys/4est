package com.wordsaretoys.forest;

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

/**
 * generates and displays the weed field
 */
public class Weeds {

	static float Refresh = 8f;
	
	static float Range = 40f;
	static float Step = 8f;

	class Group {
		VertexBuffer vertex;
		IndexBuffer index;
		Mesh mesh;
	}
	
	Hash3D map;

	Shader shader;

	Texture texture;
	int texId;
	
	int fadeLightId, fadeDistanceId;

	Group[] groups;
	
	Vector last = new Vector(Integer.MAX_VALUE, 0, 0);
	Vector ppos = Shared.player.camera.position;

	Random rng = new Random();
	
	/**
	 * ctor; creates GL objects
	 * must be called in GL context
	 */
	public Weeds() {
		
		shader = new Shader();
		shader.build(
        	Asset.getTextAsset(Shared.context, "shader/weedVert.glsl"),
        	Asset.getTextAsset(Shared.context, "shader/weedFrag.glsl")
        );

		texture = new Texture(Asset.getBitmapAsset(Shared.context, "texture/grass.jpg"));
		texId = shader.getUniformId("weed");
		
		fadeLightId = shader.getUniformId("fadeLight");
		fadeDistanceId = shader.getUniformId("fadeDistance");
		
		map = new Hash3D(4096);

		int[] attr = {
				shader.getAttributeId("position"), 3,
				shader.getAttributeId("texturec"), 2,
		};
			
		groups = new Group[4];
		for (int i = 0; i < groups.length; i++) {
			Group group = new Group();
			group.vertex = new VertexBuffer();
			group.index = new IndexBuffer();
			group.mesh = new Mesh(attr);
			groups[i] = group;
		}
			
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

		for (int i = 0; i < groups.length; i++) {
			groups[i].vertex.reset();
			groups[i].index.reset();
		}
			
		float cx = (float) Math.floor(ppos.x / Step) * Step;
		float cy = (float) Math.floor(ppos.y / Step) * Step;
		float cz = (float) Math.floor(ppos.z / Step) * Step;

		// iterate over the visible range in blocks
		for (float dx = -Range; dx <= Range; dx += Step) {
			float x = cx + dx;
			for (float dy = -Range; dy <= Range; dy += Step) {
				float y = cy + dy;
				for (float dz = -Range; dz <= Range; dz += Step) {
					float z = cz + dz;
					Cell cell = map.get(x, y, z);
					// generate the strands
					for (int i = 0; i < groups.length; i++) {
						model(groups[i], cell, x, y, z);
					}
				}
			}
		}
		
		Shared.glView.queueEvent(new Runnable() {
			public void run() {
				for (int i = 0; i < groups.length; i++) {
					groups[i].mesh.build(groups[i].vertex, groups[i].index);
				}
			}
		});
		
	}
	
	/**
	 * draw the mesh
	 */
	public void draw() {
		shader.activate();
		shader.setMatrix("modelview", Shared.player.camera.modelview);
		shader.setMatrix("projector", Shared.player.camera.projector);
		
		float t = Shared.game.getNormalTime();
		GLES20.glUniform1f(fadeLightId, 0.75f * t * t * t);
		GLES20.glUniform1f(fadeDistanceId, 40 * t);

		texture.bind(0, texId);
		for (int i = 0; i < groups.length; i++) {
			groups[i].mesh.draw();
		}
	}

	/**
	 * generates a single weed strand around specified point
	 */
	private void model(Group group, Cell cell, float x, float y, float z) {

		// TODO: smooth curves
		int k = group.vertex.length / group.mesh.stride;
		rng.setSeed(cell.seed + group.hashCode());
		
		w_p.set(x, y, z);
		w_f.set(0, 0, 0);
		w_r.set(-1, 0, 0);
		
		for (int i = 0; i < 20; i++) {

			group.vertex.set(
					w_p.x - 0.1f * w_r.x, 
					w_p.y - 0.1f * w_r.y, 
					w_p.z - 0.1f * w_r.z,
					i & 1, 0);
			group.vertex.set(
					w_p.x + 0.1f * w_r.x, 
					w_p.y + 0.1f * w_r.y, 
					w_p.z + 0.1f * w_r.z,
					i & 1, 0.1f);

			if (i > 0) {
				group.index.set(k, k + 2, k + 3, k, k + 3, k + 1);
				k += 2;
			}
			if (k >= 65500) {
				return;
			}
			
			w_f.x += 2 * rng.nextFloat() - 1;
			w_f.y += 2 * rng.nextFloat() - 1;
			w_f.z += 2 * rng.nextFloat() - 1;
			w_f.norm();

			w_p.add(w_f);
			
			w_r.x += 0.02f * rng.nextFloat() - 0.01f;
			w_r.y += 0.02f * rng.nextFloat() - 0.01f;
			w_r.z += 0.02f * rng.nextFloat() - 0.01f;
			w_r.cross(w_f).cross(w_f).neg().norm();
		}
	}

	private Vector w_p = new Vector();
	private Vector w_f = new Vector();
	private Vector w_r = new Vector();
}
