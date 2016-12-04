package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;

import android.support.v7.util.DiffUtil;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.presentationlayer.formaters.RecordsFormat;
import ru.fixapp.fooproject.presentationlayer.fragments.core.MySimpleBaseAdapter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordListAdapter extends MySimpleBaseAdapter<AudioModel,RecordListVH> {
	private OnClickListener<AudioModel> onLongClick;
	private RecordsFormat recordsFormat;

	@Inject
	public RecordListAdapter(RecordsFormat recordsFormat) {
		this.recordsFormat = recordsFormat;
	}

	@Override
	protected RecordListVH getHolder(View view, OnClickListener<AudioModel> onClick) {
		return new RecordListVH(view, onClick,onLongClick, recordsFormat);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_record_list_view;
	}

	@Override
	protected DiffUtil.Callback getDiffCallback(List<AudioModel> oldItems,
												List<AudioModel> newItems) {
		return null;
	}

	public void setOnLongClick(
			OnClickListener<AudioModel> onLongClick) {
		this.onLongClick = onLongClick;
	}

	public void remove(AudioModel audioModel) {
		int i = items.indexOf(audioModel);
		items.remove(i);
		notifyItemRemoved(i);

	}
}
