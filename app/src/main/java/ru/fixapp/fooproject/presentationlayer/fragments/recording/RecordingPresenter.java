package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

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
import rx.Observable;

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
			Observable<AudioModel> modelObs = storageInteractor.getAudioInfo(cache.getPath());
			subscribeUI(modelObs, new GetAudioInfoSubscriber());

			Observable<List<Entry>> graphObs = storageInteractor.getGraphInfo(cache.getPath());
			subscribeUI(graphObs, new RecordingViewListViewSubscriber(view()));
		}
	}

	public void startRecording() {
		subscribe(audioRecorder.start(cache.getPath()), new ErrorSubscriber<>(view()));
	}

	public void stopRecording() {
		subscribe(audioRecorder.stop(), new VoidErrorSubscriber(view()));
	}

	public void playLastAudio() {
		subscribeUI(audioPlayerInteractor.play(cache.getPath(), cache.getStart(), cache.getEnd()),
				new RecordingViewIntegerViewSubscriber(view()));
	}

	public void setNextTimePoint(long offset) {
		boolean startNow = cache.isStartNow();
		if (startNow) {
			long max = Math.max(cache.getEnd(), offset);
			long min = Math.min(cache.getEnd(), offset);
			cache.setStart(min);
			cache.setEnd(max);
		} else {
			long max = Math.max(cache.getStart(), offset);
			long min = Math.min(cache.getStart(), offset);
			cache.setStart(min);
			cache.setEnd(max);
		}
		cache.setStartNow(!startNow);
		update();
	}

	private void update() {
		view().setRangeTime(
				recordsFormat.formatOffset(cache.getStart(), cache.getEnd(), cache
								.getSampleCount(),
						cache.getDuraction()));
	}

	public void cutAudio() {
		Observable<String> graphObs = storageInteractor.cutAudio(cache.getPath(),cache.getStart(), cache.getEnd());
		subscribeUI(graphObs, new ViewSubscriber<RecordingView, String>(view()) {
			@Override
			public void onNext(String path) {
				super.onNext(path);
				cache.setCurrentPath(path);
			}

			@Override
			public void onCompleted() {
				updateInfo();
			}
		});
	}

	private static class RecordingViewIntegerViewSubscriber
			extends ViewSubscriber<RecordingView, Integer> {
		public RecordingViewIntegerViewSubscriber(RecordingView view) {super(view);}

		@Override
		public void onNext(Integer integer) {
			//				view().setupVisualizerFxAndUI(integer);
		}
	}

	private static class RecordingViewListViewSubscriber
			extends ViewSubscriber<RecordingView, List<Entry>> {
		public RecordingViewListViewSubscriber(RecordingView view) {super(view);}

		@Override
		public void onNext(List<Entry> entries) {
			view().updateVisualizer(entries);
		}
	}

	private class GetAudioInfoSubscriber
			extends ViewSubscriber<RecordingView, AudioModel> {
		public GetAudioInfoSubscriber() {super(RecordingPresenter.this.view());}

		@Override
		public void onNext(AudioModel model) {
			cache.setDuraction(model.getDuration());
			cache.setSampleCount(model.getSampleCount());
			view().showAudioInfo(recordsFormat.format(model));
			update();
		}
	}

	private class VoidErrorSubscriber extends ErrorSubscriber<Void> {
		public VoidErrorSubscriber(RecordingView view) {super(view);}

		@Override
		public void onCompleted() {
			view().showToast(R.string.audio_recorded);
			updateInfo();
		}
	}
}
