package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseFragment;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordListScreenFragment extends BaseFragment<RecordListPresenter> implements RecordListView {

	@BindView(R.id.recyclerview) RecyclerView recyclerView;
	@Inject RecordListAdapter adapter;

	public static RecordListScreenFragment open() {
		return new RecordListScreenFragment();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_listsamplescreen_fragment;
	}

	@Override
	protected void onCreateView(View view, Bundle savedInstanceState) {
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		adapter.setOnClick(category -> getPresenter().openRecordDetail(category.getAbsolutePath()));
		adapter.setOnLongClick(category -> getPresenter().deleteFile(category));
	}

	@Override
	public void delete(AudioModel audioModel) {
		adapter.remove(audioModel);
	}

	@Override
	public void openRecordDetail(String absolutePath) {
//		getNavigation().showFragment(RecordingScreenFragment.open(absolutePath));
	}
	@Override
	public void daggerInject(ActivityComponent component) {
		DaggerRecordListScreenComponent.builder()
				.activityComponent(component)
				.build()
				.inject(this);
	}

	@Override
	public void bind(List<AudioModel> items) {
		adapter.setItems(items);
	}

	@OnClick(R.id.list_fab)
	void onClickAdd(){
//		getNavigation().showFragment(RecordingScreenFragment.create());
	}
}