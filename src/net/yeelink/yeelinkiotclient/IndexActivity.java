package net.yeelink.yeelinkiotclient;

import java.text.MessageFormat;

import net.yeelink.sdk.HttpClient;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class IndexActivity extends Activity {

	public TextView tv;
	private LocationManager locationManager;
	public String result = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.index, menu);
		tv = (TextView) findViewById(R.id.fist_text);
		tv.setText("人生苦短");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		updateView(location);

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				2000, 8, new LocationListener() {

					@Override
					public void onLocationChanged(Location location) {
						// 当GPS定位信息发生改变时，更新位置
						updateView(location);
					}

					@Override
					public void onProviderDisabled(String provider) {
						updateView(null);
					}

					@Override
					public void onProviderEnabled(String provider) {
						// 当GPS LocationProvider可用时，更新位置
						updateView(locationManager
								.getLastKnownLocation(provider));

					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});

		return true;
	}

	private void updateView(Location location) {
		if (location != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("实时的位置信息：\n经度：");
			sb.append(location.getLongitude());
			sb.append("\n纬度：");
			sb.append(location.getLatitude());
			sb.append("\n高度：");
			sb.append(location.getAltitude());
			sb.append("\n速度：");
			sb.append(location.getSpeed());
			sb.append("\n方向：");
			sb.append(location.getBearing());
			sb.append("\n精度：");
			sb.append(location.getAccuracy());
			tv.setText(sb.toString());
			
			uploadData(location.getLongitude(), location.getLatitude(), location.getSpeed());
		} else {
			// 如果传入的Location对象为空则清空EditText
			tv.setText("载入中...");
		}
	}
	
	private void uploadData(double lng, double lat, float speed){
		result = "{\"value\":{\"lat\":" + lat + ",\"lng\":" + lng + ",\"speed\":" + speed + "}}";
		new Thread(new Runnable() {
			public void run() {
				HttpClient http = new HttpClient();
				http.addHeader("U-APIKEY", "4d0cd8e2e9cd21714b10696f80645d42");
				http.post("http://api.yeelink.net/v1.0/device/10879/sensor/18016/datapoints", result);
			}
		}).start();
	}

}
