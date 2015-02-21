package com.example.blue;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyView extends View
    {
		Canvas tmp;
        public MyView(Context context) 
        {
             super(context);
        }
        
        public void update(int x,int y,int refX,int refY)
        {
        	draw(tmp);
        }

        @Override
        protected void onDraw(Canvas canvas) 
        {
        	tmp = canvas;
           super.onDraw(canvas);
           int radius;
           radius = 50;
           Paint paint = new Paint();
           paint.setStyle(Paint.Style.FILL);
           paint.setColor(Color.WHITE);
           canvas.drawPaint(paint);
           // Use Color.parseColor to define HTML colors
           paint.setColor(Color.parseColor("#CD5C5C"));
           //canvas.drawCircle(refX, refY, radius,paint);
           paint.setColor(Color.parseColor("#444444"));
           //canvas.drawCircle(x, y, radius/2,paint);
           invalidate();
       }
    }