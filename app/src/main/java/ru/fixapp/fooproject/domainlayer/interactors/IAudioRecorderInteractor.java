package ru.fixapp.fooproject.domainlayer.interactors;

import rx.Observable;

public interface IAudioRecorderInteractor {

    Observable<Void> start();

    Observable<Void> stop();
}
