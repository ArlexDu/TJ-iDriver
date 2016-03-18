package edu.tongji.people;


import com.android.volley.Request.Method;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.tongji.people.MyApplication;;
public class NetWorkAccess {
    public static String base_url = "http://10.60.42.70:3000";
	
	public void ChangeInfo(final String uri,final Handler handler,final int what,final Map<String, String> map){	
		String url = base_url+uri;
		StringRequest request = new StringRequest(Method.POST, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
//						Log.i("net", response);
						Message message = new Message();
						message.what = what;
						message.obj = response;
						handler.sendMessage(message);
					}
		       }, new Response.ErrorListener() {

			       @Override
			       public void onErrorResponse(VolleyError error) {
				      // TODO Auto-generated method stub
				        Log.e("net error", error.toString());
			   }
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return map;
			}
			
		};
		request.setTag("getinfo");
		MyApplication.Get_Queues().add(request);
	}
	
}
