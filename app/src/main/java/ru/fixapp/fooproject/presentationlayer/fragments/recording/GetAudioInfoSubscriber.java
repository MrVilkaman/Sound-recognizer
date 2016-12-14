package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.presentationlayer.formaters.RecordsFormat;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class GetAudioInfoSubscriber extends ViewSubscriber<RecordingView, AudioModel> {

	private final RecordingPresenterCache cache;
	private final RecordsFormat recordsFormat;

	public GetAudioInfoSubscriber(RecordingView view, RecordingPresenterCache cache,
								  RecordsFormat recordsFormat) {
		super(view);
		this.cache = cache;
		this.recordsFormat = recordsFormat;
	}

	@Override
	public void onNext(AudioModel model) {
		cache.setDuraction(model.getDuration());
		cache.setSampleCount(model.getSampleCount());
		view().showAudioInfo(recordsFormat.format(model));
		((RecordingPresenter) view().getPresenter()).update();
	}
}
