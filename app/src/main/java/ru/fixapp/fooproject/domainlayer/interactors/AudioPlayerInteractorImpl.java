package ru.fixapp.fooproject.domainlayer.interactors;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.concurrent.TimeUnit;

import ru.fixapp.fooproject.domainlayer.exceptions.MediaPlayerError;
import rx.Observable;
import rx.subscriptions.Subscriptions;

public class AudioPlayerInteractorImpl implements AudioPlayerInteractor {

	private Context context;
	private MediaPlayer mp;

	public AudioPlayerInteractorImpl(Context context) {
		this.context = context;
	}

	@Override
	public Observable<Integer> play(String pathToFile, float offsetStart, float offsetEnd) {
		return Observable.create(subscriber -> {
			stop();
			mp = MediaPlayer.create(context, Uri.parse(pathToFile));
			if (mp != null) {
				subscriber.onNext(mp.getAudioSessionId());
				mp.seekTo((int) (offsetStart * 1000));
				mp.start();

				mp.setOnCompletionListener(mp1 -> {
					if (!subscriber.isUnsubscribed())
						subscriber.onCompleted();

				});
				mp.setOnErrorListener((mp1, what, extra) -> {
					if (!subscriber.isUnsubscribed())
						subscriber.onError(new MediaPlayerError(what, extra));
					return false;
				});
				subscriber.add(Subscriptions.create(this::stop));

				long v1 = (long) ((offsetEnd - offsetStart) * 1000);
				subscriber.add(Observable.empty()
						.delay(v1, TimeUnit.MILLISECONDS)
						.subscribe(o -> {},throwable -> {}, this::stop));


			} else {
				//??
				subscriber.onCompleted();
			}

		});

	}

	@Override
	public void stop() {
		if (mp != null) {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.release();
			mp = null;
		}
	}
}