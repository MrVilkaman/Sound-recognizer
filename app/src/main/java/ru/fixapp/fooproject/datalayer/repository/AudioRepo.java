package ru.fixapp.fooproject.datalayer.repository;


import rx.Observable;

public interface AudioRepo {

	Observable<String> getNextPathForAudio();

	Observable<String> getStoragePath();

}
