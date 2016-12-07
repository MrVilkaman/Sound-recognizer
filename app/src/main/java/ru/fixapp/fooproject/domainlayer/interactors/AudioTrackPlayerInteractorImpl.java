package ru.fixapp.fooproject.domainlayer.interactors;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AudioTrackPlayerInteractorImpl implements AudioPlayerInteractor {
	private final Context context;
	private AudioTrack audioTrack;
	private int bufferSize;

	public AudioTrackPlayerInteractorImpl(Context context) {
		this.context = context;
		init();
	}

	private void init() {
		bufferSize = AudioTrack.getMinBufferSize(
				IAudioRecorderInteractor.SAMPLING_RATE, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
			bufferSize = IAudioRecorderInteractor.SAMPLING_RATE * 2;
		}

		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				IAudioRecorderInteractor.SAMPLING_RATE,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				bufferSize,
				AudioTrack.MODE_STREAM);

	}

	@Override
	public Observable<Integer> play(String pathToFile, float offsetStart, float offsetEnd) {

		return Observable.combineLatest(getObjectObservable(), getFileStreamObservable(pathToFile),
				(o, container) -> {
					audioTrack.write(container.audioBuffer, 0, container.audioBuffer.length);
					return null;
				})
				.observeOn(Schedulers.newThread())
				.onBackpressureBuffer()
				.map(container -> 0);
	}

	@NonNull
	private Observable<Object> getObjectObservable() {
		return Observable.fromCallable(() -> {
			audioTrack.play();
			return null;
		});
	}


	private Observable<Container> getFileStreamObservable(String path) {
		return Observable.fromCallable(
				() -> new BufferedInputStream(new FileInputStream(new File(path))))
				.subscribeOn(Schedulers.io())
				.concatMap(new Func1<BufferedInputStream, Observable<Container>>() {
					@Override
					public Observable<Container> call(BufferedInputStream in) {
						return Observable.create(subscriber -> {
							byte[] contents = new byte[bufferSize];
							short[] audioBuffer = new short[bufferSize / 2];

							int bytesRead = 0;
							try {
								while ((bytesRead = in.read(contents)) != -1) {
									for (int i = 0; i < bytesRead; i += 2) {
										int lB = contents[i] & 0xff;
										int rB = contents[i + 1] << 8;
										audioBuffer[i] = (short) (lB |rB);
									}
									subscriber.onNext(new Container(audioBuffer));

								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (!subscriber.isUnsubscribed())
								subscriber.onCompleted();
						});
					}
				})

				;
	}

	@Override
	public void stop() {

	}

	private class Container {
		private final short[] audioBuffer;

		public Container(short[] audioBuffer) {
			this.audioBuffer = audioBuffer;
		}
	}
}
