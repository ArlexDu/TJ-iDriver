package edu.tongji.roadrecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import edu.happy.detetction.CameraDetect;
import edu.happy.roadrecord.R;

public class StartActivity extends Activity {

	private Button Detective;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Detective = (Button)findViewById(R.id.detective);
	}
	
	public void onClick(View v){
		Intent intent ;
		switch(v.getId()){
			case R.id.detective:
				intent = new Intent(this,CameraDetect.class);
				startActivity(intent);
			break;
			case R.id.map:
				
			break;
		}
	}

}
