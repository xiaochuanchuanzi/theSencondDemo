package test.com.myapplication.scrollview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import test.com.myapplication.selfwordview.SelfGroup;

/**
 * Created by zhangsixia on 18/4/22.
 */

public class TopScrollView extends View implements SelfGroup.BodyInterface1 {


    private OverScroller mScroller;//用于辅助View拖动或滑行
    private VelocityTracker mVelocityTracker = null; //速度追踪器
    public static final int SNAP_VELOCITY = 600;  //最小的滑动速率,小于这个速率不滑行
    private int RectWidth;
    private int RectHeight;
    private Rect mRect;
    private Paint mPaint;
    private int color = Color.GRAY;
    private int paintWidth = 2;
    private static int needRectCount = 50;
    private int totalRectWidth;
    private Context mContext;

    public TopScrollView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TopScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TopScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        mScroller = new OverScroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        mRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(paintWidth);
    }

    public void addListener(SelfGroup mSelfGroup){
        mSelfGroup.setInterface1(this);
    }

    public void setRectCount(int rectCount) {
        this.needRectCount = rectCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        RectWidth = width * 1 / 6;
        RectHeight = height / 3;
    }

    private int bigUnitCount = 3;
    private int column = 3;
    private int row = 3;
    @Override
    protected void onDraw(Canvas canvas) {
        for (int k = 0; k < bigUnitCount; k++) {
            //循环完成第一次之后进行第二列的绘制,需要改变X周方向上的参数
            float width = k * column * RectWidth + k * 30;
            for (int i = 0; i < row; i++) {//绘制行Rect
                float top = i * RectHeight;
                float bottom = top + RectHeight;
                for (int j = 0; j < column; j++) {//绘制列Rect
                    if (i == 0) {
                        if (j == 0) {
                            float left = j * RectWidth * column + width;
                            float right = left + RectWidth * column;
                            mRect.set((int) left, (int) top, (int) right, (int) bottom);
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            mPaint.setColor(Color.WHITE);
                            canvas.drawRect(mRect, mPaint);
                        }
                    } else {
                        float left = j * RectWidth + width;
                        float right = left + RectWidth;
                        mRect.set((int) left, (int) top, (int) right, (int) bottom);
                        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        mPaint.setColor(Color.WHITE);
                        canvas.drawRect(mRect, mPaint);
                    }
                    drawTextInRect(canvas, i + 1);

                }
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.GRAY);

                int leftLine = (int) (width);
                int topLine = (int) (i * RectHeight);
                int rightLine = (int) (RectWidth * column + leftLine);
                int belowLine = topLine;
                canvas.drawLine(leftLine, topLine, rightLine, belowLine, mPaint);
            }
        }
        totalRectWidth = (RectWidth * column +30) * bigUnitCount;
    }

    private void drawTextInRect(Canvas canvas, int position) {
        String testString = position + "单元";
        mPaint.setTextSize(30);
        mPaint.setColor(Color.GRAY);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(testString, mRect.centerX(), baseline, mPaint);
    }

    @Override
    public void computeScroll() {
        //返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。这是一个很重要的方法，通常放在View.computeScroll()中，用来判断是否滚动是否结束
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 调用Scroller的startScroll后，Scroller会根据偏移量是时间计算当前的X坐标和Y坐标，执行invalidte会让View执行draw()方法，从而调用computeScroll()方法
     */
    public void smoothScrollBy(int dx) {
        int scrollX = mScroller.getFinalX() + dx;
        //分三种情况
        if (scrollX < 0) { //执行下滑操作,到达顶端时执行的操作
            dx = -1 * mScroller.getFinalX();
        }
        if (scrollX >= totalRectWidth - getWidth()) {
            //执行上滑操作,到达底端时执行的操作
            dx = totalRectWidth - getWidth() - mScroller.getFinalX();
        }
        //mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, 0, 500);
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, 0,500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 根据瞬时速度，让画布滑行
     */
    public void fling(int velocityX, int velocityY) {
        //最后两个是参数是允许的超过边界值的距离
        mScroller.fling(mScroller.getStartX(), mScroller.getStartY(), velocityX, velocityY, 0, totalRectWidth - getWidth(), 0, 0);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public static int getTopNeedCount() {
        return needRectCount;
    }

    @Override
    public void getMoveParams_BODY(int distanceX, int distanceY) {
        smoothScrollBy(distanceX);
    }

    @Override
    public void getUpFling_Body(int velocityX, int velocityY) {
        /*mVelocityTracker.addMovement(event);
        //根据触摸位置计算每像素的移动速率。
        mVelocityTracker.computeCurrentVelocity(1000);*/
        fling(velocityX, -velocityY);
    }

    @Override
    public void getUpScroll_BODY(int distanceX, int distanceY) {
        smoothScrollBy(distanceX);
    }
}


