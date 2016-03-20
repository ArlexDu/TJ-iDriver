package edu.happy.roadrecord;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import edu.happy.roadrecord.MyOrientationListener.OnOrientationListener;
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



public class MainActivity extends Activity {  
    private MapView mMapView;  
    private BaiduMap mBaiduMap;
    private Context context;
    private static final String TAG = "accerlet";  
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private LocationMode mLocationMode;
    private com.baidu.location.LocationClientOption.LocationMode tempMode = com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongtitude;
    //自定义图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mOrient; 
    private double mCurrentX;
    private double mCurrentY;
    private double mCurrentZ;
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        SDKInitializer.initialize(getApplicationContext());  
     
        setContentView(R.layout.activity_main);  
        //获取地图控件引用  
        this.context = this;
        initView();
        initLocation();
        
      
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
		option.setLocationMode(tempMode);
		mLocationMode = LocationMode.NORMAL;
		
		mLocationClient.setLocOption(option); 
		//初始化图标
		mIconLocation= BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
		
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
	            Log.i(TAG,"\n x: "+ mCurrentX);  
	            Log.i(TAG,"\n y: "+ mCurrentY);  
	            Log.i(TAG,"\n z: "+ mCurrentZ);  
			}
		});
        
        
        
    }


	private void initView() {
		// TODO Auto-generated method stub
    	mMapView = (MapView) findViewById(R.id.id_bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
	}

	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mMapView.onDestroy();
    }


 
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        centerToMyLocation();
        }  
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	mBaiduMap.setMyLocationEnabled(true);
    	if(!mLocationClient.isStarted())
    	  mLocationClient.start();
    	//开启方向
    	myOrientationListener.start();
    	
    }
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	mBaiduMap.setMyLocationEnabled(false);
    	mLocationClient.stop();
    	
    	myOrientationListener.stop();
    }
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	getMenuInflater().inflate(R.menu.main, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
    	switch(item.getItemId())
    	{
    	case R.id.id_map_common:
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    	break;
		case R.id.id_map_site:
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);    
		break;
		case R.id.id_map_traffic:
		if(mBaiduMap.isTrafficEnabled())
		{
			mBaiduMap.setTrafficEnabled(false);
			item.setTitle("实时交通off");
		}
		else
		{
			mBaiduMap.setTrafficEnabled(true);
		    item.setTitle("实时交通on");
		}
		break;
		case R.id.id_map_location:
			centerToMyLocation();
		break;
		case R.id.id_mode_normal:
			mLocationMode = LocationMode.NORMAL;
		
			break;
		case R.id.id_mode_following:
			mLocationMode = LocationMode.FOLLOWING;
	
		break;
		case R.id.id_mode_compass:
			mLocationMode = LocationMode.COMPASS;
		break;
		
		default:
			break;

    	}
    	// TODO Auto-generated method stub
    	return super.onOptionsItemSelected(item);
    	
    }



	private void centerToMyLocation() {
		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu =MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
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
			
			mBaiduMap.setMyLocationData(data);
		
			//设置自定义图标
		    MyLocationConfiguration config = new 
  	    MyLocationConfiguration(mLocationMode, true, mIconLocation);

		    mBaiduMap.setMyLocationConfigeration(config);
		    
		    //更新经纬度
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();			
			
			 Log.i(TAG,"\n x: "+ mLatitude);  
	            Log.i(TAG,"\n y: "+ mLongtitude);  
			 
			
			if(isFirstIn)
			{
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate msu =MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
				
				Toast.makeText(context,location.getAddrStr(), Toast.LENGTH_SHORT).show();
				Toast.makeText(context,mLatitude+","+mLongtitude, Toast.LENGTH_SHORT).show();
				Toast.makeText(context,mCurrentX+","+mCurrentY, Toast.LENGTH_SHORT).show();
				
				
				String url = "http://192.168.1.107:5555/andinfor.php";
		
				new HttpThreadRegist(url, mLatitude, mLongtitude, mCurrentX, mCurrentY, mCurrentZ).start();
			  
			}
			
		}
    	
    }
    
}