package ru.fixapp.fooproject.domainlayer.interactors;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import net.jokubasdargis.rxbus.Bus;

import ru.fixapp.fooproject.presentationlayer.models.AudioEvents;
import ru.fixapp.fooproject.presentationlayer.models.QueriesBus;
import rx.Observable;
import rx.schedulers.Schedulers;

import static android.media.AudioRecord.RECORDSTATE_RECORDING;

public class AudioTrackRecorderInteractor implements IAudioRecorderInteractor {

	private final Bus bus;

	private AudioRecord record;
	private int bufferSize;

	public AudioTrackRecorderInteractor(Bus bus) {
		this.bus = bus;
		init();
	}

	private void init() {
		bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
			bufferSize = SAMPLING_RATE * 2;
		}

		record = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
				SAMPLING_RATE,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);
		Log.d("Audio", "state init" + record.getState());
	}

	@Override
	public Observable<Void> start(String path) {
//		recorder.setOutputFile(path);

		return Observable.fromCallable(() -> {
			Log.d("Audio", "state " + record.getState());
			record.startRecording();
			return null;
		})
				.observeOn(Schedulers.newThread())
				.concatMap(o ->
						Observable.create(subscriber -> {
							long shortsRead = 0;
							short[] audioBuffer = new short[bufferSize / 2];
							Log.d("Audio", "start Read");
							while (record.getRecordingState() == RECORDSTATE_RECORDING) {
								int numberOfShort = record.read(audioBuffer, 0, audioBuffer
										.length);
								shortsRead += numberOfShort;
								Log.d("Audio", "read =" + shortsRead);
							}
							Log.d("Audio", "stop Read");
							if (!subscriber.isUnsubscribed())
								subscriber.onCompleted();
						}));
	}

	@Override
	public Observable<Void> stop() {
		return Observable.fromCallable(() -> {
			Log.d("Audio", "stop ");
			record.stop();
			record.release();
			bus.publish(QueriesBus.AUDIO_EVENTS_QUEUE, AudioEvents.RECORDED);
			return null;
		});
	}
}
