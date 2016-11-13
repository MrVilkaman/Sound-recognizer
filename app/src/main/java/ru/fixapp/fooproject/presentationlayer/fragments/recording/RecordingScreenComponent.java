package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import android.content.Context;

import net.jokubasdargis.rxbus.Bus;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.di.PerScreen;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractorImpl;
import ru.fixapp.fooproject.domainlayer.interactors.AudioRecorderInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.IAudioRecorderInteractor;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;

@PerScreen
@Component(dependencies = ActivityComponent.class,
		modules = {RecordingScreenComponent.RecordingScreenModule.class})
public interface RecordingScreenComponent {
	void inject(RecordingScreenFragment fragment);


	@Module
	class RecordingScreenModule {
		@Provides
		@PerScreen
		IAudioRecorderInteractor recorder(AudioRepo recordDP, Bus bus) {
			return new AudioRecorderInteractor(recordDP, bus);
		}


		@PerScreen
		@Provides
		AudioPlayerInteractor provideAudioPlayer(Context context){
			return new AudioPlayerInteractorImpl(context);
		}
	}
}