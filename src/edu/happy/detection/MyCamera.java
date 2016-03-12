package edu.happy.detection;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

public class MyCamera extends JavaCameraView {

	public MyCamera(Context context, int cameraId) {
		super(context, cameraId);
		// TODO Auto-generated constructor stub
	}

	public MyCamera(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public float getfocus(){
		Camera.Parameters params = mCamera.getParameters();
		float focus = params.getFocalLength();
		return focus;
	}
}
