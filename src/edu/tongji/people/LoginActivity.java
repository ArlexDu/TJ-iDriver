package edu.tongji.people;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.happy.roadrecord.R;

public class LoginActivity extends Activity {

	private TextView username, password, newuser;
	private Button register;
	private NetWorkAccess access;
	private ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		username = (TextView) findViewById(R.id.accountEt);
		password = (TextView) findViewById(R.id.pwdEt);
		newuser = (TextView) findViewById(R.id.newuser);
		register = (Button) findViewById(R.id.subBtn);
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		access = new NetWorkAccess();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.subBtn:
			bar.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {
				public void run() {
					String uname = username.getText().toString();
					String pw = password.getText().toString();
					Map<String, String> map = new HashMap<String, String>();
					map.put("uname", uname);
					map.put("upassword", pw);
					access.ChangeInfo("/android/add", mhandler, 0, map);
				}
			}).start();
			break;
		}
	}

	Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				bar.setVisibility(View.INVISIBLE);
				System.out.println(msg.obj);
				break;
			default:
				break;
			}
		};
	};
}
