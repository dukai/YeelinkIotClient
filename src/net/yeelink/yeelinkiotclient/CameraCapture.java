package net.yeelink.yeelinkiotclient;

import java.io.IOException;

import net.yeelink.sdk.HttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class CameraCapture extends Activity {

	public SurfaceView svCamera;
	public Camera camera;
	private boolean run = true;
	public Button btnFocus;
	public Button btnTake;
	public ImageView ivPreview;
	
	public Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			switch (inputMessage.what) {
				case 0:
					break;
				case 1:
					byte[] data = (byte[]) inputMessage.obj;
					Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
					ivPreview.setImageBitmap(bm);
					break;
			}
		}

	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_capture);
		// Show the Up button in the action bar.
		setupActionBar();

		svCamera = (SurfaceView) findViewById(R.id.sv_camera);
		svCamera.getHolder().addCallback(new SurfaceCallback());
		svCamera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		btnFocus = (Button) findViewById(R.id.btn_focus);
		btnTake = (Button) findViewById(R.id.btn_take);
		
		ivPreview = (ImageView) findViewById(R.id.iv_preview);

		btnFocus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

		btnTake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camera != null) {
					camera.takePicture(new ShutterCallback() {
						public void onShutter() {
						}
					}, null, new PictureCallback() {

						@Override
						public void onPictureTaken(byte[] data,
								Camera arg1) {
							Message completeMessage = handler.obtainMessage(1,
									data);
							completeMessage.sendToTarget();
							HttpClient http = new HttpClient();
							http.addHeader("U-APIKEY", "4d0cd8e2e9cd21714b10696f80645d42");
							http.post("http://api.yeelink.net/v1.0/device/10879/sensor/18281/photos", data, null);
						}

					});
				}
			}

		});

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
				//camera.setDisplayOrientation(90);
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
			// TODO Auto-generated method stub
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_capture, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
