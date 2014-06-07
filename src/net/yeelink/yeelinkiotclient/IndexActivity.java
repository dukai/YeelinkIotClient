package net.yeelink.yeelinkiotclient;

import java.util.Date;

import net.yeelink.sdk.HttpClient;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class IndexActivity extends Activity {

	public TextView tv;
	public TextView tv_updateTime;
	public TextView tv_httpStatus;
	private LocationManager locationManager;
	public String result = "";
	public Handler mHandler;
	public Button btnCamera;
	public boolean isQuit = false;

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
		tv_updateTime = (TextView) findViewById(R.id.update_time);
		tv_httpStatus = (TextView) findViewById(R.id.htt_status);
		btnCamera = (Button) findViewById(R.id.btn_camera);
		btnCamera.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "点击了", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(IndexActivity.this, CameraCapture.class);
				
				startActivity(intent);
			}
		});
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		updateView(location);
		//定时更新位置信息
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {

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
		
		//handler处理线程事件
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message inputMessage) {
				switch (inputMessage.what) {
					case 0:
						isQuit = true;
						break;
					case 1:
						String code = ((Integer) inputMessage.obj).toString();
						tv_httpStatus.setText(code);
						break;
				}
			}

		};

		return true;
	}
	//更新视图
	private void updateView(Location location) {
		tv_updateTime.setText(new Date().toString());
		Log.i("gps", new Date().toString());
		if (location != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("经度：");
			sb.append(location.getLongitude());
			sb.append("\n纬度：");
			sb.append(location.getLatitude());
			sb.append("\n速度：");
			sb.append(location.getSpeed());
			tv.setText(sb.toString());

			uploadData(location.getLongitude(), location.getLatitude(),
					location.getSpeed());
		} else {
			// 如果传入的Location对象为空则清空EditText
			tv.setText("载入中...");
		}
	}
	//上传位置信息数据
	private void uploadData(double lng, double lat, float speed) {
		result = "{\"value\":{\"lat\":" + lat + ",\"lng\":" + lng
				+ ",\"speed\":" + speed + "}}";
		new Thread(new Runnable() {
			public void run() {
				HttpClient http = new HttpClient();
				http.addHeader("U-APIKEY", "4d0cd8e2e9cd21714b10696f80645d42");
				http.post(
						"http://api.yeelink.net/v1.0/device/10879/sensor/18016/datapoints",
						result);
				Message completeMessage = mHandler.obtainMessage(1,
						http.getStatusCode());
				completeMessage.sendToTarget();
			}
		}).start();
	}
	
	 @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            if (!isQuit) {  
                isQuit = true;  
                Toast.makeText(getApplicationContext(), "再按一次退出程序",  
                        Toast.LENGTH_SHORT).show();  
                // 利用handler延迟发送更改状态信息  
                mHandler.sendEmptyMessageDelayed(0, 2000);  
            } else {  
                finish();  
                System.exit(0);  
            }  
        }  
        return false;  
    }  
}
