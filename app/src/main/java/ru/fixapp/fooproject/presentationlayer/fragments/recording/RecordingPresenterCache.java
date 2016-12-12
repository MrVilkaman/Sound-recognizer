package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import android.os.Bundle;

public class RecordingPresenterCache {


	public static final String KEY_CAN_RECORD = "canRecord";
	public static final String KEY_PATH = "path";
	private String path;
	private boolean canRecord = true;
	private long duraction;
	private long start;
	private long end;
	private boolean isStartNow = true;
	private long sampleCount;
	private boolean nowInPlay;
	private boolean reply;

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

	public long getStart() {
		return start;
	}

	public void setStart(long offset) {
		this.start = offset;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getEnd() {
		return end;
	}

	public boolean isStartNow() {
		return isStartNow;
	}

	public void setStartNow(boolean startNow) {
		isStartNow = startNow;
	}

	public void setSampleCount(long sampleCount) {
		setStart(0);
		setEnd(sampleCount);
		this.sampleCount = sampleCount;
	}

	public long getSampleCount() {
		return sampleCount;
	}

	public void setNowInPlay(boolean nowInPlay) {
		this.nowInPlay = nowInPlay;
	}

	public boolean isNowInPlay() {
		return nowInPlay;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public boolean isReply() {
		return reply;
	}
}
