package ru.fixapp.fooproject.domainlayer;

import android.content.Context;

import java.io.File;

import javax.inject.Inject;

import ru.fixapp.fooproject.domainlayer.interactors.AudioRecorderInteractor;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class FileInfoConverter {
	private Context context;

	@Inject
	public FileInfoConverter(Context context) {
		this.context = context;
	}

	public static int calculateAudioLength(int samplesCount, int sampleRate, int channelCount) {
		return ((samplesCount / channelCount) * 1000) / sampleRate;
	}

	public AudioModel convert(File file) {

		String type;
		long duration;
//		if (file.exists()) {
//			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//			Uri uri = Uri.fromFile(file);
//			retriever.setDataSource(context, uri);
//			type = retriever
//					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
//			duration = Long.parseLong(retriever
//					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//		} else {
		long size = file.length();
		type = "";
		duration = calculateAudioLength((int) (size / 2), AudioRecorderInteractor.SAMPLING_RATE,1);
//		}

		return new AudioModel(file.getName(), type, size, duration, file.getAbsolutePath());
	}

}
