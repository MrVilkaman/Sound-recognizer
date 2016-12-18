package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;
import ru.fixapp.fooproject.presentationlayer.custonview.SpecView;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseFragment;

public class SignalinfoScreenFragment extends BaseFragment<SignalinfoPresenter>
		implements SignalinfoView {

	private static final String PATH = "path";

	@Inject SignalinfoPresenterCache cache;

	@BindView(R.id.specView) SpecView specView;


	public static SignalinfoScreenFragment open(String absolutePath) {
		SignalinfoScreenFragment fragment = new SignalinfoScreenFragment();
		Bundle args = new Bundle();
		args.putString(PATH, absolutePath);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_signalinfoscreen_fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		cache.setPath(getArguments().getString(PATH));
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void onCreateView(View view, Bundle savedInstanceState) {
		getToolbar().hide();
	}

	@Override
	public void daggerInject(ActivityComponent component) {
		DaggerSignalinfoScreenComponent.builder()
				.activityComponent(component)
				.build()
				.inject(this);
	}

	@Override
	public void updateVisualizer(List<Entry> entries) {

	}
}