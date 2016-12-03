package ru.fixapp.fooproject.domainlayer.interactors;


import java.util.List;

import ru.fixapp.fooproject.presentationlayer.models.AudioModel;
import rx.Observable;

public interface AudioStorageInteractor {

	Observable<List<AudioModel>> getAudio();

	String getNewPathForAudio();

	void deleteFileByPath(AudioModel path);
}
