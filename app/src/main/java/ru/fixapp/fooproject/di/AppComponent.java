package ru.fixapp.fooproject.di;

import android.content.Context;

import net.jokubasdargis.rxbus.Bus;

import javax.inject.Singleton;

import dagger.Component;
import ru.fixapp.fooproject.datalayer.api.RestApi;
import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.di.modules.ApiModule;
import ru.fixapp.fooproject.di.modules.AppModule;
import ru.fixapp.fooproject.di.modules.EventBusModule;
import ru.fixapp.fooproject.di.modules.ImageLoaderModule;
import ru.fixapp.fooproject.di.modules.NetworkModule;
import ru.fixapp.fooproject.di.modules.ProvidersModule;
import ru.fixapp.fooproject.di.modules.RepoModule;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.domainlayer.providers.SchedulersProvider;
import ru.fixapp.fooproject.domainlayer.providers.SessionDataProvider;
import ru.fixapp.fooproject.presentationlayer.resolution.ImageLoader;
import ru.fixapp.fooproject.presentationlayer.utils.StorageUtils;

/**
 * Created by Zahar on 24.03.16.
 */
@Component(modules = {
		AppModule.class,
		ApiModule.class,
		NetworkModule.class,
		EventBusModule.class,
		RepoModule.class,
		ImageLoaderModule.class,
		ProvidersModule.class})
@Singleton
public interface AppComponent {

	SessionDataProvider getSessionDataProvider();

	SchedulersProvider getSchedulersProvider();

	RestApi provideApi();

	Bus provideBus();

	StorageUtils storageUtils();

	Context provideContext();

	ImageLoader provideImageLoader();

	///
	AudioRepo provideAudioRepo();

	AudioSettings getAudioSettings();

}
