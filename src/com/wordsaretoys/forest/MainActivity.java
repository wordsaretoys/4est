package com.wordsaretoys.forest;

import com.wordsaretoys.forest.R;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	static Shared shared;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (shared == null) {
			shared = new Shared();
		}
		shared.onCreate(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Shared.audio.pause();
	}
	
	protected void onResume() {
		super.onResume();
		Shared.audio.play();
	}

}
