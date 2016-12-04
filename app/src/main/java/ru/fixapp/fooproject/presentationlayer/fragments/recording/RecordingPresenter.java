package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import javax.inject.Inject;

import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.datalayer.subscriber.ErrorSubscriber;
import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.IAudioRecorderInteractor;
import ru.fixapp.fooproject.presentationlayer.formaters.RecordsFormat;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordingPresenter extends BasePresenter<RecordingView> {

	private final IAudioRecorderInteractor audioRecorder;
	private final AudioPlayerInteractor audioPlayerInteractor;
	private final AudioStorageInteractor storageInteractor;
	private final RecordingPresenterCache cache;
	private final RecordsFormat recordsFormat;

	@Inject
	public RecordingPresenter(IAudioRecorderInteractor audioRecorder,
							  AudioPlayerInteractor audioPlayerInteractor,
							  AudioStorageInteractor storageInteractor,
							  RecordingPresenterCache cache, RecordsFormat recordsFormat) {
		this.audioRecorder = audioRecorder;
		this.audioPlayerInteractor = audioPlayerInteractor;
		this.storageInteractor = storageInteractor;
		this.cache = cache;
		this.recordsFormat = recordsFormat;
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();
		if (!cache.canRecord()) {
			view().hideRecordButton();
		}
		if (!cache.hasPath()) {
			cache.setNewPath(storageInteractor.getNewPathForAudio());
		}
		updateInfo();
	}

	private void updateInfo() {
		if (cache.hasPath()) {
			subscribeUI(storageInteractor.getAudioInfo(cache.getPath()),
					new ViewSubscriber<RecordingView, AudioModel>(view()) {
						@Override
						public void onNext(AudioModel model) {
							cache.setDuraction(model.getDuration());
							view().showAudioInfo(recordsFormat.format(model));
							view().showBytes(model.getAbsolutePath());
						}
					});
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
				updateInfo();
			}
		});
	}

	public void playLastAudio() {
		subscribeUI(audioPlayerInteractor.play(cache.getPath(),cache.getOffset()),
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
