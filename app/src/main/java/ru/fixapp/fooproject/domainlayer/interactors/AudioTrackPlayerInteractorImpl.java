package ru.fixapp.fooproject.domainlayer.interactors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;

import java.nio.ShortBuffer;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import rx.Observable;

public class AudioTrackPlayerInteractorImpl implements AudioPlayerInteractor {
	private final AudioSettings settings;
	private final AudioRepo audioRepo;
	private AudioTrack audioTrack;
	private boolean inPlay;
	private SignalProcessorInteractor signalProcessorInteractor;

	public AudioTrackPlayerInteractorImpl( AudioSettings settings,
										  AudioRepo audioRepo,
										   SignalProcessorInteractor signalProcessorInteractor) {
		this.settings = settings;
		this.audioRepo = audioRepo;
		this.signalProcessorInteractor = signalProcessorInteractor;
	}

	@Override
	public Observable<Integer> play(String pathToFile, float offsetStart, float offsetEnd,
									boolean reply) {
		inPlay = true;
		Observable<ShortBuffer> fileStreamObservable =
				audioRepo.getFileStreamObservable(pathToFile)
						.map(shortBuffer -> {
							ShortBuffer duplicate = shortBuffer.duplicate();
							duplicate.rewind();
							short[] shortBuff = new short[duplicate.limit()];
							duplicate.get(shortBuff);
							shortBuff = signalProcessorInteractor.getFrame(shortBuff);
							duplicate.clear();
							duplicate.put(shortBuff);
							return shortBuffer;
						});
		return Observable.combineLatest(getObjectObservable(),
				fileStreamObservable,
				(o, container) -> container)
				.map(container -> {
					short[] buffer = new short[settings.getBufferSize()];
					container.rewind();
					container.position((int) (long)offsetStart);
					int limit = (int) (long) offsetEnd;
					limit = Math.min(limit,container.limit());
					int totalWritten = 0;
					while (container.position() < limit && inPlay) {
						int numSamplesLeft = limit - container.position();
						int samplesToWrite;
						if (buffer.length <= numSamplesLeft) {
							container.get(buffer);
							samplesToWrite = buffer.length;
						} else {
							for (int i = numSamplesLeft; i < buffer.length; i++) {
								buffer[i] = 0;
							}
							container.get(buffer, 0, numSamplesLeft);
							samplesToWrite = numSamplesLeft;
							if (reply) {
								container.position((int) (long)offsetStart);
							}
						}
						totalWritten += samplesToWrite;
						audioTrack.write(buffer, 0, samplesToWrite);
					}
					return 0;
				})
				.doOnUnsubscribe(this::stop)
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
		inPlay = false;
		if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			audioTrack.stop();
		}
	}
}
