package net.yeelink.yeelinkiotclient;

import java.io.IOException;
import java.util.Date;

import net.yeelink.sdk.HttpClient;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
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
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class IndexActivity extends Activity {

	public TextView tv;
	public TextView tv_updateTime;
	public TextView tv_httpStatus;
	public TextView tv_photoUpload;
	private LocationManager locationManager;
	public String result = "";
	public Button btnCamera;
	public Button btnBegin;
	public boolean isQuit = false;
	public Camera camera;
	public SurfaceView svCamera;
	
	public Handler mHandler = new Handler(Looper.getMainLooper()) {
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
				case 2:
					String photoCode = ((Integer) inputMessage.obj).toString();
					tv_photoUpload.setText(photoCode);
					break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		svCamera = (SurfaceView) findViewById(R.id.sv_photo_preview);
		svCamera.getHolder().addCallback(new SurfaceCallback());
		svCamera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		tv = (TextView) findViewById(R.id.fist_text);
		tv_updateTime = (TextView) findViewById(R.id.update_time);
		tv_httpStatus = (TextView) findViewById(R.id.htt_status);
		tv_photoUpload = (TextView) findViewById(R.id.tv_photo_upload);
		btnCamera = (Button) findViewById(R.id.btn_camera);
		btnBegin = (Button) findViewById(R.id.btn_begin);
		btnCamera.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "点击了", Toast.LENGTH_LONG).show();
				//Intent intent = new Intent(IndexActivity.this, CameraCapture.class);
				
				//startActivity(intent);
				if (camera != null) {
					camera.autoFocus(new AutoFocusCallback() {

						@Override
						public void onAutoFocus(boolean success,
								Camera camera) {
							// TODO Auto-generated method stub

						}

					});
				}
			}
		});
		
		btnBegin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				takePictureInterval();
			}
			
		});
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		updateView(location);
		//定时更新位置信息
		/*
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
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.index, menu);
		
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
	 
	 public void takePictureInterval(){
		 new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					if(camera == null){
						camera = Camera.open();
						Parameters params = camera.getParameters();// 获取照相机的参数
						params.setPictureSize(800, 480);// 设置照片的大小为800*480
						//params.setPreviewSize(480, 800);// 设置预览取景的大小为800*480
						params.setFlashMode(Parameters.FLASH_MODE_OFF);// 开启闪光灯
						params.setJpegQuality(70);// 设置图片质量为50
						camera.setParameters(params);// 设置以上参数为照相机的参数
					}
					
					camera.takePicture(new ShutterCallback() {
						public void onShutter() {
						}
					}, null, new PictureCallback() {

						@Override
						public void onPictureTaken(byte[] data,
								Camera arg1) {
							HttpClient http = new HttpClient();
							http.addHeader("U-APIKEY", "4d0cd8e2e9cd21714b10696f80645d42");
							http.post("http://api.yeelink.net/v1.0/device/10879/sensor/18281/photos", data, null);
							Message completeMessage = mHandler.obtainMessage(2,
									http.getStatusCode());
							completeMessage.sendToTarget();
						}

					});
					try {
						Thread.sleep(20 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				
			}
			 
		 }).start();
	 }
	 
	 private class SurfaceCallback implements SurfaceHolder.Callback {

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				try {
					camera = Camera.open();// 打开摄像头
					camera.setDisplayOrientation(90);
					camera.setPreviewDisplay(svCamera.getHolder());// 设置picSV来进行预览取景

					Parameters params = camera.getParameters();// 获取照相机的参数
					params.setPictureSize(800, 480);// 设置照片的大小为800*480
					//params.setPreviewSize(480, 800);// 设置预览取景的大小为800*480
					params.setFlashMode(Parameters.FLASH_MODE_OFF);// 开启闪光灯
					params.setJpegQuality(70);// 设置图片质量为50

					camera.setParameters(params);// 设置以上参数为照相机的参数
					camera.startPreview();
				} catch (IOException e) {// 开始预览取景，然后我们就可以拍照了
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				camera.stopPreview();
				camera.release();
				camera = null;
			}

		}
}
