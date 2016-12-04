package ru.fixapp.fooproject.domainlayer.interactors;


import rx.Observable;

public interface AudioPlayerInteractor {
	Observable<Integer> play(String pathToFile,float offset);

	void stop();
}
