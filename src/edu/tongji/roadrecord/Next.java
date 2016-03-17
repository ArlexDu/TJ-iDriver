package edu.tongji.roadrecord;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import edu.happy.roadrecord.R;


public class Next extends Activity{

	MediaPlayer ourSong;
   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.next);
		
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(3000);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				finally{
					Intent openMainActivity = new Intent(Next.this,StartActivity.class);
					startActivity(openMainActivity);
				}
				
				
			}
		};
		timer.start();
	}
	@Override
	protected void onPause() {
    super.onPause();
    finish();
    
	}
}
	
	


