package ru.fixapp.fooproject.di.modules;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.datalayer.repository.AudioRepoImpl;
import ru.fixapp.fooproject.presentationlayer.utils.StorageUtils;

@Module
public class RepoModule {

	@Singleton
	@Provides
	public AudioRepo provideAudioRepo(StorageUtils storageUtils) {
		AudioRepoImpl audioRepo = new AudioRepoImpl(storageUtils);
		audioRepo.init();
		return audioRepo;
	}
}
