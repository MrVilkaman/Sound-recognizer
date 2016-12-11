package ru.fixapp.fooproject.domainlayer.interactors;


import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ru.fixapp.fooproject.presentationlayer.models.AudioModel;
import rx.Observable;

public interface AudioStorageInteractor {

	Observable<List<AudioModel>> getAudio();

	Observable<AudioModel> getAudioInfo(String path);

	String getNewPathForAudio();

	void deleteFileByPath(AudioModel path);

	Observable<List<Entry>> getGraphInfo(String path);
}
