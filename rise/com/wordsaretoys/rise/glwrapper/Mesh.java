package com.wordsaretoys.rise.glwrapper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.wordsaretoys.rise.meshutil.IndexBuffer;
import com.wordsaretoys.rise.meshutil.VertexBuffer;

/**
 * wraps GL vertex and index buffer objects
 * 
 * @author chris
 *
 */
public class Mesh {

	public int drawPrimitive = GLES20.GL_TRIANGLES;
	
	public int drawCount = 0;
	public int stride = 0;
	
	private int[] buffer = new int[2];
	
	private int[] attributeId;
	private int[] attributeSize;
	private int attributeCount;

	private boolean useIndexes;
	
	/**
	 * constructor, create attribute table and GL buffers
	 * @param attr packed array of attributes [id, size, id, size, ... ]
	 */
	public Mesh(int attr[]) {
		int count = attr.length >> 1;
		attributeId = new int[count];
		attributeSize = new int[count];
		for (int i = 0, j = 0, il = count * 2; i < il; i += 2, j++) {
			attributeId[j] = attr[i];
			attributeSize[j] = attr[i + 1];
			stride += attributeSize[j];
		}
		attributeCount = count;
	}
	
	/**
	 * create vertex (and index) buffer objects
	 * 
	 * @param vsize number of floats to allocate space for, may be 0
	 * @param isize number of shorts to allocate space for, may be 0
	 */
	public void create(int vsize, int isize) {
		// delete and recreate GL buffer objects
		// this appears to be necessary in the Android implementation
		// if you reuse the buffer, draw performance degrades quickly
		GLES20.glDeleteBuffers(2, buffer, 0);
		GLES20.glGenBuffers(2, buffer, 0);
		
		// allocate buffers as specified
		// we're just allocating space for the buffers on the GL side
		// need call to update() to actually put data in them
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vsize * 4, null, GLES20.GL_STATIC_DRAW);
		if (isize > 0) {
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer[1]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, isize * 2, null, GLES20.GL_STATIC_DRAW);
			// set draw length based on indexes
			drawCount = isize;
			useIndexes = true;
		} else {
			//set draw length based on vertexes
			drawCount = (int) Math.ceil(vsize / stride);
			useIndexes = false;
		}
	}
	
	/**
	 * write data to vertex (and index) buffers
	 * 
	 * buffer(s) must have been allocated with create()
	 * 
	 * @param vb vertex buffer data
	 * @param voffset starting position to place vertex data
	 * @param ib index buffer data (may be null)
	 * @param ioffset starting position to place index data
	 */
	public void update(VertexBuffer vb, int voffset, IndexBuffer ib, int ioffset) {
		// must at least have a vertex buffer!
		if (vb == null) {
			throw new RuntimeException("mesh.update called with invalid vertex buffer!");
		}

		// wrap vertex data in GL-compatible buffer type
		FloatBuffer fb = FloatBuffer.wrap(vb.data);
		
		// update specified region in GL vertex buffer with new data
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, voffset * 4, vb.length * 4, fb);
		
		// if we have index data
		if (ib != null) {
			
			// wrap index data in GL-compatible buffer type
			ShortBuffer sb = ShortBuffer.wrap(ib.data);

			// update specified region in GL index buffer with new data
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer[1]);
			GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ioffset * 2, ib.length * 2, sb);
		}
	}
	
	/**
	 * build mesh in one step from specified data buffers
	 * 
	 * @param vb vertex buffer
	 * @param ib index buffer, may be null
	 */
	public void build(VertexBuffer vb, IndexBuffer ib) {
		create(vb.length, ib.length);
		update(vb, 0, ib, 0);
	}
	
	/**
	 * build mesh in one step from specified vertex buffer
	 * 
	 * @param vb vertex buffer
	 */
	public void build(VertexBuffer vb) {
		create(vb.length, 0);
		update(vb, 0, null, 0);
	}
	
	/**
	 * draw the mesh
	 */
	public void draw() {
		// bind the buffers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer[1]);
		
		// enable and specify each attribute
		int i, acc;
		for (i = 0, acc = 0; i < attributeCount; i++) {
			GLES20.glEnableVertexAttribArray(attributeId[i]);
			GLES20.glVertexAttribPointer(attributeId[i], attributeSize[i], GLES20.GL_FLOAT, false, stride * 4, acc * 4);
			acc += attributeSize[i];
		}
		
		// draw elements/arrays
		if (useIndexes) {
			GLES20.glDrawElements(drawPrimitive, drawCount, GLES20.GL_UNSIGNED_SHORT, 0);
		} else {
			GLES20.glDrawArrays(drawPrimitive, 0, drawCount);
		}
		
		// disable attributes
		for (i = 0; i < attributeCount; i++) {
			GLES20.glDisableVertexAttribArray(attributeId[i]);
		}
	}

	/**
	 * release the GL buffers
	 */
	public void release() {
		GLES20.glDeleteBuffers(2, buffer, 0);
	}
}
