package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import android.os.Bundle;

public class RecordingPresenterCache {


	public static final String KEY_CAN_RECORD = "canRecord";
	public static final String KEY_PATH = "path";
	private String path;
	private boolean canRecord = true;
	private long duraction;
	private float offset;

	public boolean canRecord() {
		return canRecord;
	}

	public String getPath() {
		return path;
	}

	public void setCurrentPath(String path) {
		canRecord = false;
		this.path = path;
	}

	public boolean hasPath() {
		return path != null;
	}

	public void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			canRecord = savedInstanceState.getBoolean(KEY_CAN_RECORD, true);
			path = savedInstanceState.getString(KEY_PATH, null);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_CAN_RECORD, canRecord);
		outState.putString(KEY_PATH, path);
	}

	public void setNewPath(String newPathForAudio) {
		path = newPathForAudio;
	}

	public void setDuraction(long duraction) {
		this.duraction = duraction;
	}

	public long getDuraction() {
		return duraction;
	}

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}
}
