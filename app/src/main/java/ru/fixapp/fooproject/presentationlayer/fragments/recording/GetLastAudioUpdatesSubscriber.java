package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import java.util.Locale;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class GetLastAudioUpdatesSubscriber extends ViewSubscriber<RecordingView, AudioModel> {
	public GetLastAudioUpdatesSubscriber(RecordingView view) {
		super(view);
	}

	@Override
	public void onNext(AudioModel item) {

		String name = item.getName();
		String type = item.getType();
		float v = item.getDuration() / 1000f;
		String format = String.format(Locale.getDefault(), "Длительность %.1fc", v);
		String format2 = String.format("Размер %s кб", String.valueOf(item.getSize() / 1024));
		view().showAudioInfo(name + '\n' + type + '\n' + format + '\n' + format2);
		view().showBytes(item.getAbsolutePath());
	}

}
