package test.com.myapplication.scrollview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhangsixia on 18/4/26.
 */

public class LeftAndTop extends View {

    private int RectWidth;
    private int RectHeight;
    private Rect mRect;
    private Paint mPaint;
    private int color = Color.GRAY;
    private int paintWidth = 2;

    public LeftAndTop(Context context) {
        super(context);
        init();
    }

    public LeftAndTop(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeftAndTop(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(paintWidth);
    }

    public void setRectCount(int rectCount) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        RectWidth = width;
        RectHeight = height / 3;
    }

    private int row = 3;
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, 0);
        //获取X,Y轴方向的位移
        for (int i = 0; i < row; i++) {//绘制行Rect
            float top = i * RectHeight;
            float bottom = top + RectHeight;
            float left = 0;
            float right = left + RectWidth;
            mRect.set((int) left, (int) top, (int) right, (int) bottom);
            mPaint.setColor(Color.GRAY);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawRect(mRect, mPaint);
            drawTextInRect(canvas, i + 1);
        }
    }

    private void drawTextInRect(Canvas canvas, int position) {
        String testString = position + "单元";
        mPaint.setTextSize(30);
        mPaint.setColor(Color.WHITE);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(testString, mRect.centerX(), baseline, mPaint);
    }
}
