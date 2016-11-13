package ru.fixapp.fooproject.domainlayer.providers;


import rx.Observable;

public interface AudioPlayer {

	Observable<Integer> play(String pathToFile);

	void stop();
}
