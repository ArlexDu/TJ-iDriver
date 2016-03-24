package edu.happy.detection;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
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

import android.R.integer;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import edu.happy.roadrecord.R;
import edu.tongji.people.NetWorkAccess;
import edu.tongji.roadrecord.HProgress;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


import edu.happy.detection.MyOrientationListener.OnOrientationListener;
import edu.happy.roadrecord.R;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;




public class CameraDetect extends Activity implements CvCameraViewListener2{
	
	//添加
	
    private Context context;
    private static final String TAGA = "ACC";  
    private static final String TAGG = "LOCAT";
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
     
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongtitude;
    private double mSpeed;
    
    private NetWorkAccess access;
    //自定义图标

    private MyOrientationListener myOrientationListener;
    private float mOrient; 
    private double mCurrentX;
    private double mCurrentY;
    private double mCurrentZ;
    private int mdistance;
    private String address;
    
	private double v1;
	private double v2;
	private double v3;
	
	private double s1;
	private double s2;	
	private double s3;
	
	private double t1;
	private double t2;
	private double t3;
	private int count = 0;
	
	private double totalA;
	private double totalB;
	private double totalC;
	private double totalD;
	
	//opencv 摄像头的view
	private MyCamera camera;
	private boolean mIsjavaCamera = true;
	//图片矩阵
	private Mat mrgb;
	//灰度图
	private Mat gray;
	//感兴趣区域
	private Mat roi;
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
	
	private DistanceTracker tracker;
	
	private TextView showdistance;
	
	
	//提示条
	private HProgress progress;
	static {
		if(!OpenCVLoader.initDebug()){
			System.out.println("opencv 初始化失败！");
		}else{
			System.loadLibrary("opencv_java");
			System.loadLibrary("tracker");
		}
	}
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
	//			Log.i(TAG, "file path is " +xmlfilePath);
				mDetector = new CascadeClassifier(xmlfilePath);
				mDetection = new MatOfRect();
				camera.enableView();
				tracker  = new DistanceTracker();
	//			Log.i(TAG, "load successful");
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
			switch (msg.what) {
			case 0:
				System.out.println("传送成功:" +msg.obj);
				break;
			case 1:
				getnew = true;
				break;
			default:
				mdistance =Integer.valueOf( msg.obj.toString());
				showdistance.setText(msg.obj+"米");
				if(count == 0){
					v1 = mSpeed;
					s1 = Integer.valueOf(msg.obj.toString());
					t1 = System.currentTimeMillis();
				}else if(count == 1){
					v2 = mSpeed;
					s2 = Integer.valueOf(msg.obj.toString());
					t2 = System.currentTimeMillis();
				}
				else  if(count == 2)
				{
					v3 = mSpeed;
					s3 = Integer.valueOf(msg.obj.toString());
					t3 = System.currentTimeMillis();
					
					totalA = (-1) * guanhuacount(v1, v2, v3, s1, s2, s3, t1, t2, t3);
				}
				else{
					
					v1 = v2;
					s1 = s2;
					t1 = t2;
					
					v2 = v3;
					s2 = s3;
					t2 = t3;
					
					v3 = mSpeed;
					s3 = Integer.valueOf(msg.obj.toString());
					t3 = System.currentTimeMillis();
					
					totalA = (-1) * guanhuacount(v1, v2, v3, s1, s2, s3, t1, t2, t3);
					totalB = (s3 - s2)/(t3 -t2)*1.2 + 0.5 * totalA * 1.44;
					double aerfa =  Math.max(mCurrentZ,totalB);
					totalC =  (v3 * v3 / aerfa - (v3 - (s3 - s2)/(t3 -t2))/aerfa)*0.5 +v3 *1.2 +5;
					if(totalC == totalB){
						totalD = 1000000000;
					}
					else{
					totalD = (s3 - totalB)/(totalC -totalB);
					}
					float result = (float) (1 - Math.min(1,totalD));
	//				Log.i("BBB", "mm"+totalD);
					progress.setCurrentCount(result);
				}
				
//				lastTime = System.currentTimeMillis();
//                setDistances(Integer.valueOf(msg.obj.toString()));	
                
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CameraDetect.this);
				final String userid = sharedPreferences.getString("userid", "-100");
//				System.out.println("user id is "+ userid);
				if(!userid.equals("-100")){
					// TODO Auto-generated method stub
					String url = "/andinfor.php";
					new Thread(new Runnable() {
						public void run() {
							Map<String, String> map = new HashMap<String, String>();
							map.put("userid", userid);
							map.put("latitude", String.valueOf(mLatitude));
							map.put("longtitude", String.valueOf(mLongtitude));
							map.put("xa",String.valueOf(mCurrentX));
							map.put("ya",String.valueOf(mCurrentY));
							map.put("za",String.valueOf(mCurrentZ));
							map.put("mspeed", String.valueOf(mSpeed*3.6));
							map.put("distance", String.valueOf(mdistance));
							map.put("andress",String.valueOf(address));
							map.put("w",String.valueOf(totalD));
							access.ChangeInfo("/andinfor.php", myHandler, 0, map);
						}
					}).start();
				}
				count++;
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		//添加
		
	        
	     
	      
	        //获取地图控件引用  
	        this.context = this;
	       
	        
		
