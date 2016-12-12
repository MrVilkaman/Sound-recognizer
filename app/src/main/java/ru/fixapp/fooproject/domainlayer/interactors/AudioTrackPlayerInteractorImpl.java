package ru.fixapp.fooproject.domainlayer.interactors;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import rx.Observable;

public class AudioTrackPlayerInteractorImpl implements AudioPlayerInteractor {
	private final AudioSettings settings;
	private final AudioRepo audioRepo;
	private AudioTrack audioTrack;

	public AudioTrackPlayerInteractorImpl(Context context, AudioSettings settings,
										  AudioRepo audioRepo) {
		this.settings = settings;
		this.audioRepo = audioRepo;
	}

	@Override
	public Observable<Integer> play(String pathToFile, float offsetStart, float offsetEnd) {

		return Observable.combineLatest(getObjectObservable(), audioRepo.getFileStreamObservable(pathToFile),
				(o, container) -> container)
//				.observeOn(Schedulers.newThread())
				.map(container -> {
					short[] buffer = new short[settings.getBufferSize()];
					container.rewind();
					container.position((int) (long)offsetStart);
					int limit = (int) (long) offsetEnd;
					limit = Math.min(limit,container.limit());
					int totalWritten = 0;
					while (container.position() < limit ) {
						int numSamplesLeft = limit - container.position();
						int samplesToWrite;
						if (numSamplesLeft >= buffer.length) {
							container.get(buffer);
							samplesToWrite = buffer.length;
						} else {
							for (int i = numSamplesLeft; i < buffer.length; i++) {
								buffer[i] = 0;
							}
							container.get(buffer, 0, numSamplesLeft);
							samplesToWrite = numSamplesLeft;
						}
						totalWritten += samplesToWrite;
						audioTrack.write(buffer, 0, samplesToWrite);
					}
					return 0;
				})
				.map(q ->{
					audioTrack.stop();
					return 0;
				})
				.ignoreElements();
	}

	@NonNull
	private Observable<Object> getObjectObservable() {
		return Observable.fromCallable(() -> {

			audioTrack = new AudioTrack(
					AudioManager.STREAM_MUSIC,
					settings.getSampleRate(),
					AudioFormat.CHANNEL_OUT_MONO,
					settings.getEncoding(),
					settings.getBufferSize(),
					AudioTrack.MODE_STREAM);
			audioTrack.play();
			return null;
		});
	}

	@Override
	public void stop() {

	}
}
