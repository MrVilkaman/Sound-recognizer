package ru.fixapp.fooproject.di.modules;

import android.content.Context;
import android.media.AudioFormat;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.presentationlayer.utils.StorageUtils;

/**
 * Created by Zahar on 24.03.16.
 */
@Module
@Singleton
public class AppModule {

	@NonNull private Context context;

	public AppModule(@NonNull Context context) {
		this.context = context;
	}

	@Provides
	@NonNull
	@Singleton
	public Context provideContext() {
		return context;
	}

	@Provides
	@NonNull
	@Singleton
	public StorageUtils provideStorageUtils() {
		return new StorageUtils(context);
	}


	@Singleton
	@Provides
	public AudioSettings getAudioSettings(){
		return new AudioSettings(22050, AudioFormat.ENCODING_PCM_16BIT,
				AudioFormat.CHANNEL_IN_MONO);
	}
}
