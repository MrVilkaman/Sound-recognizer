package ru.fixapp.fooproject.presentationlayer.fragments.recording;


import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
	@BindView(R.id.recording_audio_visualizerview) LineChart lineChart;

	private Visualizer mVisualizer;

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
//		visualizerView.setEnabled(true);
		// Create the Visualizer object and attach it to our media player.
		mVisualizer = new Visualizer(audioSessionId);
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		mVisualizer.setDataCaptureListener(
				new Visualizer.OnDataCaptureListener() {
					@Override
					public void onWaveFormDataCapture(Visualizer visualizer,
													  byte[] bytes, int samplingRate) {
//						visualizerView.updateVisualizer(bytes);
					}

					@Override
					public void onFftDataCapture(Visualizer visualizer,
												 byte[] bytes, int samplingRate) {
					}
				}, Visualizer.getMaxCaptureRate() / 2, true, false);
	}

	@Override
	public void showViz() {
//		visualizerView.setEnabled(false);


	}

	@Override
	public void showBytes(String absolutePath) {

		if (absolutePath == null || absolutePath.isEmpty()) {
			return;
		}

		int bytesRead;

		try {
			InputStream is = new FileInputStream(absolutePath);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[2048];
			while ((bytesRead = is.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			byte[] bytes = bos.toByteArray();
			byte[] bytes1 = Arrays.copyOfRange(bytes, 3930, bytes.length);
			updateVisualizer(bytes1);
		} catch (Exception e) {
			handleError(e);
		}
	}

	private void updateVisualizer(byte[] bytes) {

		List<Entry> entries = new ArrayList<>();
		for (int i = 0; i < bytes.length; i++) {
			entries.add(new Entry(i, bytes[i]));
		}

		LineDataSet dataSet = new LineDataSet(entries, "Label");
		dataSet.setColor(Color.BLUE);
		dataSet.setValueTextColor(Color.BLACK);
		dataSet.setDrawCircles(false);
		dataSet.setDrawCircleHole(false);
		dataSet.setDrawHorizontalHighlightIndicator(false);

		lineChart.getLegend().setEnabled(false);
		Description desc = new Description();
		desc.setText("");
		lineChart.setDescription(desc);
		lineChart.setDrawGridBackground(false);


		lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
			@Override
			public String getFormattedValue(float value, AxisBase axis) {
				float v = 1000f * axis.mAxisMaximum / cache.getDuraction();
//				IAudioRecorderInteractor.SAMPLING_RATE
				return String.format("%.2f",value/v);
			}
		});


		LineData lineData = new LineData(dataSet);
		lineChart.setData(lineData);
		lineChart.invalidate();
	}

	@Override
	public void daggerInject(ActivityComponent component) {
		DaggerRecordingScreenComponent.builder()
				.activityComponent(component)
				.build()
				.inject(this);
	}
}