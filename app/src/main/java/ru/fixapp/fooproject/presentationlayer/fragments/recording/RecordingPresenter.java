package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import javax.inject.Inject;

import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;

public class RecordingPresenter extends BasePresenter<RecordingView> {

//	private IAudioRecorder audioRecorder;
//
//	private GetLastAudioUpdatesUseCase audioUpdates;
//	private PlayLastAudioUseCase playLastAudio;

	@Inject
	public RecordingPresenter(
//			IAudioRecorder audioRecorder, GetLastAudioUpdatesUseCase audioUpdates,
//							  PlayLastAudioUseCase playLastAudio
	) {
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();
		String currentPath = view().getCurrentPath();
		if (currentPath != null) {
			view().hideRecordButton();
		}

//		audioUpdates.setCurrentPath(currentPath).execute(new GetLastAudioUpdatesSubscriber(view()));
	}


	public void startRecording() {
//		subscribe(audioRecorder.start(), new ErrorSubscriber<>(view()));
	}

	public void stopRecording() {
//		subscribe(audioRecorder.stop(), new ErrorSubscriber<Void>(view()){
//			@Override
//			public void onCompleted() {
//				view().showToast(R.string.audio_recorded);
//			}
//		});
	}

	public void playLastAudio() {
//		playLastAudio.setCurrentPath(view().getCurrentPath()).execute(new ViewSubscriber<RecordingView,Integer>(view()) {
//			@Override
//			public void onNext(Integer integer) {
////				view().setupVisualizerFxAndUI(integer);
//			}
//
//			@Override
//			public void onCompleted() {
//				super.onCompleted();
//				view().showViz();
//
//			}
//		});
	}
}
