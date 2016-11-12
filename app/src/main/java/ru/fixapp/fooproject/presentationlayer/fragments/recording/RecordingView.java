package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;

public interface RecordingView extends BaseView {

	void showAudioInfo(String textAudio);

	String getCurrentPath();

	void hideRecordButton();

	void setupVisualizerFxAndUI(int audioSessionId);

	void showViz();

	void showBytes(String absolutePath);
}