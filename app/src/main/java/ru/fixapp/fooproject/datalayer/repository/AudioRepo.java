package ru.fixapp.fooproject.datalayer.repository;


import java.io.DataOutputStream;
import java.nio.ShortBuffer;

import rx.Observable;

public interface AudioRepo {

	Observable<String> getNextPathForAudio();

	Observable<String> getStoragePath();

	Observable<ShortBuffer> getFileStreamObservable(String path);

	Observable<DataOutputStream> getDataStreamObservable(String path);
}
