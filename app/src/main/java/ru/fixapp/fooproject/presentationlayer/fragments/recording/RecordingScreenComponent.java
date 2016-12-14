package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import android.content.Context;

import net.jokubasdargis.rxbus.Bus;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.di.PerScreen;
import ru.fixapp.fooproject.domainlayer.FileInfoConverter;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractorImpl;
import ru.fixapp.fooproject.domainlayer.interactors.AudioTrackPlayerInteractorImpl;
import ru.fixapp.fooproject.domainlayer.interactors.AudioTrackRecorderInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.IAudioRecorderInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractorImpl;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;

@PerScreen
@Component(dependencies = ActivityComponent.class,
		modules = {RecordingScreenComponent.RecordingScreenModule.class})
public interface RecordingScreenComponent {

	void inject(RecordingScreenFragment fragment);


	@Module
	class RecordingScreenModule {

		@PerScreen
		@Provides AudioStorageInteractor provideAudioStorageInteractor(AudioRepo recordDP,
																	   FileInfoConverter fileInfoConverter,
																	   AudioSettings audioSettings,
																	   SignalProcessorInteractor
																					   processorInteractor){
			return new AudioStorageInteractorImpl(recordDP, fileInfoConverter, audioSettings,
					processorInteractor);
		}

		@Provides
		@PerScreen
		IAudioRecorderInteractor recorder(Bus bus, AudioSettings settings, AudioRepo audioRepo) {
			return new AudioTrackRecorderInteractor(bus, settings, audioRepo);
		}

		@PerScreen
		@Provides
		AudioPlayerInteractor provideAudioPlayer(Context context, AudioSettings settings,
												 AudioRepo audioRepo){
			return new AudioTrackPlayerInteractorImpl(context,settings, audioRepo);
		}

		@PerScreen
		@Provides
		RecordingPresenterCache provideRecordingPresenterCache(){
			return new RecordingPresenterCache();
		}
		@PerScreen
		@Provides
		SignalProcessorInteractor provideSignalProcesserInteractor(){
			return new SignalProcessorInteractorImpl();
		}
	}
}