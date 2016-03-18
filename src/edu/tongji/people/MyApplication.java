package edu.tongji.people;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;

public class MyApplication extends Application {
   public static RequestQueue queues;
   
   @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		queues=Volley.newRequestQueue(getApplicationContext());
	}
   
   public static RequestQueue Get_Queues(){
	   return queues;
   }
}
