package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.presentationlayer.formaters.RecordsFormat;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseVH;
import ru.fixapp.fooproject.presentationlayer.fragments.core.MySimpleBaseAdapter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordListVH extends BaseVH<AudioModel> {

	private final RecordsFormat recordsFormat;
	@BindView(R.id.record_title) TextView titleView;

	public RecordListVH(View view, MySimpleBaseAdapter.OnClickListener<AudioModel> onClick,
						MySimpleBaseAdapter.OnClickListener<AudioModel> onLongClick,
						RecordsFormat recordsFormat) {
		super(view, onClick);
		view.setOnLongClickListener(v -> {
			onLongClick.click((AudioModel) v.getTag());
			return true;
		});
		this.recordsFormat = recordsFormat;
	}

	@Override
	public void bind(AudioModel item) {
		titleView.setText(recordsFormat.format(item));
	}

}
