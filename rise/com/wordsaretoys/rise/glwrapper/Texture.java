package com.wordsaretoys.rise.glwrapper;

import java.nio.IntBuffer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * represents a single texture object
 * 
 * @author chris
 *
 */
public class Texture {
	
	// GL id
	int[] id = new int[1];
	
	// GL texture type
	int type;

	/**
	 * ctor, creates id-only for surface texture usage
	 */
	@SuppressLint("InlinedApi")
	public Texture() {
		GLES20.glGenTextures(1, id, 0);
		type = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
	}

	/**
	 * ctor, creates texture from Android bitmap object
	 */
	public Texture(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		IntBuffer ib = IntBuffer.allocate(width * height);
		bitmap.copyPixelsToBuffer(ib);
		ib.rewind();

		// allocate a GL texture
		GLES20.glGenTextures(1, id, 0);
		type = GLES20.GL_TEXTURE_2D;
		
		// copy texture data and generate mipmap
		GLES20.glBindTexture(type, id[0]);
		GLES20.glTexImage2D(type, 0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glGenerateMipmap(type);
		GLES20.glBindTexture(type, 0);
	}
	
	/**
	 * ctor, creates texture from raw bitmap
	 */
	public Texture(int[] bitmap, int width, int height) {
		// allocate a GL texture
		GLES20.glGenTextures(1, id, 0);
		type = GLES20.GL_TEXTURE_2D;
		
		// copy texture data and generate mipmap
		IntBuffer ib = IntBuffer.wrap(bitmap);
		GLES20.glBindTexture(type, id[0]);
		GLES20.glTexImage2D(type, 0, GLES20.GL_RGBA, 
				width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glGenerateMipmap(type);
		GLES20.glBindTexture(type, 0);
	}
	
	/**
	 * ctor, creates cube map texture from 6 bitmaps
	 */
	public Texture(int[] nx, int[] ny, int[] nz, int[] px, int[]  py, int[] pz, int width, int height) {
		
		GLES20.glGenTextures(1, id, 0);
		type = GLES20.GL_TEXTURE_CUBE_MAP;
		
		GLES20.glBindTexture(type, id[0]);
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(nx));

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(ny));

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(nz));
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(px));

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(py));

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(pz));

		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glGenerateMipmap(type);
		GLES20.glBindTexture(type, 0);
	}

	/**
	 * ctor, creates cube map texture from 6 bitmaps
	 */
	public Texture(Bitmap nx, Bitmap ny, Bitmap nz, Bitmap px, Bitmap py, Bitmap pz) {
		int width = nx.getWidth();
		int height = ny.getHeight();
		IntBuffer ib = IntBuffer.allocate(width * height);

		GLES20.glGenTextures(1, id, 0);
		type = GLES20.GL_TEXTURE_CUBE_MAP;
		
		GLES20.glBindTexture(type, id[0]);
		
		nx.copyPixelsToBuffer(ib);
		ib.rewind();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		ib.clear();

		ny.copyPixelsToBuffer(ib);
		ib.rewind();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		ib.clear();

		nz.copyPixelsToBuffer(ib);
		ib.rewind();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		ib.clear();
		
		px.copyPixelsToBuffer(ib);
		ib.rewind();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		ib.clear();

		py.copyPixelsToBuffer(ib);
		ib.rewind();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		ib.clear();

		pz.copyPixelsToBuffer(ib);
		ib.rewind();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 
				0, GLES20.GL_RGBA, width, height, 0, 
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
		ib.clear();

		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameteri(type, 
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glGenerateMipmap(type);
		GLES20.glBindTexture(type, 0);
	}
	
	/**
	 * bind the texture to a sampler and texture unit
	 * 
	 * used in conjunction with shader.activate()
	 * the sampler parameter is available from the activated shader
	 * 
	 * @param index texture unit index {0..MAX_TEXTURE_IMAGE_UNITS}
	 * @param sampler id of sampler variable from shader
	 */
	public void bind(int index, int sampler) {
		GLES20.glUniform1i(sampler, index);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
		GLES20.glBindTexture(type, id[0]);
	}
	
	/**
	 * release the GL texture
	 */
	public void release() {
		GLES20.glDeleteTextures(1, id, 0);
	}
	
	/**
	 * return GL object id
	 */
	public int getId() {
		return id[0];
	}

	/**
	 * reset a default parameter
	 */
	public void set(int param, int value) {
		GLES20.glBindTexture(type, id[0]);
		GLES20.glTexParameteri(type, param, value);
		GLES20.glBindTexture(type, 0);
	}

}
