package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.di.PerScreen;
import ru.fixapp.fooproject.domainlayer.FileInfoConverter;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractorImpl;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractorImpl;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.domainlayer.providers.SchedulersProvider;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;

@PerScreen
@Component(dependencies = ActivityComponent.class, modules = {RecordListScreenComponent.ListSampleScreenModule.class})
public interface RecordListScreenComponent {
	void inject(RecordListScreenFragment fragment);

	@Module
	@PerScreen
	class ListSampleScreenModule {

		@PerScreen
		@Provides AudioStorageInteractor provideAudioStorageInteractor(AudioRepo recordDP,
																	   FileInfoConverter fileInfoConverter){
			return new AudioStorageInteractorImpl(recordDP, fileInfoConverter);
		}

		@PerScreen
		@Provides
		SignalProcessorInteractor provideSignalProcesserInteractor(AudioRepo recordDP,
																   AudioSettings audioSettings,
																   SchedulersProvider schedulers){
			return new SignalProcessorInteractorImpl(recordDP, audioSettings, schedulers);
		}
	}
}