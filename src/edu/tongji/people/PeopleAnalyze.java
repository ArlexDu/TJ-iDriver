package edu.tongji.people;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
					Thread.sleep(3000);
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PeopleAnalyze.this);
					String userid = preferences.getString("userid", "-100");
					Map<String, String> map = new HashMap<String, String>();
					map.put("userid", userid);
					access.ChangeInfo("/andlogin.php", mHandler, 0, map);
					Message message = new Message();
					message.what = 0;
					mHandler.sendMessage(message);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
    Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		float[] times ={12.5f,12.5f,12.5f,12.5f,12.5f,12.5f,12.5f,12.5f}; 
    		chart = new PinChart(PeopleAnalyze.this,times);
    		setContentView(chart);
    		chart.start();
    	};
    };

}
