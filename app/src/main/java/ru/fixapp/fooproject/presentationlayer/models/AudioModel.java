package ru.fixapp.fooproject.presentationlayer.models;

public class AudioModel {
	private final String name;
	private final String type;
	private final long size;
	private final long duration;
	private final long sampleCount;
	private String absolutePath;

	public AudioModel(String name, String type, long size, long duration, String absolutePath,
					  long sampleCount) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.duration = duration;
		this.absolutePath = absolutePath;
		this.sampleCount = sampleCount;
	}

	public long getSampleCount() {
		return sampleCount;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public long getSize() {
		return size;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return "AudioModel{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", size=" + size +
				", duration=" + duration +
				'}';
	}

	public String getAbsolutePath() {
		return absolutePath;
	}
}
