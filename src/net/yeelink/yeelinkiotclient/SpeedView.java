package net.yeelink.yeelinkiotclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class SpeedView extends View {

	private int speed = 0;
	private int prevSpeed = 0;
	private float currentSpeed = 0;
	private float step = 0;
	
	public Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			switch (inputMessage.what) {
				case 0:
					SpeedView.this.invalidate();
					break;
			}
		}

	};
	
	public SpeedView(Context context) {
		super(context);
	}
	
	public void setSpeed(int value){
		prevSpeed = speed;
		speed = value;
		this.updateSpeed();
	}
	
	public void updateSpeed(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				step = (float) (speed - prevSpeed) / 50;
				currentSpeed = (float) prevSpeed;
				int i = 0;
				while(i < 50){
					currentSpeed += step;
					Log.i("run", ((Float) currentSpeed).toString());
					Message completeMessage = mHandler.obtainMessage(0,
							i);
					completeMessage.sendToTarget();
					i++;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				currentSpeed = speed;
				Message completeMessage = mHandler.obtainMessage(0,	i);
				completeMessage.sendToTarget();
			}
			
		}).start();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		Paint p = new Paint();
        p.setColor(0xff064483);// 设置红色  
        p.setStrokeWidth(20);
        p.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了  
        
        p.setStyle(Paint.Style.STROKE);//设置空心
        
        int width = this.getWidth();
        
        RectF oval1=new RectF(40,100,width - 80, width);  
        canvas.drawArc(oval1, 155, 230, false, p);//小弧形
        
        Paint p2 = new Paint();
        p2.setColor(0xff3fa2ff);
        p2.setStrokeWidth(25);
        p2.setAntiAlias(true);
        p2.setStyle(Paint.Style.STROKE);
        
        oval1.set(50,110, width - 90, width - 10);
        canvas.drawArc(oval1, 155, currentSpeed, false, p2);
	}

}
