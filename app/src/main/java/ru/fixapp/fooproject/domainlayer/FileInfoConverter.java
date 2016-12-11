package ru.fixapp.fooproject.domainlayer;

import android.content.Context;

import java.io.File;

import javax.inject.Inject;

import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class FileInfoConverter {
	private final AudioSettings settings;
	private Context context;

	@Inject
	public FileInfoConverter(Context context, AudioSettings settings) {
		this.context = context;
		this.settings = settings;
	}

	public static int calculateAudioLength(long samplesCount, int sampleRate, int channelCount) {
		return (int) (((samplesCount / channelCount) * 1000) / sampleRate);
	}

	public AudioModel convert(File file) {


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
		long sampleCount = size / settings.getBytePerSample();
		duration = calculateAudioLength(sampleCount, settings.getSampleRate(),1);

//		}

		return new AudioModel("format", "", size, duration, file.getAbsolutePath(),sampleCount,
				file.lastModified());
	}

}
