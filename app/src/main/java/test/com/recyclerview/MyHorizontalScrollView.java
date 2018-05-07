package test.com.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import test.com.R;
import test.com.myapplication.newestversion.LeftScrollView;
import test.com.myapplication.newestversion.TopScrollView;

/**
 * Created by zhangsixia on 18/4/24.
 */

public class MyHorizontalScrollView extends HorizontalScrollView {

    private int RectWidth;
    private int RectHeight;
    private Rect mRect;
    private Paint mPaint;
    private int color;
    private int paintWidth = 2;
    private int needRectCount_X = TopScrollView.getTopNeedCount();
    private int needRectCount_Y = LeftScrollView.getLeftNeedCount();
    public MyHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        color = ContextCompat.getColor(getContext(), R.color.green_disable_qb);
        mRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(paintWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        RectWidth = width * 1/5;
        RectHeight = height * 1 / 15;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, 0);
        for (int i = 0; i < needRectCount_X; i++) {//绘制行Rect
            int distanceX = i * RectWidth;
            for(int j = 0; j < needRectCount_Y; j++){//绘制列Rect
                int distanceY = j * RectHeight;
                mRect.set(distanceX, distanceY, RectWidth+distanceX, RectHeight + distanceY);
                mPaint.setColor(Color.GRAY);
                canvas.drawRect(mRect, mPaint);
                drawTextInRect(mRect,canvas, i + 1,j+1);
            }
        }

    }

    private synchronized void drawTextInRect(Rect rect,Canvas canvas, int positionX,int positionY) {
        String testString = positionY+"0" + positionX;
        mPaint.setTextSize(30);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(Typeface.DEFAULT);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline_Y = ((rect.bottom + rect.top) - (fontMetrics.bottom + fontMetrics.top)) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(testString, rect.centerX(), baseline_Y, mPaint);
    }
    int cur_x;
    int cur_y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cur_x = (int) event.getX();
                cur_y = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return true;
    }
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }


}
