package ru.fixapp.fooproject.presentationlayer.formaters;


import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordsFormat {

	private final SimpleDateFormat dateFormat;

	@Inject
	public RecordsFormat() {
		dateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
	}

	@NonNull
	public String format(AudioModel item) {
		Date lastModDate = new Date(item.lastModified());
		String type = dateFormat.format(lastModDate);
		float v = item.getDuration() / 1000f;
		String format = String.format(Locale.getDefault(), "Длительность %.1fc", v);
		String format2 = String.format("Размер %s кб", String.valueOf(item.getSize() / 1024));
		return type + '\n' + format + '\n' + format2;
	}

	public String formatOffset(float start, float end, long sampleCount, long duraction) {
		float startD = duraction * start / sampleCount/ 1000;
		float endD = duraction * end / sampleCount/ 1000;
		return String.format("%1.2f - %2.2f",startD,endD);
	}
}
