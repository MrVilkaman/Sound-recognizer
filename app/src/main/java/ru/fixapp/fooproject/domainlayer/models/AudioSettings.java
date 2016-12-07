package ru.fixapp.fooproject.domainlayer.models;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioSettings {

	private final int bufferSize;
	private final int encoding;
	private final int channel;
	private int sampleRate;
	private int audioSoure = MediaRecorder.AudioSource.VOICE_RECOGNITION;
	private long bytePerSample;

	public AudioSettings(int sampleRateInHz, int encoding, int channel) {
		sampleRate = sampleRateInHz;
		this.encoding = encoding;
		bytePerSample = isPCM16BIT() ? 2 : 1;

		this.channel = channel;
		bufferSize = getBufferSize(sampleRateInHz, encoding, channel);
	}

	private int getBufferSize(int sampleRateInHz, int encoding, int channel) {
		int buffer = AudioRecord.getMinBufferSize(sampleRateInHz, channel, encoding);

		if (buffer == AudioRecord.ERROR || buffer == AudioRecord.ERROR_BAD_VALUE) {
			buffer = sampleRateInHz * 2;
		}
		return buffer;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getEncoding() {
		return encoding;
	}

	public int getChannel() {
		return channel;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getAudioSoureForRecord() {
		return audioSoure;
	}

	public long getBytePerSample() {
		return bytePerSample;
	}

	public boolean isPCM16BIT() {
		return encoding == AudioFormat.ENCODING_PCM_16BIT;
	}
}
