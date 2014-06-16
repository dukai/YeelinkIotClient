package net.yeelink.yeelinkiotclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class SpeedView extends View {

	private int speed = 0;
	
	public SpeedView(Context context) {
		super(context);
	}
	
	public void setSpeed(int value){
		speed = value;
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
        RectF oval1=new RectF(40,40,400,400);  
        canvas.drawArc(oval1, 155, 220, false, p);//小弧形
        
        Paint p2 = new Paint();
        p2.setColor(0xff3fa2ff);
        p2.setStrokeWidth(25);
        p2.setAntiAlias(true);
        p2.setStyle(Paint.Style.STROKE);
        
        oval1.set(50,50, 390, 390);
        canvas.drawArc(oval1, 155, speed, false, p2);
        
	}

}
