package ru.fixapp.fooproject.domainlayer.interactors;

import android.media.MediaRecorder;

import net.jokubasdargis.rxbus.Bus;

import java.io.IOException;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.presentationlayer.models.AudioEvents;
import ru.fixapp.fooproject.presentationlayer.models.QueriesBus;
import rx.Observable;

public class AudioRecorderInteractor implements IAudioRecorderInteractor {

	private final Bus bus;
	private final AudioRepo recordDP;

	private MediaRecorder recorder;

	public AudioRecorderInteractor(AudioRepo recordDP, Bus bus) {
		this.recordDP = recordDP;
		this.bus = bus;
	}

	@Override
	public Observable<Void> start() {
		return recordDP.getNextPathForAudio()
				.concatMap(nextPathForAudio -> Observable.create(subscriber -> {
					recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					recorder.setOutputFile(nextPathForAudio);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
					recorder.setAudioEncodingBitRate(64);
					recorder.setAudioSamplingRate(44100);

					try {
						recorder.prepare();
						recorder.start();
						if (!subscriber.isUnsubscribed())
							subscriber.onCompleted();
					} catch (IOException e) {
						if (!subscriber.isUnsubscribed())
							subscriber.onError(e);
					}
				}));
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
