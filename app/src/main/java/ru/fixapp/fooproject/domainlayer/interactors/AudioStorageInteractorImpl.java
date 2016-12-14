package ru.fixapp.fooproject.domainlayer.interactors;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.FileInfoConverter;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.domainlayer.models.Container;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;
import rx.Observable;
import rx.exceptions.Exceptions;

public class AudioStorageInteractorImpl implements AudioStorageInteractor {

	private final AudioRepo recordDP;
	private final FileInfoConverter fileInfoConverter;
	private final AudioSettings audioSettings;
	private final SignalProcessorInteractor signalProcessorInteractor;


	public AudioStorageInteractorImpl(AudioRepo recordDP, FileInfoConverter fileInfoConverter,
									  AudioSettings audioSettings,
									  SignalProcessorInteractor signalProcessorInteractor) {
		this.recordDP = recordDP;
		this.fileInfoConverter = fileInfoConverter;
		this.audioSettings = audioSettings;
		this.signalProcessorInteractor = signalProcessorInteractor;
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
		return recordDP.getFileStreamObservable(path)
//				.map(shortBuffer -> {
//					short[] shortBuff = new short[shortBuffer.limit()];
//					shortBuffer.get(shortBuff);
//					shortBuff = signalProcessorInteractor.getFrame(shortBuff);
//					shortBuffer.clear();
//					shortBuffer.put(shortBuff);
//					return shortBuffer;
//				})
				.map(shortBuffer -> {
					shortBuffer.rewind();
					short[] shortBuff = new short[shortBuffer.limit()];
					shortBuffer.get(shortBuff);
					List<Entry> entries = new ArrayList<>();
					if (audioSettings.isPCM16BIT()) {
						for (int i = 0; i < shortBuff.length; i++) {
							entries.add(new Entry(i, shortBuff[i]));
						}
					}
					return entries;
				});
	}

	@Override
	public Observable<String> cutAudio(String path, long start, long end) {

		Observable<String> temp = recordDP.getNextPathForAudio().cache();

		Observable<Container> map = recordDP.getFileStreamObservable(path)
				.map(shortBuffer -> {
					int shortCount = (int) (end - start);
					short[] shortBuff = new short[shortCount];
					shortBuffer.position((int) start);
					shortBuffer.get(shortBuff, 0, shortCount);
					return new Container(shortBuff);
				});

		Observable<DataOutputStream> other = temp.concatMap(recordDP::getDataStreamObservable);
		return map.withLatestFrom(other, (container, stream) -> {
			try {
				short[] shorts = container.getAudioBuffer();
				for (int i = 0; i < shorts.length; i++) {
					stream.writeShort(shorts[i]); //Save each number
				}
				stream.close();
			} catch (IOException e) {
				throw Exceptions.propagate(e);
			}
			return null;
		}).concatMap(o -> temp);


	}
}
