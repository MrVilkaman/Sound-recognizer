package ru.fixapp.fooproject.domainlayer.interactors;

import java.io.File;
import java.util.List;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.FileInfoConverter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;
import rx.Observable;

public class AudioStorageInteractorImpl implements AudioStorageInteractor {

	private final AudioRepo recordDP;
	private final FileInfoConverter fileInfoConverter;

	public AudioStorageInteractorImpl(AudioRepo recordDP, FileInfoConverter fileInfoConverter) {
		this.recordDP = recordDP;
		this.fileInfoConverter = fileInfoConverter;
	}

	@Override
	public Observable<List<AudioModel>> getAudio() {
		return recordDP.getStoragePath()
				.map(s -> new File(s).listFiles())
				.flatMap((array) -> Observable.from(array)
						.map(fileInfoConverter::convert)
						.toList()
				);
	}

	@Override
	public String getNewPathForAudio() {
		return recordDP.getNextPathForAudio().toBlocking().first();
	}

	@Override
	public void deleteFileByPath(AudioModel path) {
		new File(path.getAbsolutePath()).delete();
	}
}
