package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import android.os.Bundle;

public class RecordingPresenterCache {


	public static final String KEY_CAN_RECORD = "canRecord";
	public static final String KEY_PATH = "path";
	private String path;
	private boolean canRecord = true;
	private long duraction;
	private float start;
	private float end;
	private boolean isStartNow = true;

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
		setEnd(duraction/1000f);
	}

	public long getDuraction() {
		return duraction;
	}

	public float getStart() {
		return start;
	}

	public void setStart(float offset) {
		this.start = offset;
	}

	public void setEnd(float end) {
		this.end = end;
	}

	public float getEnd() {
		return end;
	}

	public boolean isStartNow() {
		return isStartNow;
	}

	public void setStartNow(boolean startNow) {
		isStartNow = startNow;
	}
}
