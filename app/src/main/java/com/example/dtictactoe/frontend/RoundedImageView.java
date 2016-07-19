package com.example.dtictactoe.frontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
//import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by martin on 18.7.16.
 */
public class RoundedImageView extends ImageView {

    private Paint paint = new Paint();

    public RoundedImageView(Context context) {
        super(context);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // TODO Auto-generated constructor stub
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, paint);
    }



    public void setTurn(int turn){
        if(turn == MyGLRenderer.TURN_RED){
            Log.d("TAG", "changin to red");
            paint.setColor(Color.RED);
            invalidate();
        } else if (turn == MyGLRenderer.TURN_BLUE){
            Log.d("TAG", "changin to blue");
            paint.setColor(Color.BLUE);
            invalidate();        }
    }
}