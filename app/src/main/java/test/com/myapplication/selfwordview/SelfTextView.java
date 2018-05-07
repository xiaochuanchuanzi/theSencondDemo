package test.com.myapplication.selfwordview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.TextView;

import static test.com.myapplication.selfwordview.SelfGroup.isScrolling;

/**
 * Created by zhangsixia on 18/4/28.
 */

@SuppressLint({"ViewConstructor", "AppCompatCustomView"})
public class SelfTextView extends TextView{

    public SelfTextView(Context context) {
        super(context);
    }

    public SelfTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.GRAY);
        int leftLine = 0;
        int rightLine = getWidth();
        int topLine = getHeight()-1;
        int belowLine = topLine;
        canvas.drawLine(leftLine, topLine, rightLine, belowLine, linePaint);
        //drawTextInRect(canvas);
    }
    @Deprecated
    private void drawTextInRect( Canvas canvas) {
        String testString =  "00000";
        linePaint.setTextSize(30);
        linePaint.setColor(Color.BLACK);
        Paint.FontMetricsInt fontMetrics = linePaint.getFontMetricsInt();
        int baseline_X = ((getLeft() + getRight()) ) / 2;
        int baseline_Y = ((getBottom() + getTop()) - (fontMetrics.bottom + fontMetrics.top)) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        linePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(testString, baseline_X, baseline_Y, linePaint);
    }

    /* @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean onclick = false;
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.i("ACTION_DOWN","   执行了down操作");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("ACTION_DOWN","   执行了move操作");
                break;
            case MotionEvent.ACTION_UP:
                Log.i("ACTION_DOWN","   执行了up操作");
                break;
        }
        return true;
    }*/
}
