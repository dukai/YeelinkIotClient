package net.yeelink.yeelinkiotclient;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrackActivity extends Activity {

	SpeedView view;
	TextView tv;
	
	public Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			switch (inputMessage.what) {
				case 0:
					int code = (Integer) inputMessage.obj;
					tv.setText(((Integer) code).toString());
					view.setSpeed(code);
					view.invalidate();
					break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);

		RelativeLayout rl = (RelativeLayout) findViewById(R.id.track_layout);

		view = new SpeedView(this);
		view.setMinimumHeight(1000);
		view.setMinimumWidth(600);
		// 通知view组件重绘
		view.setSpeed(10);
		view.invalidate();
		rl.addView(view);

		tv = new TextView(this.getApplicationContext());
		Typeface tf = Typeface.createFromAsset(getAssets(), "font/DS_DIGI.TTF");// 读取字体
		tv.setText("SPEED");
		tv.setTypeface(tf);
		rl.addView(tv);
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					Random rand = new Random();
					int i = rand.nextInt(); //int范围类的随机数
					i = rand.nextInt(100); //生成0-100以内的随机数
					Message completeMessage = mHandler.obtainMessage(0,
							i);
					completeMessage.sendToTarget();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}).start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track, menu);
		return true;
	}

}
