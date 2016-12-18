package ru.fixapp.fooproject.presentationlayer.fragments.recording;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import javax.inject.Inject;

import ru.fixapp.fooproject.datalayer.subscriber.ErrorSubscriber;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.IAudioRecorderInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractor;
import ru.fixapp.fooproject.presentationlayer.formaters.RecordsFormat;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;
import rx.Observable;

public class RecordingPresenter extends BasePresenter<RecordingView> {

	private final IAudioRecorderInteractor audioRecorder;
	private final AudioPlayerInteractor audioPlayerInteractor;
	private final AudioStorageInteractor storageInteractor;
	private final SignalProcessorInteractor signalProcessorInteractor;


	private final RecordingPresenterCache cache;
	private final RecordsFormat recordsFormat;

	@Inject
	public RecordingPresenter(IAudioRecorderInteractor audioRecorder,
							  AudioPlayerInteractor audioPlayerInteractor,
							  AudioStorageInteractor storageInteractor,
							  SignalProcessorInteractor signalProcessorInteractor,
							  RecordingPresenterCache cache, RecordsFormat recordsFormat) {
		this.audioRecorder = audioRecorder;
		this.audioPlayerInteractor = audioPlayerInteractor;
		this.storageInteractor = storageInteractor;
		this.signalProcessorInteractor = signalProcessorInteractor;
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

	void updateInfo() {
		if (cache.hasPath()) {
			Observable<AudioModel> modelObs = storageInteractor.getAudioInfo(cache.getPath());
			subscribeUI(modelObs, new GetAudioInfoSubscriber(view(), cache, recordsFormat));

			Observable<List<Entry>> graphObs = signalProcessorInteractor.getGraphInfo(cache.getPath());
			subscribeUI(graphObs, new LoadTrackGraphSubscriber(view()));
		}
	}

	public void startRecording() {
		subscribe(audioRecorder.start(cache.getPath()), new ErrorSubscriber<>(view()));
	}

	public void stopRecording() {
		subscribe(audioRecorder.stop(), new StopAudioSubscriber(view()));
	}

	public void playLastAudio() {
		if (cache.isNowInPlay()) {
			view().showPlayBtn();
			audioPlayerInteractor.stop();
		} else {
			subscribeUI(
					audioPlayerInteractor.play(cache.getPath(), cache.getStart(), cache.getEnd(),
							cache.isReply()),
					new PlayAudioSubscriber(view()));
		}
	}

	public void setNextTimePoint(long offset) {
		boolean startNow = cache.isStartNow();
		swapEnds(offset, startNow ? cache.getEnd() : cache.getStart());
		cache.setStartNow(!startNow);
		update();
	}

	private void swapEnds(long offset, long end) {
		long max = Math.max(end, offset);
		long min = Math.min(end, offset);
		cache.setStart(min);
		cache.setEnd(max);
	}

	void update() {
		view().setRangeTime(
				recordsFormat.formatOffset(cache.getStart(), cache.getEnd(), cache
						.getSampleCount(), cache.getDuraction()));
	}

	public void cutAudio() {
		Observable<String> graphObs =
				storageInteractor.cutAudio(cache.getPath(), cache.getStart(), cache.getEnd());
		subscribeUI(graphObs, new CutAudioSubscriber(view(), cache));
	}
}

