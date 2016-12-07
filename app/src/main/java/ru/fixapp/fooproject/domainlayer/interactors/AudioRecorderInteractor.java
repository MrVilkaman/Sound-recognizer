package ru.fixapp.fooproject.domainlayer.interactors;

import android.media.MediaRecorder;

import net.jokubasdargis.rxbus.Bus;

import java.io.IOException;

import ru.fixapp.fooproject.presentationlayer.models.AudioEvents;
import ru.fixapp.fooproject.presentationlayer.models.QueriesBus;
import rx.Observable;

public class AudioRecorderInteractor implements IAudioRecorderInteractor {

	private final Bus bus;

	private MediaRecorder recorder;

	public AudioRecorderInteractor(Bus bus) {
		this.bus = bus;
	}

	@Override
	public Observable<Void> start(String path) {
		return Observable.create(subscriber -> {
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setOutputFile(path);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
			recorder.setAudioEncodingBitRate(64000);
			recorder.setAudioSamplingRate(SAMPLING_RATE);

			try {
				recorder.prepare();
				recorder.start();
				if (!subscriber.isUnsubscribed())
					subscriber.onCompleted();
			} catch (IOException e) {
				if (!subscriber.isUnsubscribed())
					subscriber.onError(e);
			}
		});
	}

	@Override
	public Observable<Void> stop() {
		return Observable.create(subscriber -> {
			if (recorder != null) {
				try {
					recorder.stop();
					recorder.release();
					if (!subscriber.isUnsubscribed())
						subscriber.onCompleted();
					bus.publish(QueriesBus.AUDIO_EVENTS_QUEUE, AudioEvents.RECORDED);
				} catch (RuntimeException e) {
					if (!subscriber.isUnsubscribed())
						subscriber.onError(e);
				} finally {
					recorder = null;
				}
			}

		});

	}
}
