package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;

public class CutAudioSubscriber extends ViewSubscriber<RecordingView, String> {
	private final RecordingPresenterCache cache;

	public CutAudioSubscriber(RecordingView view, RecordingPresenterCache cache) {
		super(view);
		this.cache = cache;
	}

	@Override
	public void onNext(String path) {
		super.onNext(path);
		cache.setCurrentPath(path);
	}

	@Override
	public void onCompleted() {
		((RecordingPresenter) view().getPresenter()).updateInfo();
	}
}