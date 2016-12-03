package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import javax.inject.Inject;

import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.datalayer.subscriber.ErrorSubscriber;
import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.IAudioRecorderInteractor;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;

public class RecordingPresenter extends BasePresenter<RecordingView> {

	private final IAudioRecorderInteractor audioRecorder;
	private final AudioPlayerInteractor audioPlayerInteractor;
	private final RecordingPresenterCache cache;

	@Inject
	public RecordingPresenter(IAudioRecorderInteractor audioRecorder,
							  AudioPlayerInteractor audioPlayerInteractor,
							  RecordingPresenterCache cache) {
		this.audioRecorder = audioRecorder;
		this.audioPlayerInteractor = audioPlayerInteractor;
		this.cache = cache;
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();
		if (!cache.canRecord()) {
			view().hideRecordButton();
		}
		if(!cache.hasPath()){
			// get new
		}

	}


	public void startRecording() {
		subscribe(audioRecorder.start(cache.getPath()), new ErrorSubscriber<>(view()));
	}

	public void stopRecording() {
		subscribe(audioRecorder.stop(), new ErrorSubscriber<Void>(view()) {
			@Override
			public void onCompleted() {
				view().showToast(R.string.audio_recorded);
			}
		});
	}

	public void playLastAudio() {
		subscribeUI(audioPlayerInteractor.play(cache.getPath()),
				new ViewSubscriber<RecordingView, Integer>(view()) {
					@Override
					public void onNext(Integer integer) {
						//				view().setupVisualizerFxAndUI(integer);
					}

					@Override
					public void onCompleted() {
						super.onCompleted();
						view().showViz();
					}
				});
	}
}
