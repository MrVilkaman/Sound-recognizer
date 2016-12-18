package ru.fixapp.fooproject.presentationlayer.custonview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.domainlayer.fft.SignalFeature;

public class SpecView extends View {

	private float originalWidth;
	private float originalHeight;
	private Paint paint;

	private Random random;
	private FFTModel model;

	public SpecView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SpecView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {

		random = new Random();
		paint = new Paint();


		if (isInEditMode()) {
			List<SignalFeature> signalFeatures =
					Arrays.asList(new SignalFeature(new double[]{51, 0, 14, 12, 85, 16, 99}, null));
			model = new FFTModel(signalFeatures,0,100);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);


		if (model == null) {
			return;
		}

		List<SignalFeature> list = model.getList();
		int XN = list.size();
		if (XN == 0) {
			return;
		}
		int YN = list.get(0).getFftCeps().length;

		float dX = originalWidth / XN;
		float dY = originalHeight / YN;

		float left = 0;
		float right = dX;

		double dif = model.getMax() - model.getMin();
		double modelMin = model.getMin();
		for (int i = 0; i < XN; i++) {
			float top = 0;
			float bottom = dY;
			double[] doubles = list.get(i).getFftCeps();
			for (int j = YN - 1; 0 <= j; j--) {
				int col = (int) ((doubles[j] - modelMin)/ dif *255);

				paint.setColor(Color.rgb(col, 255-col, 0));
				canvas.drawRect(left, top, right, bottom, paint);
				top += dY;
				bottom += dY;
			}
			left += dX;
			right += dX;
		}

	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		originalWidth = MeasureSpec.getSize(widthMeasureSpec);
		originalHeight = MeasureSpec.getSize(heightMeasureSpec);

		super.onMeasure(MeasureSpec.makeMeasureSpec((int) originalWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec((int) originalHeight, MeasureSpec.EXACTLY));
	}

	public void setModel(FFTModel model) {
		this.model = model;
		invalidate();
	}
}
