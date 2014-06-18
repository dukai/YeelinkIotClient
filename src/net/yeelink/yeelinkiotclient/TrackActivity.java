package net.yeelink.yeelinkiotclient;

import java.util.Random;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TrackActivity extends Activity {

	SpeedView view;
	TextView tv;
	TextView tvLogo;
	TextView tvLat;
	TextView tvLng;
	TextView tvSpeed;
	private boolean isLocate = false;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	public Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			switch (inputMessage.what) {
				case 0:
					//int code = (Integer) inputMessage.obj;
					//tv.setText(((Integer) code).toString());
					//view.setSpeed(code);
					break;
				case 1:
					BDLocation location = (BDLocation) inputMessage.obj;
					tvLng.setText(String.valueOf(location.getLongitude()));
					tvLat.setText(String.valueOf(location.getLatitude()));
					
                    Random rand = new Random();
                    int i = rand.nextInt(); //int范围类的随机数
                    i = rand.nextInt(4); //生成0-100以内的随机数
                    tvSpeed.setText(String.valueOf(location.getSpeed() + i));
					view.setSpeed((int) location.getSpeed() + i);
					break;
			}
		}

	};

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			logMsg(sb.toString());

			Message completeMessage = mHandler.obtainMessage(1, location);
			completeMessage.sendToTarget();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}

		public void logMsg(String value) {
			Log.i("run", value);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		Typeface tf = Typeface.createFromAsset(getAssets(), "font/DS_DIGI.TTF");// 读取字体
		tvLogo = (TextView) findViewById(R.id.tv_logo);
		tvLat = (TextView) findViewById(R.id.tv_lat);
		tvLng = (TextView) findViewById(R.id.tv_lng);
		tvSpeed = (TextView) findViewById(R.id.tv_speed);
		tvLat.setTypeface(tf);
		tvLng.setTypeface(tf);
		tvSpeed.setTypeface(tf);

		tvLogo.setTypeface(tf);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.track_layout);

		view = new SpeedView(this);
		view.setMinimumHeight(1000);
		view.setMinimumWidth(600);
		// 通知view组件重绘
		rl.addView(view);

		view.setSpeed(0);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		// option.setScanType(5000);//设置发起定位请求的间隔时间为5000ms
		option.setScanSpan(5000);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		mLocationClient.requestLocation();
	}

	@Override
	public void onResume() {
		super.onResume();
		mLocationClient.start();
		isLocate = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isLocate) {

					try {
						Thread.sleep(1000);
						mLocationClient.requestLocation();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	public void onPause() {
		super.onPause();
		mLocationClient.stop();
		isLocate = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track, menu);
		return true;
	}

}
