package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.di.PerScreen;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;

@PerScreen
@Component(dependencies = ActivityComponent.class, modules = {RecordListScreenComponent.ListSampleScreenModule.class})
public interface RecordListScreenComponent {
	void inject(RecordListScreenFragment fragment);

	@Module
	@PerScreen
	class ListSampleScreenModule {

		@PerScreen
		@Provides
		RecordListAdapter provideRecordListAdapter(){
			return new RecordListAdapter();
		}
	}
}