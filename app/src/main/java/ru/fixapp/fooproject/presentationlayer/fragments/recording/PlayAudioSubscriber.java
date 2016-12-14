package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;

public class PlayAudioSubscriber extends ViewSubscriber<RecordingView, Integer> {
	public PlayAudioSubscriber(RecordingView view) {
		super(view);
		view().showPauseBtn();
	}

	@Override
	public void onNext(Integer integer) {
	}

	@Override
	public void onCompleted() {
		view().showPlayBtn();
	}

	@Override
	public void onError(Throwable e) {
		view().showPlayBtn();
	}
}
