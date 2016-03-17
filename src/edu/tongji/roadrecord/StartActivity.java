package edu.tongji.roadrecord;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import edu.happy.detection.CameraDetect;
import edu.happy.roadrecord.MainActivity;
import edu.happy.roadrecord.R;

public class StartActivity extends Activity {

	private HProgress pro;
	private int current = 0;
	private ImageView select,cancel,map,camera,people;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		select = (ImageView)findViewById(R.id.select_button);
		cancel = (ImageView)findViewById(R.id.cancel_button);
		map = (ImageView)findViewById(R.id.map);
		camera = (ImageView)findViewById(R.id.camera);
		people = (ImageView)findViewById(R.id.people);
		initanim();
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
//			intent = new Intent(MainActivity.this,WriteActivity.class);
			initanim();
//			startActivity(intent);
			break;	
		}
	}
	
	 //初始化界面
	 public void initanim(){
				cancel.clearAnimation();
				cancel.invalidate();
				map.setVisibility(View.GONE);
				camera.setVisibility(View.GONE);
				people.setVisibility(View.GONE);
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
		map.setVisibility(View.GONE);
		camera.setVisibility(View.GONE);
		people.setVisibility(View.GONE);
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

	
}
