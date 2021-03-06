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
import ru.fixapp.fooproject.domainlayer.fft.MinMaxModel;
import ru.fixapp.fooproject.domainlayer.fft.SignalFeature;

public class SpecView extends View {

	private boolean fft = false;


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
					Arrays.asList(new SignalFeature(new double[]{ 51, 14,0,12, 85, 16, 99,2,100}, new double[]{-45,12,5,-50,10}));
			model = new FFTModel(signalFeatures, new MinMaxModel(0, 100),new MinMaxModel(-50, 10));
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
		SignalFeature signalFeature = list.get(0);
		double[] fftCeps = getDoubles(signalFeature);
		int YN = fftCeps.length;

		float dX = originalWidth / XN;
		float dY = originalHeight / YN;

		float left = 0;
		float right = dX;

		MinMaxModel minMaxModel;
		if (fft) {
			minMaxModel = model.getFft();
		}else{
			minMaxModel = model.getMfcc();
		}

		double dif = minMaxModel.getMax() - minMaxModel.getMin();
		double modelMin = minMaxModel.getMin();
		for (int i = 0; i < XN; i++) {
			float top = 0;
			float bottom = dY;
			double[] doubles;
			doubles = getDoubles(list.get(i));
			for (int j = YN - 1; 0 <= j; j--) {
				int i2 = 30;
				int i1 = 382- i2;
				int col = (int) ((doubles[j] - modelMin) / dif * i1);

				if (col != 0) {
					col += i2;
					if (col <= 255) {
						paint.setColor(Color.argb(col, 0, 255, 0));
					} else {
						paint.setColor(Color.argb(col - 127, 255, 0, 0));
					}
					canvas.drawRect(left, top, right, bottom, paint);
				}

				top += dY;
				bottom += dY;
			}
			left += dX;
			right += dX;
		}

	}

	private double[] getDoubles(SignalFeature signalFeature) {
		double[] doubles;
		if (fft){
			doubles = signalFeature.getFftCeps();
		}else{
			doubles = signalFeature.getMelCeps();
		}
		return doubles;
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
