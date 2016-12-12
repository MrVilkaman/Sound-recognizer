package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseFragment;

public class RecordingScreenFragment extends BaseFragment<RecordingPresenter>
		implements RecordingView {

	private static final String PATH = "extra_path";

	@Inject RecordingPresenterCache cache;

	@BindView(R.id.recording_audio_info) TextView textView;
	@BindView(R.id.recording_audio_info_2) TextView textView2;
	@BindView(R.id.recording_record) View recordButton;
	@BindView(R.id.recording_play) Button playButton;
	@BindView(R.id.recording_audio_visualizerview) LineChart lineChart;

	public static RecordingScreenFragment create() {
		return new RecordingScreenFragment();
	}

	public static RecordingScreenFragment open(String absolutePath) {
		RecordingScreenFragment recordingScreenFragment = new RecordingScreenFragment();
		Bundle args = new Bundle();
		args.putString(PATH, absolutePath);
		recordingScreenFragment.setArguments(args);
		return recordingScreenFragment;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.layout_recordingscreen_fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		getToolbar().hide();
		// dirty hack!! need more flexible way!
		cache.restoreState(savedInstanceState);
		Bundle arguments = getArguments();
		if (arguments != null) {
			cache.setCurrentPath(arguments.getString(PATH));
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		cache.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreateView(View view, Bundle savedInstanceState) {
	}

	@Override
	public void hideRecordButton() {
		recordButton.setVisibility(View.GONE);
	}

	@Override
	public void showAudioInfo(String textAudio) {
		textView.setText(textAudio);
	}

	@OnClick(R.id.recording_play)
	void onClickPlay() {
		getPresenter().playLastAudio();
	}

	@OnClick(R.id.recording_cut)
	void onClickCut() {
		getPresenter().cutAudio();
	}

	@OnTouch(R.id.recording_record)
	boolean onTouchRecord(View view, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				getPresenter().stopRecording();
				break;
			case MotionEvent.ACTION_DOWN:
				getPresenter().startRecording();
				break;
			default:

		}
		return false;
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


		lineChart.getXAxis()
				.setValueFormatter((value, axis) -> {
					float v = 1000f * axis.mAxisMaximum / cache.getDuraction();

					return String.format("%.2f", value / v);
				});

		lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, Highlight h) {
				// TODO: 07.12.16 !
				List<ILineDataSet> dataSets = lineChart.getLineData().getDataSets();
				float x = e.getX();
				ILineDataSet iLineDataSet = dataSets.get(0);
				Entry entryForXPos = iLineDataSet.getEntryForIndex((int) x);

				getPresenter().setNextTimePoint((long)entryForXPos.getX());
			}

			@Override
			public void onNothingSelected() {
			}
		});

		Highlight h1 = new Highlight(0, 0, 1);
		Highlight h2 = new Highlight(0, 0, 2);
		lineChart.highlightValues(new Highlight[]{h1, h2});


		LineData lineData = new LineData(dataSet);
		lineChart.setData(lineData);
		lineChart.invalidate();
	}

	@Override
	public void setRangeTime(String text) {
		textView2.setText(text);
	}

	@Override
	public void showPlayBtn() {
		cache.setNowInPlay(false);
		playButton.setText(R.string.record_play);
	}

	@Override
	public void showPauseBtn() {
		cache.setNowInPlay(true);
		playButton.setText(R.string.record_stop);
	}

	@Override
	public void daggerInject(ActivityComponent component) {
		DaggerRecordingScreenComponent.builder()
				.activityComponent(component)
				.build()
				.inject(this);
	}
}