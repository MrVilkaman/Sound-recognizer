package ru.fixapp.fooproject.domainlayer.interactors;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.util.Log;

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

	}

	@Override
	public Observable<Integer> play(String pathToFile, float offsetStart, float offsetEnd) {

		return Observable.combineLatest(getObjectObservable(), getFileStreamObservable(pathToFile),
				(o, container) -> container)
				.observeOn(Schedulers.newThread())
				.map(container -> {

					Log.d("Audio","audioTrack write thread"+Thread.currentThread().getName());
					return audioTrack.write(container.audioBuffer, 0,
							container.audioBuffer.length);
				})
				.ignoreElements();
	}

	@NonNull
	private Observable<Object> getObjectObservable() {
		return Observable.fromCallable(() -> {

			audioTrack = new AudioTrack(
					AudioManager.STREAM_MUSIC,
					IAudioRecorderInteractor.SAMPLING_RATE,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					bufferSize,
					AudioTrack.MODE_STREAM);
			audioTrack.play();
			return null;
		});
	}


	private Observable<Container> getFileStreamObservable(String path) {
		return Observable.fromCallable(
				() -> new FileInputStream(new File(path)))
				.subscribeOn(Schedulers.io())
				.concatMap(new Func1<FileInputStream, Observable<Container>>() {
					@Override
					public Observable<Container> call(FileInputStream is) {
						return Observable.create(subscriber -> {
							short[] audioBuffer = new short[bufferSize / 2];
							try {
								byte[] b = new byte[bufferSize];
								int bytesRead;
								while ((bytesRead = is.read(b)) != -1) {

									for (int i = 0; i < bytesRead/ 2; i++) {
										int lB = b[i * 2] & 0xff;
										int rB = b[i * 2 + 1] << 8;
										audioBuffer[i] = (short) (lB | rB);
									}
									subscriber.onNext(new Container(audioBuffer));
								}
//								byte[] bytes = bos.toByteArray();
							} catch (Exception e) {
								e.printStackTrace();
							}


							if (!subscriber.isUnsubscribed())
								subscriber.onCompleted();
						});
					}
				})
				.onBackpressureBuffer();
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
