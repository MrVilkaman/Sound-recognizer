package ru.fixapp.fooproject.domainlayer.interactors;

import rx.Observable;

public interface IAudioRecorderInteractor {

    Observable<Void> start(String path);

    Observable<Void> stop();
}
