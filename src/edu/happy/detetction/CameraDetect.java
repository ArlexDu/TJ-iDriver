package edu.happy.detetction;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import edu.happy.roadrecord.R;


public class CameraDetect extends Activity implements CvCameraViewListener2{

	
	//opencv 摄像头的view
	private CameraBridgeViewBase camera;
	private boolean mIsjavaCamera = true;
	//图片矩阵
	private Mat mrgb;
	//灰度图
	private Mat gray;
	//识别矩形
	private MatOfRect mDetection;
	//分类器
	private CascadeClassifier mDetector;
	//检测范围大小
	private int detectiveSize = 0;
	private float smaller = 0.1f;
	
	private String TAG="Detector";
	
	//每两秒检测一次
	private boolean getnew = true;
	
	//链接opencv
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		
		public void onManagerConnected(int status) {
//			Log.i(TAG, "init");
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:{
				try{
					FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/car.xml");
//					Log.i(TAG, "file path is " +Environment.getExternalStorageDirectory()+"/face.xml");
					InputStream in = getResources().getAssets().open("car.xml");
					byte[] buffer = new byte[8192];
					int count = 0;
					while((count = in.read(buffer))>=0){
						fos.write(buffer,0,count);
					}
					fos.close();
					in.close();
				//构造分类器
				String xmlfilePath = null;
				xmlfilePath = Environment.getExternalStorageDirectory()+"/car.xml";
				Log.i(TAG, "file path is " +xmlfilePath);
				mDetector = new CascadeClassifier(xmlfilePath);
				mDetection = new MatOfRect();
				camera.enableView();
				Log.i(TAG, "load successful");
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
//					Log.i(TAG, "wrong");
				}
				break;
			}
			default:
//				Log.i(TAG, "load failed");
				super.onManagerConnected(status);
				break;
			}
		};
	};
	
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			getnew = true;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camera_detect);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		camera = (CameraBridgeViewBase)findViewById(R.id.camera);
		camera.setVisibility(View.VISIBLE);
		camera.setCvCameraViewListener(this);
//		Log.i(TAG, "create");
	}
	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "mat");
		mrgb = new Mat(height,width,CvType.CV_8UC4);
	}
	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mrgb.release();
	}
	//相机每一帧的处理方案
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "frame");
		mrgb = inputFrame.rgba();
		gray = inputFrame.gray();
		if(detectiveSize == 0){
			int height = gray.rows();
			if(Math.round(height*smaller)>0){
				detectiveSize = Math.round(height*smaller);
			}
		}
		if(getnew){//需要计算新的位置
			new MyThread().start();
		}else{//直接沿用以前的位置不变
			
		}
		int num = 0;
		for(Rect rect:mDetection.toArray()){
			Core.rectangle(
					mrgb, 
					new Point(rect.x,rect.y),
					new Point(rect.x+rect.width,rect.y+rect.height),
					new Scalar(0,0,255));
			++num;
		}
		return mrgb;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		Log.i(TAG, "resume");
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
//		Log.i(TAG, "done");
	}
	
	class MyThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m = new Message();
			getnew  = false;
			mDetector.detectMultiScale(gray, mDetection, 1.1, 2, 2, new Size(detectiveSize,detectiveSize),
					new Size());
			if(!mDetection.empty()){
				Log.i(TAG, "get result");
				try{
					sleep(2000);
				}catch(Exception e){
					e.printStackTrace();
				}
				m.what = 1;
				
			}else{
				m.what = 2;
			}
			myHandler.sendMessage(m);	
		}
	}
}
