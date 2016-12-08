package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.presentationlayer.activities.ActivityComponent;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseFragment;

public class RecordingScreenFragment extends BaseFragment<RecordingPresenter>
		implements RecordingView {

	private static final String PATH = "extra_path";

	@Inject RecordingPresenterCache cache;

	@BindView(R.id.recording_audio_info) TextView textView;
	@BindView(R.id.recording_audio_info_2) TextView textView2;
	@BindView(R.id.recording_record) View recordButton;
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
	public void setupVisualizerFxAndUI(int audioSessionId) {
	}

	@Override
	public void showViz() {
	}

	@Override
	public void showBytes(String absolutePath) {

		if (absolutePath == null || absolutePath.isEmpty()) {
			return;
		}

		int bytesRead;

		try {
			InputStream is = new FileInputStream(absolutePath);
			byte[] b = new byte[8192];
			ByteArrayOutputStream bos = new ByteArrayOutputStream(65536);
			while ((bytesRead = is.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			byte[] bytes = bos.toByteArray();
//			byte[] bytes1 = Arrays.copyOfRange(bytes, 3930, bytes.length);
			updateVisualizer(bytes);
		} catch (Exception e) {
			handleError(e);
		}
	}

	@Inject AudioSettings audioSettings;
	private void updateVisualizer(byte[] bytes) {




		List<Entry> entries = new ArrayList<>();
		if (audioSettings.isPCM16BIT()) {
			for (int i = 0; i < bytes.length / 2; i++) {
				int lB = bytes[i * 2] & 0xff;
				int rB = bytes[i * 2 + 1] << 8;
				short sample = (short) (lB | rB);
				entries.add(new Entry(i, sample));
			}
		}else {
			for (int i = 0; i < bytes.length ; i++) {
				entries.add(new Entry(i, bytes[i]));
			}
		}


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
				uiResolver.showToast(R.string.simple_text,entryForXPos.getX());

				float v = 1000f *  iLineDataSet.getXMax() / cache.getDuraction();
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
	public void daggerInject(ActivityComponent component) {
		DaggerRecordingScreenComponent.builder()
				.activityComponent(component)
				.build()
				.inject(this);
	}
}