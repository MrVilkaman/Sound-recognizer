package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;

public interface RecordingView extends BaseView, SpectrGraghView {

	void showAudioInfo(String textAudio);

	void hideRecordButton();

	void setRangeTime(String text);

	void showPlayBtn();

	void showPauseBtn();
}