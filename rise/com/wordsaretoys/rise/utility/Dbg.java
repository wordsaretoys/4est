package com.wordsaretoys.rise.utility;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * debug display wrapper
 */
public class Dbg {

	HashMap<String, StringBuffer> map;
	String[] keys;
	TextView debugView;
	Handler handler;
	
	public Dbg(Context context, int id) {
		Activity main  = (Activity) context;

		map = new HashMap<String, StringBuffer>();
		keys = new String[1];
		
		debugView = (TextView) main.findViewById(id);
		handler = new Handler(new Handler.Callback() {
			
			StringBuffer text = new StringBuffer();
			String[] keys = new String[16];

			@Override
			public boolean handleMessage(Message msg) {
				text.setLength(0);
				map.keySet().toArray(keys);
				for (int i = 0; i < map.size(); i++) {
					text.append(keys[i]).append(": ");
					text.append(map.get(keys[i])).append('\n');
					
				}

				// always show memory status
				text.append("free: ").append(Runtime.getRuntime().freeMemory()).append(' ');
				text.append("heap: ").append(Runtime.getRuntime().totalMemory()).append('\n');
				
				debugView.setText(text);
				handler.sendEmptyMessageDelayed(0, 250);
				return false;
			}
		});
		handler.sendEmptyMessage(0);
	}
	
	private StringBuffer find(String key) {
		StringBuffer sb = map.get(key);
		if (sb == null) {
			sb = new StringBuffer();
			map.put(key, sb);
		}
		return sb;
	}
	
	public synchronized void set(String key, String s) {
		StringBuffer sb = find(key);
		sb.setLength(0);
		sb.append(s);
	}

	public synchronized void set(String key, float f) {
		StringBuffer sb = find(key);
		sb.setLength(0);
		sb.append(f);
	}

	public synchronized void set(String key, int i) {
		StringBuffer sb = find(key);
		sb.setLength(0);
		sb.append(i);
	}

	public synchronized void set(String key, float f, float n) {
		StringBuffer sb = find(key);
		sb.setLength(0);
		f = (float)(Math.floor(f * n) / n);
		sb.append(f);
	}

	public synchronized void set(String key, float x, float y, float z, float n) {
		StringBuffer sb = find(key);
		sb.setLength(0);
		x = (float)(Math.floor(x * n) / n);
		y = (float)(Math.floor(y * n) / n);
		z = (float)(Math.floor(z * n) / n);
		sb.append(x).append(",").append(y).append(",").append(z);
	}
}
