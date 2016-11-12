package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseVH;
import ru.fixapp.fooproject.presentationlayer.fragments.core.MySimpleBaseAdapter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordListVH extends BaseVH<AudioModel> {

	@BindView(R.id.record_title) TextView titleView;

	public RecordListVH(View view, MySimpleBaseAdapter.OnClickListener<AudioModel> onClick,
						MySimpleBaseAdapter.OnClickListener<AudioModel> onLongClick) {
		super(view, onClick);
		view.setOnLongClickListener(v -> {
			onLongClick.click((AudioModel) v.getTag());
			return true;
		});
	}

	@Override
	public void bind(AudioModel item) {
		String name = item.getName();
		String type = item.getType();
		float v = item.getDuration() / 1000f;
		String format = String.format(Locale.getDefault(), "Длительность %.1fc", v);
		String format2 = String.format("Размер %s кб", String.valueOf(item.getSize() / 1024));
		titleView.setText(name + '\n' + type + '\n' + format + '\n' + format2);
	}
}
