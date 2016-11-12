package ru.fixapp.fooproject.presentationlayer.activities;

import android.view.View;

import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.di.AppComponent;
import ru.fixapp.fooproject.di.IHasComponent;
import ru.fixapp.fooproject.di.modules.activity.CommonActivityModule;
import ru.fixapp.fooproject.di.modules.activity.DrawerModule;
import ru.fixapp.fooproject.di.modules.activity.ToolbarModule;
import ru.fixapp.fooproject.presentationlayer.app.App;


public class MainActivity extends BaseActivity implements IHasComponent<ActivityComponent> {

	private ActivityComponent screenComponent;

	@Override
	protected void injectDagger() {
		if (screenComponent != null) {
			return;
		}

		AppComponent appComponent = App.get(this).getAppComponent();
		View rootView = getRootView();
		CommonActivityModule commonActivityModule =
				new CommonActivityModule(this, this, rootView, getSupportFragmentManager(),
						getContainerID());

		screenComponent = DaggerActivityComponent.builder().appComponent(appComponent)
				.commonActivityModule(commonActivityModule)
				.toolbarModule(new ToolbarModule(rootView, this))
				.drawerModule(new DrawerModule(rootView)).build();
		screenComponent.inject(this);
	}

	protected int getActivityLayoutResourceID() {
		return R.layout.activity_main_app;
	}

	@Override
	public ActivityComponent getComponent() {
		injectDagger();
		return screenComponent;
	}
}
