package edu.tongji.people;


import java.io.Console;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

public class PeopleAnalyze extends Activity {

	private PinChart chart;
	private ProgressBar bar;
	private NetWorkAccess access;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		bar = new ProgressBar(this);
		setContentView(bar);
		access = new NetWorkAccess();
		new Thread(new Runnable() {
			public void run() {
                 try {
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PeopleAnalyze.this);
					String userid = preferences.getString("userid", "-100");
				//	System.out.println("user id is "+userid);
					Map<String, String> map = new HashMap<String, String>();
					map.put("userid", userid);
					access.ChangeInfo("/andRequest.php", mHandler, 0, map);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
    Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		if(msg.what == 0){
    			Log.i("result : ", (String) msg.obj);
    			float[] times = new float[8];
    			String percentages = msg.obj.toString();
    		        String[] perArray = percentages.split("/");
    		        for (int i = 0; i < perArray.length; i++) {
    		            times[i] = Float.valueOf(perArray[i]);
    		            times[i] = (float) Math.round(times[i]*100)/100;
    		        } 
        		chart = new PinChart(PeopleAnalyze.this,times);
        		setContentView(chart);
        		chart.start();
    		}
    	};
    };

}
