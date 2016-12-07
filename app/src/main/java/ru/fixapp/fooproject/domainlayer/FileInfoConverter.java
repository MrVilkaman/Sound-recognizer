package ru.fixapp.fooproject.domainlayer;

import android.content.Context;

import java.io.File;

import javax.inject.Inject;

import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class FileInfoConverter {
	private Context context;

	@Inject
	public FileInfoConverter(Context context) {
		this.context = context;
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
			type = "";
			duration = 0;
//		}
		long size = file.length();

		return new AudioModel(file.getName(), type, size, duration, file.getAbsolutePath());
	}

}
