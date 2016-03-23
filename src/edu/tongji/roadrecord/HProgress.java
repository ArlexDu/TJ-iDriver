package edu.tongji.roadrecord;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class HProgress extends View {
	
	/**分段颜色*/
	 private static final int[] SECTION_COLORS = {Color.RED,Color.YELLOW,Color.GREEN};
	 /**进度条最大值*/
	 private float maxCount;
	 /**进度条当前值*/
	 private float currentCount;
	 /**画笔*/
	 private Paint mPaint;
	 private int mWidth,h_bar,mHeight;
     private int trangle_width = 0;
	 public HProgress(Context context, AttributeSet attrs,
	   int defStyleAttr) {
	  super(context, attrs, defStyleAttr);
	  initView(context);
	 }
	 public HProgress(Context context, AttributeSet attrs) {
	  super(context, attrs);
	  initView(context);
	 }
	 public HProgress(Context context) {
	  super(context);
	  initView(context);
	 }

	 private void initView(Context context) {
		 trangle_width = 50;
	 }
	 @Override
	 protected void onDraw(Canvas canvas) {
	  super.onDraw(canvas);
	  mPaint = new Paint();
	  mPaint.setAntiAlias(true);
	  
	  float round = (mWidth -trangle_width)/2;
//	  System.out.println("max="+maxCount + "  current="+currentCount);
	  mPaint.setColor(Color.rgb(71, 76, 80));
	  RectF rectBg = new RectF(0+trangle_width, 0+trangle_width, mWidth, h_bar);
	  canvas.drawRoundRect(rectBg, round, round, mPaint);
	  mPaint.setColor(Color.BLACK);
	  RectF rectBlackBg = new RectF(2+trangle_width, 2+trangle_width, mWidth-2, h_bar-2);
	  canvas.drawRoundRect(rectBlackBg, round, round, mPaint);

	  float section = 1-currentCount/maxCount;
	  RectF rectProgressBg = new RectF(3+trangle_width, (h_bar-3)*section+trangle_width,mWidth-3,h_bar-3);
	  if(section >= 2.0f/3.0f){//绿色
	   if(section != 1.0f){
	    mPaint.setColor(SECTION_COLORS[2]);
	   }else{
	    mPaint.setColor(Color.TRANSPARENT);
	   }
	  }else{
	   int count = (section >= 1.0f/3.0f ) ? 2 : 3;
	   int[] colors = new int[count];
//	        复制数组
	   System.arraycopy(SECTION_COLORS, 3-count, colors, 0, count);
	   float[] positions = new float[count];
	   //黄绿
	   if(count == 2){
	    positions[0] = 0.0f;
	    positions[1] = 1.0f;
	   }else{//红黄绿
	    positions[0] = 0.0f;
	    positions[1] = 1-(1.0f/2.0f)/(currentCount/maxCount);
//	    System.out.println("yellow is "+ positions[1]);
	    positions[2] = 1.0f;
	   }
	   positions[positions.length-1] = 1.0f;
	   LinearGradient shader = new LinearGradient(3+trangle_width, (h_bar-3)*section,mWidth-3,h_bar-3, colors,positions, Shader.TileMode.MIRROR);
	   mPaint.setShader(shader);
	  }
	  canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
	  Path path = new Path();
	  path.moveTo(0, (h_bar-3)*section);
	  path.lineTo(trangle_width, (h_bar-3)*section+trangle_width);
	  path.lineTo(0, 2*trangle_width+(h_bar-3)*section);
	  path.close();
	  canvas.drawPath(path, mPaint);
	 }

	 private int dipToPx(int dip) {
	  float scale = getContext().getResources().getDisplayMetrics().density;
	  return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	 }

	 /***
	  * 设置最大的进度值
	  * @param maxCount
	  */
	 public void setMaxCount(float maxCount) {
	  this.maxCount = maxCount;
	 }

	 /***
	  * 设置当前的进度值
	  * @param currentCount
	  */
	 public void setCurrentCount(float currentCount) {
	  this.currentCount = currentCount > maxCount ? maxCount : currentCount;
	  invalidate();
	 }

	 public float getMaxCount() {
	  return maxCount;
	 }

	 public float getCurrentCount() {
	  return currentCount;
	 }

//	 在画出view之前要调用这个函数用于计算当前控件的大小
	 @Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	  int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
	  int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
	  int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
	  int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
	  if (widthSpecMode == MeasureSpec.EXACTLY) {
	   mWidth = widthSpecSize;
	  } else {
	   mWidth = dipToPx(40);
	  }
	  if (heightSpecMode == MeasureSpec.EXACTLY) {
	   mHeight = dipToPx(200);
	   h_bar = dipToPx(180);
	  } else {
	   mHeight = heightSpecSize;
	  }
	  setMeasuredDimension(mWidth, mHeight);
	 }

}
