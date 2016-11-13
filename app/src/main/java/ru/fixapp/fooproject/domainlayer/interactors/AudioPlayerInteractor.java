package ru.fixapp.fooproject.domainlayer.interactors;


import rx.Observable;

public interface AudioPlayerInteractor {
	Observable<Integer> play(String pathToFile);

	void stop();
}
