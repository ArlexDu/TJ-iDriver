package edu.tongji.people;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class PinChart extends View {

	static Canvas c;
	private Paint[] mPaints;
	private RectF mBigOval;
	float[] mSweep = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int preWidth;
	private mAnimation ani;
	private int centerX;
	private int centerY;
	int valueX;
	int valueY;

	public static float[] humidity = { 110, 60, 50, 50, 40, 30, 10, 10 };
	private String str[] = { "24%", "19%", "21%", "18%", "3%", "3%", "4%", "6%" };
    private String name[] = {"0km~40km","40km~50km","50km~60km","60km~70km","70km~80km","80km~90km","90km~100km","100km~"};
	private final String color[] = { "#2cbae7", "#ffa500", "#ff5b3b", "#9fa0a4", "#6a71e5", "#f83f5d", "#64a300",
			"#64ef85" };

	public PinChart(Context context) {
		super(context);
		initView();
	}

	public PinChart(Context context, AttributeSet atr) {
		super(context, atr);
		initView();
	}

	private void initView() {
		ani = new mAnimation();
		ani.setDuration(2000);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);// 设置背景颜色(透明)
		mPaints = new Paint[humidity.length];

		// 设置不同区域的不同颜色
		for (int i = 0; i < humidity.length; i++) {
			mPaints[i] = new Paint();
			mPaints[i].setAntiAlias(true);
			mPaints[i].setStyle(Paint.Style.FILL);
			mPaints[i].setColor(Color.parseColor(color[i]));
		}
		// getWidth函数获得的是px
		int cicleWidth = getWidth() - dp2px(60);
		centerX = getWidth() / 2;
//		底部的长方形的说明高度是30px，加上底部
		centerY = (getHeight()-dp2px(60))>(getWidth()-dp2px(60))?((getHeight()-dp2px(60))/2):(getWidth() / 2+dp2px(30));
//		提示的长方形块
		preWidth = (getWidth() - dp2px(40)) / 4;
		int half = getWidth() / 2;
		int circle_radius = cicleWidth/2;

		mBigOval = new RectF();// 饼图的四周边界
		mBigOval.top = centerY-circle_radius;
		mBigOval.left = half - cicleWidth / 2;
		mBigOval.bottom = centerY+circle_radius;
		mBigOval.right = half + cicleWidth / 2;

		float start = -180;
		Rect bounds = new Rect();
		for (int i = 0; i < humidity.length; i++) {
			/*
			 * canvas.drawarc的具体参数
			 * public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint) 
			 * oval :指定圆弧的外轮廓矩形区域。
			 * startAngle: 圆弧起始角度，单位为度。
			 * sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。 
			 * useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
			 *  paint:绘制圆弧的画板属性，如颜色，是否填充等。
			 */
//			绘制扇形区域
			canvas.drawArc(mBigOval, start, mSweep[i], true, mPaints[i]);
//			加入文字描述
//			if (humidity[i] > 45) {
//				正常绘制，上下层绘制叠盖
				mPaints[i].setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
				mPaints[i].setAntiAlias(true);
				mPaints[i].setColor(Color.WHITE);
				mPaints[i].getTextBounds(str[i], 0, str[i].length(), bounds);
				mPaints[i].setTextSize(sp2px(15));
				measureText(start + 180, humidity[i], cicleWidth / 2.5f, i);
				canvas.drawText(str[i], valueX - mPaints[i].measureText(str[i]) / 2, valueY + bounds.height() / 2,
						mPaints[i]);
//			}
			start += humidity[i];
			int j = 1;
			int k;
			if (i < 4) {
				j = 0;
				k = i;
			} else {
				j = 1;
				k = i - 4;
			}
			mPaints[i] = new Paint();
			mPaints[i].setAntiAlias(true);
			mPaints[i].setStyle(Paint.Style.FILL);
			mPaints[i].setColor(Color.parseColor(color[i]));
			canvas.drawRect(new RectF(dp2px(20) + preWidth * k, centerY+circle_radius+ dp2px(j * 30 + 20),
					dp2px(20) + preWidth * (k + 1), centerY+circle_radius+ dp2px(50 + j * 30)), mPaints[i]);
			mPaints[i].setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
			mPaints[i].setAntiAlias(true);
			mPaints[i].setColor(Color.WHITE);
			mPaints[i].getTextBounds(name[i], 0, name[i].length(), bounds);
			mPaints[i].setTextSize(sp2px(12));
			canvas.drawText(name[i], dp2px(20) + preWidth * k + preWidth / 2 - mPaints[i].measureText(name[i]) / 2,
					centerY+circle_radius+ dp2px(j * 30 + 20) + (dp2px(30) / 2 + bounds.height() / 2), mPaints[i]);
		}
	}

	/**
	 * 显示相应区域字开始的x,y坐标
	 * 
	 * @param start
	 * @param angle
	 * @param radius
	 * @param i
	 */
	private void measureText(float start, float angle, float radius, int i) {
		float temp = start + (angle / 2);

		if (temp < 90) {
			valueX = (int) (centerX - Math.abs(radius * Math.sin((temp / 180) * Math.PI)));
			valueY = (int) (centerY - Math.abs(radius * Math.cos((temp / 180) * Math.PI)));
		} else if (temp > 90 && temp < 180) {
			temp = 180 - temp;
			valueX = centerX + (int) Math.abs((radius * Math.cos((temp / 180) * Math.PI)));
			valueY = centerY - (int) Math.abs((radius * Math.sin((temp / 180) * Math.PI)));
		} else if (temp > 180 && temp < 270) {
			temp = temp - 180;
			valueX = centerX + (int) Math.abs((radius * Math.cos((temp / 180) * Math.PI)));
			valueY = centerY + (int) Math.abs((radius * Math.sin((temp / 180) * Math.PI)));
		} else {
			temp = 360 - temp;
			valueX = centerX - (int) Math.abs((radius * Math.cos((temp / 180) * Math.PI)));
			valueY = centerY + (int) Math.abs((radius * Math.sin((temp / 180) * Math.PI)));
		}

	}

	private int sp2px(int value) {
		float v = getResources().getDisplayMetrics().scaledDensity;
		return (int) (value * v + 0.5f);
	}

	// dp转化为px
	private int dp2px(int value) {
		float v = getResources().getDisplayMetrics().density;
		return (int) (value * v + 0.5f);
	}

	public void start() {
		startAnimation(ani);
	}

	class mAnimation extends Animation {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			for (int i = 0; i < humidity.length; i++) {
				mSweep[i] = humidity[i] * interpolatedTime;
			}
			invalidate();
		}
	}

}