package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.datalayer.subscriber.ErrorSubscriber;

public class StopAudioSubscriber extends ErrorSubscriber<Void> {
	public StopAudioSubscriber(RecordingView view) {super(view);}

	@Override
	public void onCompleted() {
		view().showToast(R.string.audio_recorded);
		((RecordingPresenter) view().getPresenter()).updateInfo();
	}
}
