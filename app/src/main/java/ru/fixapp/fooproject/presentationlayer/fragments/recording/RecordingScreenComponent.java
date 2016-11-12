package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import dagger.Component;
import dagger.Module;
import ru.fixapp.fooproject.di.PerScreen;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;

@PerScreen
@Component(dependencies = ActivityComponent.class,
		modules = {RecordingScreenComponent.RecordingScreenModule.class})
public interface RecordingScreenComponent {
	void inject(RecordingScreenFragment fragment);


	@Module
	class RecordingScreenModule {
//		@Provides
//		@PerScreen
//		IAudioRecorder recorder(AudioRecordDP recordDP) {
//			return new AudioRecorder(recordDP);
//		}
	}
}