package ru.fixapp.fooproject.domainlayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

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
		if (file.exists()) {
			MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
			Uri uri = Uri.fromFile(file);
			mediaMetadataRetriever.setDataSource(context, uri);
			type = mediaMetadataRetriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
			duration = Long.parseLong(mediaMetadataRetriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		} else {
			type = "";
			duration = 0;
		}
		long size = file.length();

		return new AudioModel(file.getName(), type, size, duration, file.getAbsolutePath());
	}

}
