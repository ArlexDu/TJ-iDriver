package edu.happy.detection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpThreadRegist extends Thread {

	
	String url;
	
	double latitude;
	double longtitude;
	
	double xa;
	double ya;
	double za;
	
	
	public HttpThreadRegist(String url, double latitude, double longtitude,
			double xa, double ya, double za) {
		
		this.url = url;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
	}


	private void doPost(){
	
		try {
			
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(100000);
			OutputStream out =  conn.getOutputStream();
			String content = "latitude="+latitude+"&longtitude="+longtitude+"&xa="+xa+"&ya="+ya+"&za="+za;
			
			out.write(content.getBytes());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String str;
			
			while((str=reader.readLine())!=null){
				sb.append(str);
			}
			
			System.out.println(sb.toString());
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch(IOException e){
			 e.printStackTrace();
		 
	  }
   }
	
	
	@Override
	public void run() {
		
		try {
			doPost();
			sleep(2000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}
}
