package ru.fixapp.fooproject.presentationlayer.activities;


import android.content.Context;

import net.jokubasdargis.rxbus.Bus;

import dagger.Component;
import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.di.AppComponent;
import ru.fixapp.fooproject.di.PerActivity;
import ru.fixapp.fooproject.di.modules.activity.CommonActivityModule;
import ru.fixapp.fooproject.di.modules.activity.DrawerModule;
import ru.fixapp.fooproject.di.modules.activity.ToolbarModule;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.domainlayer.providers.SchedulersProvider;
import ru.fixapp.fooproject.presentationlayer.resolution.ImageLoader;
import ru.fixapp.fooproject.presentationlayer.resolution.navigation.NavigationResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.ThrowableResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.UIResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.toolbar.IToolbar;
import ru.fixapp.fooproject.presentationlayer.utils.StorageUtils;

@PerActivity
@Component(dependencies = AppComponent.class,
		modules = {CommonActivityModule.class, DrawerModule.class,
				ToolbarModule.class})
public interface ActivityComponent {
	void inject(MainActivity activity);

	SchedulersProvider getSchedulersProvider();

	Context getContext();

	UIResolver getUIResolver();

	ThrowableResolver getThrowableResolver();

	NavigationResolver getNavigationResolver();

	IToolbar getIToolbar();

	BaseActivityView provideBaseActivityView();

	ImageLoader provideImageLoader();

	StorageUtils storageUtils();

	Bus provideBus();

	AudioRepo provideAudioRepo();

	AudioSettings getAudioSettings();

}