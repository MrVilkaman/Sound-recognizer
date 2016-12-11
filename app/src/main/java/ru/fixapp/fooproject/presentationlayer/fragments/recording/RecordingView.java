package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;

public interface RecordingView extends BaseView {

	void showAudioInfo(String textAudio);

	void hideRecordButton();

	void updateVisualizer(List<Entry> entries);

	void setRangeTime(String text);
}