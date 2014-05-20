package com.wordsaretoys.rise.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * methods for loading and managing assets
 */
public class Asset {

	/**
	 * reads a text file from the assets
	 */
	static public String getTextAsset(Context context, String fileName) {
		String text = "";
		try {
			InputStream stream = context.getAssets().open(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
				while( (line = reader.readLine()) != null) {
					text += line + "\n";
				}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * read image file from assets into bitmap 
	 */
	static public Bitmap getBitmapAsset(Context context, String fileName) {
	    InputStream istr;
	    Bitmap bitmap = null;
	    try {
	        istr = context.getAssets().open(fileName);
	        bitmap = BitmapFactory.decodeStream(istr);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return bitmap;	
	}
	
	/**
	 * read image file from assets into color array 
	 */
	static public int[] getImageAsset(Context context, String fileName) {
	    InputStream istr;
	    Bitmap bitmap = null;
	    try {
	        istr = context.getAssets().open(fileName);
	        bitmap = BitmapFactory.decodeStream(istr);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		// extract the bitmap data
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] c = new int[w * h];
		bitmap.getPixels(c, 0, w, 0, 0, w, h);
	    return c;	
	}
	
	/**
	 * read a binary file from the assets
	 */
	static public boolean getBinaryAsset(Context context, String fileName, byte[] buffer) {
		try {
			InputStream stream = context.getAssets().open(fileName);
			BufferedInputStream bist = new BufferedInputStream(stream);
			bist.read(buffer);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
