package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;

public class LoadTrackGraphSubscriber extends ViewSubscriber<RecordingView, List<Entry>> {
	public LoadTrackGraphSubscriber(RecordingView view) {super(view);}

	@Override
	public void onNext(List<Entry> entries) {
		view().updateVisualizer(entries);
	}
}