package net.yeelink.yeelinkiotclient;

import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class CameraCapture extends Activity {

	public SurfaceView svCamera;
	public Camera camera;
	private boolean run = true;
	public Button btnFocus;
	public Button btnTake;
	
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
		
	}
	
	private class SurfaceCallback implements SurfaceHolder.Callback{

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
				params.setPictureSize(480, 320);// 设置照片的大小为800*480
				params.setPreviewSize(480, 320);// 设置预览取景的大小为800*480
				params.setFlashMode(Parameters.FLASH_MODE_ON);// 开启闪光灯
				params.setJpegQuality(60);// 设置图片质量为50

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