		//
		
		//
	        
	        
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SDKInitializer.initialize(getApplicationContext());  
        
		setContentView(R.layout.camera_detect);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		camera = (MyCamera)findViewById(R.id.camera);
		camera.setVisibility(View.VISIBLE);
		camera.setCvCameraViewListener(this);
		showdistance = (TextView)findViewById(R.id.distance);
		progress = (HProgress) findViewById(R.id.notice);
		progress.setMaxCount(1);
		progress.setCurrentCount(0.2f);
		access = new NetWorkAccess();
//		Log.i(TAG, "create");
		//TIAN
		initLocation();
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
//		width:1280 height:720
//		Log.i(TAG,"height is "+mrgb.rows());
//		Log.i(TAG, "width is "+mrgb.cols());
		roi = gray.submat(gray.rows()/4,gray.rows(),gray.cols()/4,gray.cols()/4*3);
		if(detectiveSize == 0){
			int height = roi.rows();
			if(Math.round(height*smaller)>0){
				detectiveSize = Math.round(height*smaller);
			}
		}
		if(getnew){//需要计算新的位置
			new MyThread().start();
		}else{//直接沿用以前的位置不变
			
		}
		float focus = camera.getfocus();
//		Log.i(TAG, "focus is "+focus);
		int num = 0;
		int max = -1;
//		获取识别出来的最大的车辆为比对车辆
		for(Rect rect:mDetection.toArray()){
			if(max == -1){
				max = 0;
			}
			if (rect.width>mDetection.toArray()[max].width){
				max = num;
			}
			num++;
		}
//		Log.i(TAG, "max is "+max);
//		显示最大的识别车辆
		if(max != -1){
			Rect rect = mDetection.toArray().clone()[max];
			Core.rectangle(
					mrgb, 
					new Point(gray.cols()/4+rect.x,rect.y+gray.rows()/4),
					new Point(gray.cols()/4+rect.x+rect.width,rect.y+rect.height+gray.rows()/4),
					new Scalar(0,255,0));
			int length = tracker.GetDistance(mrgb.getNativeObjAddr(),gray.cols()/4+rect.x+rect.width/2,rect.y+rect.height+gray.rows()/4);
//			System.out.println("length is "+ length);
//			if(length>0){
				Message m = new Message();
				m.what = 2;
				m.obj = length;
				myHandler.sendMessage(m);		
//			}
//		showdistance.setText(length+"米");
		}
		return mrgb;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
		mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
	}
	class MyThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m = new Message();
			getnew  = false;
			mDetector.detectMultiScale(roi, mDetection, 1.1, 2, 2, new Size(detectiveSize,detectiveSize),
					new Size());
			if(!mDetection.empty()){
//				Log.i(TAG, "get result");
				try{
					sleep(50);
				}catch(Exception e){
					e.printStackTrace();
				}
				m.what = 1;
				
			}else{
				m.what = 1;
			}
			myHandler.sendMessage(m);	
		}
	}
	
	
	/////////////////////////////////////////////////
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	if(!mLocationClient.isStarted())
      	  mLocationClient.start();
    	
    	myOrientationListener.start();
    	
    }


    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
  
    	mLocationClient.stop();
    	
    	myOrientationListener.stop();
    }
    
    private void initLocation() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this);
		mLocationListener =new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
		
		mLocationClient.setLocOption(option); 
		//初始化图标
	
		
		myOrientationListener = new MyOrientationListener(context);
	
        myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrientationChanged(float x) {
				// TODO Auto-generated method stub
			   mOrient = x;
			   
			   
			}

			@Override
			public void onOrientationChangAll(float x, float y, float z) {
				mCurrentX = x;
				mCurrentY = y;
				mCurrentZ = z;
	           
			}
		});
        
        
        
    }
    
    
    private class MyLocationListener implements BDLocationListener
    {

		@Override
		public void onReceiveLocation(BDLocation location) {

			MyLocationData data = new MyLocationData.Builder()//
			.direction(mOrient)//
			.accuracy(location.getRadius())//
			.latitude(location.getLatitude())//
			.longitude(location.getLongitude())//
			.build();
			
		
		
		

		  
		    
		    //更新经纬度
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();			
			mSpeed = 14;//location.getSpeed();
			
   //         Log.i(TAGG,"\n LAT: "+ mLatitude);  
   //         Log.i(TAGG,"\n LONG: "+ mLongtitude);  
   //         Log.i(TAGG,"\n SPEED: "+ mSpeed);  
            
        
			if(isFirstIn)
			{
			
				isFirstIn = false;
				address = location.getAddrStr();
				Toast.makeText(context,location.getAddrStr(), Toast.LENGTH_SHORT).show();
				Toast.makeText(context,mLatitude+","+mLongtitude, Toast.LENGTH_SHORT).show();
				Toast.makeText(context,mCurrentX+","+mCurrentY, Toast.LENGTH_SHORT).show();
				
				
			////////	要删除的
			    TimerTask task = new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String url = "http://192.168.1.107:5555/andinfor.php";
						new HttpThreadRegist(url, mLatitude, mLongtitude, mCurrentX, mCurrentY, mCurrentZ).start();	
						
						
					}
				};
				Timer timer = new Timer(true);
				timer.schedule(task,3000, 1000);	
				
				
			////////
			  
			}
			
		}
    	
    }
	
	private float[] speeds = new float[4];
	private float[] times = new float[2];
	private int[] distances = new int[3];
	private long lastTime = 0;   
    private int currentSpeed = 0;
    private int currentTime = 0;
    private int currentDistance = 0;
    
	private void setSpeeds(float speed){
//		if(currentSpeed)
	}
	
	//设置两次时间间隔
	private void setTimes(){
		times[currentTime] = System.currentTimeMillis() - lastTime;
		currentTime++;
		if(currentTime>1){
			currentTime = 0;
		}
	}
	
	private void setDistances(int distance){
		 
	}
	
	
	private double guanhuacount(double v1,double v2,double v3,double s1,double s2,double s3,double t1,double t2,double t3){
		double currentA = 0;
		
		currentA = (v3 -v2)/2 + (s1 - s2)/(t2 - t1) - (s3 - s2)/(t3 - t2);
		currentA = currentA/(t3- t2);
		
		
		return currentA;
		
			
	}
	
}






///////////////////////////////////////

