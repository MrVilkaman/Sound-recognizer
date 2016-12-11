package ru.fixapp.fooproject.domainlayer.interactors;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.FileInfoConverter;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;
import rx.Observable;
import rx.exceptions.Exceptions;

public class AudioStorageInteractorImpl implements AudioStorageInteractor {

	private final AudioRepo recordDP;
	private final FileInfoConverter fileInfoConverter;
	private final AudioSettings audioSettings;


	public AudioStorageInteractorImpl(AudioRepo recordDP, FileInfoConverter fileInfoConverter,
									  AudioSettings audioSettings) {
		this.recordDP = recordDP;
		this.fileInfoConverter = fileInfoConverter;
		this.audioSettings = audioSettings;
	}

	@Override
	public Observable<List<AudioModel>> getAudio() {
		return recordDP.getStoragePath()
				.map(s -> new File(s).listFiles())
				.flatMap((array) -> Observable.from(array)
						.map(this::doConvert)
						.toList()
				);
	}

	@NonNull
	private AudioModel doConvert(File file) {return fileInfoConverter.convert(file);}

	@Override
	public Observable<AudioModel> getAudioInfo(String path) {
		return Observable.just(path)
				.map(File::new)
				.concatMap(file -> file.exists() ? Observable.just(file) :
						Observable.error(new IllegalArgumentException()))
				.map(this::doConvert);
	}

	@Override
	public String getNewPathForAudio() {
		return recordDP.getNextPathForAudio()
				.toBlocking()
				.first();
	}

	@Override
	public void deleteFileByPath(AudioModel path) {
		new File(path.getAbsolutePath()).delete();
	}

	@Override
	public Observable<List<Entry>> getGraphInfo(String path) {
		return Observable.just(path)
				.map(this::getFileInputStream)
				.map(this::getByteArray)
				.map(o -> {
					byte[] bytes = o.toByteArray();
					List<Entry> entries = new ArrayList<>();
					if (audioSettings.isPCM16BIT()) {
						for (int i = 0; i < bytes.length / 2; i++) {
							int lB = bytes[i * 2] & 0xff;
							int rB = bytes[i * 2 + 1] << 8;
							short sample = (short) (lB | rB);
							entries.add(new Entry(i, sample));
						}
					}else {
						for (int i = 0; i < bytes.length ; i++) {
							entries.add(new Entry(i, bytes[i]));
						}
					}
					return entries;
				});
	}

	@NonNull
	private ByteArrayOutputStream getByteArray(FileInputStream fileInputStream) {
		int bytesRead;
		byte[] b = new byte[8192];
		ByteArrayOutputStream bos = new ByteArrayOutputStream(65536);
		try {

			while ((bytesRead = fileInputStream.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			return bos;
		} catch (Exception e) {
			throw Exceptions.propagate(e);
		}
	}

	@NonNull
	private FileInputStream getFileInputStream(String p) {
		try {
			return new FileInputStream(p);
		} catch (FileNotFoundException e) {
			throw Exceptions.propagate(e);
		}
	}
}
