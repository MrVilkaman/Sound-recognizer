package ru.fixapp.fooproject.datalayer.repository;


import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.UUID;

import ru.fixapp.fooproject.presentationlayer.utils.StorageUtils;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

import static rx.Observable.fromCallable;
import static rx.Observable.just;

public class AudioRepoImpl implements AudioRepo {

	private StorageUtils storageUtils;

	public AudioRepoImpl(StorageUtils storageUtils) {
		this.storageUtils = storageUtils;
	}

	public void init() {
		getStoragePath().map(s -> new File(s).mkdir())
				.subscribe();
	}

	@Override
	public Observable<String> getNextPathForAudio() {
		return getStoragePath()
				.map(s -> s + UUID.randomUUID()
						.toString() + ".3gpp");
	}

	@Override
	public Observable<String> getStoragePath() {
		return just(storageUtils.getStoragePath()).map(s -> s + "/audio/");
	}

	private long lastModified;
	private ShortBuffer cacheBuffer;

	@Override
	public Observable<ShortBuffer> getFileStreamObservable(String path) {
		return Observable.fromCallable(
				() -> new File(path))
				.subscribeOn(Schedulers.io())
				.concatMap(file -> {
					long l = file.lastModified();
					if (l != lastModified || cacheBuffer == null) {
						return readFromFile(file).doOnNext(ignore -> lastModified = l);
					}else{
						return fromCallable(() -> cacheBuffer);
					}
				});

	}

	@NonNull
	private Observable<ShortBuffer> readFromFile(File file) {
		return fromCallable(() -> new FileInputStream(file))
				.map(is -> {
					try {
						ByteBuffer allocate = ByteBuffer.allocate(is.available());
						byte[] b = new byte[65536];
						int bytesRead;
						while ((bytesRead = is.read(b)) != -1) {
							allocate = allocate.put(b,0,bytesRead);
						}
						allocate.position(0);
						return allocate.asShortBuffer();
					} catch (Exception e) {
						throw Exceptions.propagate(e);
					}
				}).doOnNext(shortBuffer -> cacheBuffer = shortBuffer);
	}

	@Override
	public Observable<DataOutputStream> getDataStreamObservable(String path) {
		return Observable.fromCallable(() -> new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(new File(path)))));
	}
}
