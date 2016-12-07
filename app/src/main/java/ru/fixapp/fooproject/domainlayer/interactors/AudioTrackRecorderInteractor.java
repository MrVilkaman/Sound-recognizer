package ru.fixapp.fooproject.domainlayer.interactors;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.jokubasdargis.rxbus.Bus;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.fixapp.fooproject.presentationlayer.models.AudioEvents;
import ru.fixapp.fooproject.presentationlayer.models.QueriesBus;
import rx.Observable;
import rx.functions.Func1;
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
		return Observable.combineLatest(getAudioRecordObservable(),
				getFileStreamObservable(path),(container, dataOutputStream) -> saveToFile(dataOutputStream, container)).ignoreElements().map(r -> null);
	}

	@Nullable
	private Void saveToFile(DataOutputStream stream, Container container) {
		try {
			short[] audioBuffer = container.audioBuffer;
			Log.e("Audio", "start writeShort " + audioBuffer.length);
			for (int i = 0; i < audioBuffer.length; i++) {
				stream.writeShort(audioBuffer[i]); //Save each number
			}
			Log.e("Audio", "End writeShort " + audioBuffer.length);

		} catch (IOException e) {
			Log.e("Audio", "File write failed: " + e.toString());
		}
		return null;
	}

	private Observable<DataOutputStream> getFileStreamObservable(String path) {
		return Observable.fromCallable(() -> new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(new File(path)))))
				.subscribeOn(Schedulers.io());
	}

	private Observable<Container> getAudioRecordObservable() {
		return Observable.fromCallable(() -> {
			Log.d("Audio", "state " + record.getState());
			record.startRecording();
			return null;
		})
				.observeOn(Schedulers.newThread())
				.concatMap(new Func1<Object, Observable<Container>>() {
					@Override
					public Observable<Container> call(Object o) {
						return Observable.create(subscriber -> {
							long shortsRead = 0;
							short[] audioBuffer = new short[bufferSize / 2];
							Log.d("Audio", "start Read");
							while (record.getRecordingState() == RECORDSTATE_RECORDING) {
								int numberOfShort = record.read(audioBuffer, 0, audioBuffer
										.length);
								shortsRead += numberOfShort;
								subscriber.onNext(new Container(audioBuffer));
								Log.d("Audio", "read =" + shortsRead);
							}
							Log.d("Audio", "stop Read");
							if (!subscriber.isUnsubscribed())
								subscriber.onCompleted();
						});
					}
				})
				.onBackpressureBuffer()
				.observeOn(Schedulers.io());
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

	private static class Container {
		private final short[] audioBuffer;

		public Container(short[] audioBuffer) {
			this.audioBuffer = audioBuffer;
		}
	}
}
