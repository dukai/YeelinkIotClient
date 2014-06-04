package net.yeelink.yeelinkiotclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new Thread() {
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL("http://www.qq.com/");
					urlConnection = (HttpURLConnection) url.openConnection();
					InputStream in = new BufferedInputStream(
							urlConnection.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "GBK"));
					String theString;
					do{
						theString = reader.readLine();
						byte[] bs = theString.getBytes("gb2312");
						System.out.println(new String(bs, "gb2312"));
					}while(!theString.equals(""));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					urlConnection.disconnect();
				}
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
}
