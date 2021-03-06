package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;
import ru.fixapp.fooproject.presentationlayer.custonview.SpecView;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseFragment;

public class SignalinfoScreenFragment extends BaseFragment<SignalinfoPresenter>
		implements SignalinfoView {

	private static final String PATH = "path";

	@Inject SignalinfoPresenterCache cache;

	@BindView(R.id.specView) SpecView specView;
	@BindView(R.id.recording_audio_visualizerview) LineChart lineChart;


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
	public void drawSpectr(FFTModel model) {
		specView.setModel(model);
	}

	@Override
	public void updateVisualizer(List<Entry> entries) {

		LineDataSet dataSet = new LineDataSet(entries, "Label");
		dataSet.setColor(Color.BLUE);
		dataSet.setValueTextColor(Color.BLACK);
		dataSet.setDrawCircles(false);
		dataSet.setDrawCircleHole(false);
		dataSet.setDrawHorizontalHighlightIndicator(false);

		lineChart.getLegend()
				.setEnabled(false);
		Description desc = new Description();
		desc.setText("");
		lineChart.setDescription(desc);
		lineChart.setDrawGridBackground(false);
		lineChart.setVerticalScrollBarEnabled(false);
		lineChart.setHorizontalScrollBarEnabled(false);


		Highlight h1 = new Highlight(0, 0, 1);
		Highlight h2 = new Highlight(0, 0, 2);
		lineChart.highlightValues(new Highlight[]{h1, h2});
		lineChart.setHighlightPerDragEnabled(false);


		LineData lineData = new LineData(dataSet);
		lineChart.setData(lineData);
		lineChart.invalidate();
	}
}