package edu.tongji.people;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.happy.roadrecord.R;

public class RegisterActivity extends Activity {

	private TextView username, password, olduser;
	private Button register;
	private NetWorkAccess access;
	private ProgressBar bar;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		username = (TextView) findViewById(R.id.accountEt);
		password = (TextView) findViewById(R.id.pwdEt);
		olduser = (TextView) findViewById(R.id.olduser);
		register = (Button) findViewById(R.id.subBtn);
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		access = new NetWorkAccess();
		context = RegisterActivity.this;
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
					access.ChangeInfo("/android.php", mhandler, 0, map);
				}
			}).start();
			break;
		case R.id.olduser:
//			System.out.println("go to login");
			Intent intent = new Intent(context, LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
			break;
			
		}
	}

	Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				bar.setVisibility(View.INVISIBLE);
//				System.out.println(msg.obj);
				SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = preferences.edit();
				editor.putString("userid", msg.obj.toString());
				editor.commit();
				Intent intent = new Intent(context, PeopleAnalyze.class);
				startActivity(intent);
				RegisterActivity.this.finish();
				break;
			default:
				break;
			}
		};
	};
}
