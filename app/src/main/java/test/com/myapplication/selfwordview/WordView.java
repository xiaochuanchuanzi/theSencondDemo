package test.com.myapplication.selfwordview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangsixia on 18/4/19.
 */

public class WordView extends View {

    /*头部*/
    private Paint titlePaint;
    private Rect titleRect;
    /*左侧*/
    private Paint leftPaint;
    private Rect leftRect;
    /*主体*/
    private Paint bodytPaint;
    private Rect bodyRect;
    /*尺寸单位*/
    private int totalSizeWidth;
    private int totalSizeHeight;
    /*单位Rect*/
    private int leftItem = 0;
    private int topItem = 0;
    private Rect itemTitleRect;
    /*左上角固定不动的一个view*/
    private Rect leftTopRect = new Rect(0, 0, 200, 200);
    private Paint leftTopPaint = new Paint();
    /*发生滚动或者缩放时,产生的尺寸变化差值*/
    public int distance_X = 0;
    public int distance_Y = 0;

    public WordView(Context context) {
        super(context);
        init();
    }

    public WordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*初始化的方法*/
    private void init() {
        titlePaint = new Paint();
        leftPaint = new Paint();
        bodytPaint = new Paint();
        titleRect = new Rect();
        leftRect = new Rect();
        bodyRect = new Rect();

        //Typeface font = Typeface.create("哈哈",Typeface.BOLD);
        leftTopPaint.setColor(Color.RED);
        leftTopPaint.setAntiAlias(true);
        leftTopPaint.setStyle(Paint.Style.FILL);
        leftTopPaint.setStrokeWidth(2);
        //leftTopPaint.setTextAlign(Paint.Align.CENTER);
        //leftTopPaint.setTextSize(16);
        //leftTopPaint.setTypeface(font);

        leftPaint.setColor(Color.GRAY);
        leftPaint.setAntiAlias(true);
        leftPaint.setStyle(Paint.Style.STROKE);
        leftPaint.setStrokeWidth(5);

        titlePaint.setColor(Color.BLUE);
        titlePaint.setAntiAlias(true);
        titlePaint.setStyle(Paint.Style.STROKE);
        titlePaint.setStrokeWidth(5);

        bodytPaint.setColor(Color.BLACK);
        bodytPaint.setAntiAlias(true);
        bodytPaint.setStyle(Paint.Style.STROKE);
        bodytPaint.setStrokeWidth(3);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalSizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        totalSizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(totalSizeWidth, totalSizeHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, 0);
        drawTitle(canvas);
    }

    /*绘制头部控件*/
    public void drawTitle(Canvas canvas) {

        Log.i("TAG", "distance_X          " + distance_X);
        if(distance_X > 0) distance_X = 0;
        if(distance_Y > 0) distance_Y = 0;

        BodyView.getInstance().onDraw(200 + distance_X, 200 + distance_Y, canvas, bodytPaint);

        LeftView.getInstance(this).onDraw(0, 200 + distance_Y, canvas, leftPaint);

        TitleView.getInstance(this).onDraw(200 + distance_X, 0, canvas, titlePaint);


        canvas.drawRect(leftTopRect, leftTopPaint);
    }


    int last_X = 0;
    int last_Y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                last_X = (int) event.getRawX();
                last_Y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 两次的偏移量
                distance_X = (int) (event.getRawX() - last_X);
                distance_Y = (int) (event.getRawY() - last_Y);
                break;
            case MotionEvent.ACTION_UP:
                last_X = (int) event.getRawX();
                last_Y = (int) event.getRawY();
                break;
        }
        invalidate();
        return true;
    }


}















