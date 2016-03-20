package edu.tongji.people;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;

public class PeopleAnalyze extends Activity {

	private PinChart chart;
	private ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		bar = new ProgressBar(this);
		chart = new PinChart(this);
		setContentView(bar);
		new Thread(new Runnable() {
			public void run() {
                 try {
					Thread.sleep(3000);
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
    		setContentView(chart);
    		chart.start();
    	};
    };

}
