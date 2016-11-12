package ru.fixapp.fooproject.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.datalayer.providers.MainSchedulersProvider;
import ru.fixapp.fooproject.datalayer.providers.SessionDataProviderImpl;
import ru.fixapp.fooproject.datalayer.store.LocalStorage;
import ru.fixapp.fooproject.datalayer.store.LocalStorageImpl;
import ru.fixapp.fooproject.datalayer.store.MemoryStorage;
import ru.fixapp.fooproject.datalayer.store.MemoryStorageImpl;
import ru.fixapp.fooproject.domainlayer.providers.SchedulersProvider;
import ru.fixapp.fooproject.domainlayer.providers.SessionDataProvider;

/**
 * Created by Zahar on 24.03.16.
 */
@Module
@Singleton
public class ProvidersModule {

	@Singleton
	@Provides
	SessionDataProvider provideSessionDataProvider(LocalStorage localStorage,
												   MemoryStorage memoryStorage) {
		return new SessionDataProviderImpl(localStorage, memoryStorage);
	}

	@Singleton
	@Provides
	SchedulersProvider provideSchedulersProvider() {
		return new MainSchedulersProvider();
	}

	@Singleton
	@Provides
	@NonNull
	LocalStorage provideLocalStorage(Context context) {
		return new LocalStorageImpl(context);
	}

	@Singleton
	@Provides
	@NonNull
	MemoryStorage provideMemoryStorage() {
		return new MemoryStorageImpl();
	}

}
