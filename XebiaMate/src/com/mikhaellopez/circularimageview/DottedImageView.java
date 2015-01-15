package com.mikhaellopez.circularimageview;

import com.android.vantage.R;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DottedImageView extends ImageView {
	private Paint paint;
	private long circleCount=0;

	public DottedImageView(Context context) {
		super(context);
		initPaint();
	}

	private void initPaint() {
		paint = new Paint(); // define paint and paint color
		paint.setColor(getResources().getColor(R.color.dot_color));
		paint.setStyle(Style.FILL_AND_STROKE);
		// paint.setAntiAlias(true);
		// Logger.info("DottedCircleImage", "Width :" + this.getWidth()
		// + ", Height:" + this.getHeight());
		// TODO Auto-generated constructor stub
	}

	public DottedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public DottedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	public void setCircleCount(long circleCount) {
		this.circleCount = circleCount;
		invalidate();
		// set by default to 5
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Logger.info("DottedCircleImage", "Width :" + this.getWidth()
				+ ", Height:" + this.getHeight());
		for (int i = 0; i < circleCount; i++) {

			int randomX = (int) Util.randInt(0, this.getWidth());
			int randomY = (int) Util.randInt(0, this.getHeight());
			Logger.info("DottedCircleImage", "Random X :" + randomX
					+ ", RandomY:" + randomY);
			canvas.drawCircle(randomX, randomY, 10, paint);
		}

	}

}
