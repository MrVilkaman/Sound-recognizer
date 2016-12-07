package ru.fixapp.fooproject.domainlayer.interactors;

import android.media.AudioRecord;
import android.support.annotation.Nullable;
import android.util.Log;

import net.jokubasdargis.rxbus.Bus;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.presentationlayer.models.AudioEvents;
import ru.fixapp.fooproject.presentationlayer.models.QueriesBus;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.media.AudioRecord.RECORDSTATE_RECORDING;

public class AudioTrackRecorderInteractor implements IAudioRecorderInteractor {

	private final Bus bus;
	private final AudioSettings settings;

	private AudioRecord record;

	public AudioTrackRecorderInteractor(Bus bus, AudioSettings settings) {
		this.bus = bus;
		this.settings = settings;
	}

	@Override
	public Observable<Void> start(String path) {
		return Observable.combineLatest(getAudioRecordObservable(),
				getFileStreamObservable(path),
				(container, dataOutputStream) -> saveToFile(dataOutputStream, container))
				.ignoreElements()
				.map(r -> null);
	}

	@Nullable
	private Void saveToFile(DataOutputStream stream, Container container) {
		try {
			if (settings.isPCM16BIT()) {
				short[] audioBuffer = container.audioBuffer;
				for (int i = 0; i < audioBuffer.length; i++) {
					stream.writeShort(audioBuffer[i]); //Save each number
				}
			} else {
				stream.write(container.audioBufferByte);
			}
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

			record = new AudioRecord(settings.getAudioSoureForRecord(),
					settings.getSampleRate(),
					settings.getChannel(),
					settings.getEncoding(),
					settings.getBufferSize());

			record.startRecording();
			return null;
		})
				.observeOn(Schedulers.newThread())
				.concatMap(new Func1<Object, Observable<Container>>() {
					@Override
					public Observable<Container> call(Object o) {
						return Observable.create(subscriber -> {
							if (settings.isPCM16BIT()) {
								short[] audioBuffer = new short[settings.getBufferSize() / 2];
								while (record.getRecordingState() == RECORDSTATE_RECORDING) {
									int numberOfShort = record.read(audioBuffer, 0, audioBuffer
											.length);
									subscriber.onNext(new Container(audioBuffer));
								}
							} else {
								byte[] audioBuffer = new byte[settings.getBufferSize()];
								while (record.getRecordingState() == RECORDSTATE_RECORDING) {
									int numberOfShort = record.read(audioBuffer, 0, audioBuffer
											.length);
									subscriber.onNext(new Container(audioBuffer));
								}
							}

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
			if (record != null) {
				record.stop();
				record.release();
				bus.publish(QueriesBus.AUDIO_EVENTS_QUEUE, AudioEvents.RECORDED);
			}
			record = null;
			return null;
		});
	}

	private static class Container {
		private final byte[] audioBufferByte;
		private final short[] audioBuffer;

		public Container(short[] audioBuffer) {
			this.audioBuffer = audioBuffer;
			audioBufferByte = null;
		}

		public Container(byte[] audioBufferByte) {
			this.audioBufferByte = audioBufferByte;
			audioBuffer = null;
		}
	}
}
