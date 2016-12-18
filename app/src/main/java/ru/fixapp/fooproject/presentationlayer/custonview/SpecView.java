package ru.fixapp.fooproject.presentationlayer.custonview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class SpecView extends View {

	private float originalWidth;
	private float originalHeight;
	private Paint paint;

	private Random random;

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

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);


		int XN = 10;
		int YN = 255;

		float dX = originalWidth / XN;
		float dY = originalHeight / YN;

		float left = 0;
		float right = dX;


		for (int i = 0; i < XN; i++) {
			float top = 0;
			float bottom = dY;
			for (int j = 0; j < YN ; j++) {
				int col = j;
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
}
