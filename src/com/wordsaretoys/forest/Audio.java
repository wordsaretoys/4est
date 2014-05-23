package com.wordsaretoys.forest;

import java.util.Arrays;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.wordsaretoys.rise.utility.Needle;

/**
 * audio synthesizer engine
 */
public class Audio {

	static String TAG = "AudioEngine";
	
	// audio pump thread helper
	Needle pump;
	
	// audio track object
	private AudioTrack track;
	
	// length of buffer in shorts
	private int bufferLength;
	
	// sampling rate in Hz
	private int sampleRate;
	
	// audio staging buffer
	private float[] stager;
	
	// audio hardware buffer
	private short[] buffer;
	
	// master volume
	private float volume;
	
	// ambient chorus
	Chorus chorus;
	
	// crash sound effect
	Crash crash;
	
	/**
	 * ctor; called on first reference to singleton
	 */
	public Audio() {
		pump = new Needle("audio pump", 1) {
			@Override
			public void run() {
				init();
				while (inPump()) {
					loop();
				}
				term();
			}
		};
		pump.start();
		volume = 1;
	}

	/**
	 * start playback at time zero
	 */
	public void play() {
		pump.resume();
		if (track != null) {
			track.play();
		}
	}
	
	/**
	 * pause playback
	 */
	public void pause() {
		pump.pause();
		track.pause();
	}
	
	/**
	 * terminate audio engine
	 */
	public void close() {
		pump.stop();
	}

	/**
	 * startup and initialization  
	 */
	private void init() {
		pump.setPriority(Thread.MAX_PRIORITY);
		
		sampleRate = AudioTrack.getNativeOutputSampleRate(
				AudioManager.STREAM_MUSIC);

		bufferLength = AudioTrack.getMinBufferSize(
				sampleRate, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);

		buffer = new short[bufferLength];
		stager = new float[bufferLength];

		chorus = new Chorus();
		crash = new Crash();
		
		track = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				sampleRate,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				bufferLength * 2, // length in bytes
				AudioTrack.MODE_STREAM);
		
		if (track.getState() != AudioTrack.STATE_INITIALIZED) {
			throw new RuntimeException("Couldn't initialize AudioTrack object");
		}
		
		track.setStereoVolume(1, 1);
		track.play();
	}
	
	/**
	 * main audio pump loop
	 */
	private void loop() {
		stage();
		track.write(buffer, 0, buffer.length);
	}

	/**
	 * terminate and cleanup
	 */
	private void term() {
		track.stop();
		track.release();
	}
	
	/**
	 * stage all active voices to audio buffer
	 */
	private void stage() {
		Arrays.fill(stager, 0);
		chorus.fill(stager);
		crash.fill(stager);
		
		// headroom mix from staging buffer to audio buffer
		for (int i = 0, il = buffer.length; i < il; i++) {
			float b = volume * stager[i];
			if (b <= -1.25f)
			{
			    b = -0.987654f;
			}
			else if (b >= 1.25f)
			{
			    b = 0.987654f;
			}
			else
			{
			    b = 1.1f * b - 0.2f * b * b * b;
			}
			buffer[i] = (short)(32767 * b);
		}
	}
	
	/**
	 * generates a breathy tone at specified freqency
	 * 
	 * choose both noiseFactor and limiter in the range (0..1)
	 * with noiseFactor several orders of magnitude less than limiter
	 * otherwise, positive feedback will create awful distortion
	 * note that higher limiter means lower resulting tone frequency
	 * 
	 * good values include
	 * 		noiseFactor = 0.0002, limiter = 0.01 for audible tone
	 * 		noiseFactor = 0.002, limiter = 0.5 for more windy sound
	 * 
	 */
	class Whistler {
		
		float noiseFactor, limiter, qFactor;
		float t0, t1, t2;
		float[] buffer;
		long noise;
		
		public Whistler(float nf, float lm) {
			noiseFactor = nf;
			limiter = lm;
			t2 = 0.001f;
			buffer = new float[bufferLength];
			noise = System.currentTimeMillis(); 
		}
		
		public void setPitch(double f) {
			qFactor = (float)(1 / Math.pow(sampleRate / (2 * Math.PI * f), 2));
		}
		
		public void fill() {
			for (int i = 0; i < buffer.length; i++) {
				float n = (float)((noise >> 16) * (t2 > 0 ? -1 : 1)) / (float)(0xffffffffL);
				t2 = (float)(-qFactor * t0 + noiseFactor * n - t2 * limiter);
				t1 += t2;
				t0 += t1;
				buffer[i] = t0;
				noise = (noise * 25214903917L + 11L) & 0xffffffffffffL;
			}
		}
	}
	
	/**
	 * crossfades between two whistlers over a scale 
	 * 
	 * scale array contains frequencies in Hz for each note
	 * of the scale. notes will be randomly selected. rate
	 * denotes how quickly the crossfades occur, in units of
	 * buffers per second.
	 */
	class Voice {

		Whistler wh0, wh1;
		float [] scale;
		float rate, time;
		
		public Voice(float[] scale, float rate) {
			this.scale = scale;
			this.rate = rate / sampleRate;
			wh0 = new Whistler(0.002f, 0.75f);
			wh0.setPitch(nextNote());
			wh1 = new Whistler(0.002f, 0.75f);
			wh1.setPitch(nextNote());
			time = 0;
		}
		
		public void fill(float[] buffer) {
			wh0.fill();
			wh1.fill();
			for (int i = 0; i < buffer.length; i++) {
				time += rate;
				if (time >= 1) {
					time = time % 1;
					Whistler swap = wh0;
					wh0 = wh1;
					wh1 = swap;
					wh1.setPitch(nextNote());
				}
				buffer[i] += (1 - time) * wh0.buffer[i] + time * wh1.buffer[i];
			}
		}
		
		float nextNote() {
			return scale[(int)(Math.random() * scale.length)];
		}
	}

	/**
	 * combines two voices in a scale
	 */
	class Chorus {
		
		float[] scale = {440.0f, 528.0f, 616.0f, 704.0f, 792.0f};
		Voice voice0, voice1, voice2;
		
		public Chorus() {
			voice0 = new Voice(scale, 0.25f);
			voice1 = new Voice(scale, 0.33f);
		}
		
		public void fill(float buffer[]) {
			voice0.fill(buffer);
			voice1.fill(buffer);
		}
	}

	/**
	 * generates a crash sound
	 */
	class Crash {

		float[] sound;
		int index;
		
		public Crash() {
//			float rate = 1 / (sampleRate * 0.001f);
			sound = new float[65536];
			for (int i = 0; i < sound.length; i++) {
//				sound[i] = (float)(2 * Math.random() - 1);
			}
		}
		
		public void start() {
			index = 0;
		}
		
		public void fill(float[] buffer) {
			for (int i = 0; i < buffer.length && index < sound.length; i++, index++) {
				buffer[i] += sound[index];
			}
		}
		
	}
}
