package edu.happy.roadrecord;

import Detetction.CameraDetect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

	private Button Detective;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Detective = (Button)findViewById(R.id.detective);
	}
	
	private void OnClick(View v){
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
