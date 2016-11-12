package ru.fixapp.fooproject.di.modules.activity;


import android.support.v7.app.AppCompatActivity;
import android.view.View;

import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.di.PerActivity;
import ru.fixapp.fooproject.presentationlayer.resolution.toolbar.IToolbar;
import ru.fixapp.fooproject.presentationlayer.resolution.toolbar.ToolbarMenuHelper;
import ru.fixapp.fooproject.presentationlayer.resolution.toolbar.ToolbarResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.toolbar.ToolbarResolverImpl;

@Module
public class ToolbarModule {
	private View view;
	private AppCompatActivity activity;

	public ToolbarModule(View view, AppCompatActivity activity) {
		this.view = view;
		this.activity = activity;
	}

	@Provides
	@PerActivity
	public ToolbarResolver getToolbarResolver(ToolbarMenuHelper menuHelper) {
		return new ToolbarResolverImpl(menuHelper);
	}

	@Provides
	@PerActivity
	public ToolbarMenuHelper createToolbarMenuHelper() {
		return new ToolbarMenuHelper(() -> activity.invalidateOptionsMenu());
	}

	@Provides
	@PerActivity
	public IToolbar getToolbar(ToolbarResolver resolver) {
		return resolver;
	}
}
