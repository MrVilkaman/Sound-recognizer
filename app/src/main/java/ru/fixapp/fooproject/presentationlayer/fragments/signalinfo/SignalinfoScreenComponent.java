package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;

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
import ru.fixapp.fooproject.presentationlayer.fragments.recording.RecordingPresenterCache;

@PerScreen
@Component(dependencies = ActivityComponent.class,
		modules = {SignalinfoScreenComponent.SignalinfoScreenModule.class})
public interface SignalinfoScreenComponent {
	void inject(SignalinfoScreenFragment fragment);


	@Module
	class SignalinfoScreenModule {
		@PerScreen
		@Provides
		AudioStorageInteractor provideAudioStorageInteractor(AudioRepo recordDP,
															 FileInfoConverter fileInfoConverter) {
			return new AudioStorageInteractorImpl(recordDP, fileInfoConverter);
		}

		@PerScreen
		@Provides
		RecordingPresenterCache provideRecordingPresenterCache() {
			return new RecordingPresenterCache();
		}

		@PerScreen
		@Provides
		SignalProcessorInteractor provideSignalProcesserInteractor(AudioRepo audioRepo,
																   AudioSettings settings,
																   SchedulersProvider schedulers) {
			return new SignalProcessorInteractorImpl(audioRepo, settings, schedulers);
		}
		@PerScreen
		@Provides
		SignalinfoPresenterCache provideSignalinfoPresenterCache() {
			return new SignalinfoPresenterCache();
		}

	}
}