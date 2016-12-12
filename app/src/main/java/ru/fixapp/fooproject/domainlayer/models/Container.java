package ru.fixapp.fooproject.domainlayer.models;

public class Container {
	private final byte[] audioBufferByte;
	private final short[] audioBuffer;

	public Container(short[] audioBuffer) {
		this.audioBuffer = audioBuffer;
		audioBufferByte = null;
	}

	public Container(byte[] audioBufferByte) {
		this.audioBufferByte = audioBufferByte;
		audioBuffer = null;
	}

	public byte[] getAudioBufferByte() {
		return audioBufferByte;
	}

	public short[] getAudioBuffer() {
		return audioBuffer;
	}
}