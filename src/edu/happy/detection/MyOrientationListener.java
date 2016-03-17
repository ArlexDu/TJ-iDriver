package edu.happy.detection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MyOrientationListener implements SensorEventListener {
	 private static final String TAG = "sensor";  
	private SensorManager mSensorManager;
	private Context mContext;
	private Sensor mSensor;
	private Sensor nSensor;
	private float lastOrient;
	private float lastX;
	private float lastY;
	private float lastZ;
	
	
	
	public MyOrientationListener(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	@SuppressWarnings("deprecation")
	public void start()
	{
		mSensorManager= (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager != null)
		{
			
			//获得方向传感器
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			nSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			
		}
		if(mSensor !=null)
		{
			
			mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);
			
		}
		if(nSensor !=null)
		{
			mSensorManager.registerListener(this,nSensor,SensorManager.SENSOR_DELAY_GAME);
		}
		
	}
	
	public void stop(){
		mSensorManager.unregisterListener(this);
		
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {

		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
			float orient =event.values[SensorManager.DATA_X];
			
			if(Math.abs(orient - lastOrient) > 1.0)
			{
				if(mOnOrientationListener != null)
				{
					mOnOrientationListener.onOrientationChanged(orient);
				}
		
			}
			
			
			lastOrient = orient;
		}
		
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){  
            Log.i(TAG,"onSensorChanged");  
              
            //图解中已经解释三个值的含义  
            float x = event.values[0];  
            float y = event.values[1];  
            float z = event.values[2];  
           
				if(mOnOrientationListener != null)
				{
					mOnOrientationListener.onOrientationChangAll(x, y, z);
				}
		
			
			
			
			lastX =x;
			lastY =y;
			lastZ =z;
//            Log.i(TAG,"\n x: "+x);  
//            Log.i(TAG,"\n y "+y);  
//            Log.i(TAG,"\n z "+z);  
        }  
		
		
	}
	
	private OnOrientationListener mOnOrientationListener;
	
	public void setOnOrientationListener(
			OnOrientationListener mOnOrientationListener) {
		this.mOnOrientationListener = mOnOrientationListener;
	}

	
	public interface OnOrientationListener
	{
		void onOrientationChanged(float x);
		void onOrientationChangAll(float x,float y,float z);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
