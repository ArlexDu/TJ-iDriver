package edu.tongji.roadrecord;


import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import edu.happy.detection.CameraDetect;
import edu.happy.roadrecord.MainActivity;
import edu.happy.roadrecord.R;
import edu.tongji.people.LoginActivity;
import edu.tongji.people.PeopleAnalyze;

public class StartActivity extends Activity implements OnGestureListener {

	private HProgress pro;
	private int current = 0;
	private ViewFlipper flipper;
	private boolean showNext = true;
	private boolean isRun = true;
	private int currentPage = 0;
	private final int SHOW_NEXT = 0011;
	private ImageView select,cancel,map,camera,people;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	private GestureDetector mGestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		select = (ImageView)findViewById(R.id.select_button);
		cancel = (ImageView)findViewById(R.id.cancel_button);
		map = (ImageView)findViewById(R.id.map);
		camera = (ImageView)findViewById(R.id.camera);
		people = (ImageView)findViewById(R.id.people);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		mGestureDetector = new GestureDetector(this);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		initanim();
		firstuse();
		displayRatio_selelct(currentPage);
		thread.start();
	}
	//仅第一次登陆才会打开数据库建立
	private void firstuse(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int first = preferences.getInt("open", 0);
//		System.out.println("open ："+first);
		SharedPreferences.Editor editor = preferences.edit();
		//第一次登陆  
		if(first == 0){
			int userid = -100;
			editor.putInt("userid", userid);
		}
		first++;
		editor.putInt("open", first);
		editor.commit();
		
	}
	public void onClick(View v){
		Intent intent;
		switch(v.getId()){
		//进入选择模式
		case R.id.select_button:
			chooseFunction();
			break;
		case R.id.cancel_button:
			cancelSelect();
			break;
		case R.id.map:
			intent = new Intent(StartActivity.this,MainActivity.class);
			initanim();
			startActivity(intent);
			break;	
		case R.id.camera:
			intent = new Intent(StartActivity.this,CameraDetect.class);
			initanim();
			startActivity(intent);
			break;	
		case R.id.people:
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			int uid = preferences.getInt("userid", -100);
			if(uid == -100){//需要登录或者注册
				intent = new Intent(StartActivity.this,LoginActivity.class);
			}else{
				intent = new Intent(StartActivity.this,PeopleAnalyze.class);
			}
			initanim();
			startActivity(intent);
			break;	
		}
	}
	
	 //初始化界面
	 public void initanim(){
				cancel.clearAnimation();
				cancel.invalidate();
				map.setVisibility(View.INVISIBLE);
				camera.setVisibility(View.INVISIBLE);
				people.setVisibility(View.INVISIBLE);
				cancel.setVisibility(View.INVISIBLE);
				select.setVisibility(View.VISIBLE);
			}
	 //选择操作的弹框
	 private void chooseFunction(){
				select.clearAnimation();
				select.invalidate();
				select.setVisibility(View.INVISIBLE);
				cancel.setVisibility(View.VISIBLE);
				cancel.bringToFront();
			    Animation rotateanimation=AnimationUtils.loadAnimation(StartActivity.this, R.anim.select_rotate);
				rotateanimation.setFillAfter(true);
				cancel.startAnimation(rotateanimation);
				rotateanimation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						map.setVisibility(View.VISIBLE);
						camera.setVisibility(View.VISIBLE);
						people.setVisibility(View.VISIBLE);
					}
				});
				
			}
	
	//取消选择的动画
	public void cancelSelect(){
		cancel.clearAnimation();
		cancel.invalidate();
		map.setVisibility(View.INVISIBLE);
		camera.setVisibility(View.INVISIBLE);
		people.setVisibility(View.INVISIBLE);
		cancel.setVisibility(View.INVISIBLE);
		select.setVisibility(View.VISIBLE);
		select.bringToFront();
		Animation rotateanimation=AnimationUtils.loadAnimation(StartActivity.this, R.anim.cancel_rotate);
		rotateanimation.setFillAfter(true);
		rotateanimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub	
			    Animation animation=AnimationUtils.loadAnimation(StartActivity.this, R.anim.black_cancel);
//				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
					}
				});
			}
		});
		select.startAnimation(rotateanimation);
	}
	Thread thread = new Thread(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRun){
				try {
					Thread.sleep(1000 * 5);
					Message msg = new Message();
					msg.what = SHOW_NEXT;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	};
	
	   Handler mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case SHOW_NEXT:
					if (showNext) {
						showNextView();
					} else {
						showPreviousView();
					}
					break;

				default:
					break;
				}
			}
	    	
	    };
	private void showNextView(){

		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));		
		flipper.showNext();
		currentPage ++;
		if (currentPage == flipper.getChildCount()) {
			displayRatio_normal(currentPage - 1);
			currentPage = 0;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage - 1);
		}
		Log.e("currentPage", currentPage + "");		
		
	}
	private void showPreviousView(){
		displayRatio_selelct(currentPage);
		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		flipper.showPrevious();
		currentPage --;
		if (currentPage == -1) {
			displayRatio_normal(currentPage + 1);
			currentPage = flipper.getChildCount() - 1;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage + 1);
		}
		Log.e("currentPage", currentPage + "");		
	}
	private void displayRatio_selelct(int id){
		int[] ratioId = { R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)findViewById(ratioId[id]);
		img.setSelected(true);
	}
	private void displayRatio_normal(int id){
		int[] ratioId = { R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)findViewById(ratioId[id]);
		img.setSelected(false);
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	return this.mGestureDetector.onTouchEvent(event);
    }
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		Log.e("view", "onFling");
		if (e1.getX() - e2.getX()> FLING_MIN_DISTANCE  
                && Math.abs(velocityX) > FLING_MIN_VELOCITY ) {
			Log.e("fling", "left");
			showNextView();
			showNext = true;
//			return true;
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE  
                && Math.abs(velocityX) > FLING_MIN_VELOCITY){
			Log.e("fling", "right");
			showPreviousView();
			showNext = false;
//			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
