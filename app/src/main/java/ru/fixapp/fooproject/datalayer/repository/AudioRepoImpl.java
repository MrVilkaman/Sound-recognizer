package ru.fixapp.fooproject.datalayer.repository;


import java.io.File;
import java.util.UUID;

import ru.fixapp.fooproject.presentationlayer.utils.StorageUtils;
import rx.Observable;

import static rx.Observable.just;

public class AudioRepoImpl implements AudioRepo {

	private StorageUtils storageUtils;
	private String lastPath;

	public AudioRepoImpl(StorageUtils storageUtils) {
		this.storageUtils = storageUtils;
	}

	@Override
	public Observable<String> getNextPathForAudio() {
		return getStoragePath().map(s -> s + UUID.randomUUID().toString() + ".3gpp")
				.doOnNext(s -> lastPath = s);
	}

	@Override
	public Observable<String> getStoragePath() {
		return just(storageUtils.getStoragePath()).map(s -> s+"/audio/");
	}

	public void init() {
		getStoragePath().map(s -> new File(s).mkdir()).subscribe();
	}
}
