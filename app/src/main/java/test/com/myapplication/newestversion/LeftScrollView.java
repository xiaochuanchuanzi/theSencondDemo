package test.com.myapplication.newestversion;

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

/**
 * Created by zhangsixia on 18/4/22.
 */

public class LeftScrollView extends View implements BodyScrollView.BodyInterface{

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
    private int totalRectHeight;


    public LeftScrollView(Context context) {
        super(context);
        init();
    }

    public LeftScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeftScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setRectCount(int rectCount) {
        this.needRectCount = rectCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        RectWidth = width;
        RectHeight = height * 1 / 15;
    }

    int rectCount;
    @Override
    protected void onDraw(Canvas canvas) {
        rectCount = 0;
        canvas.translate(0, 0);
        for (int i = 0; i < needRectCount; i++) {
            int distance = i * RectHeight;
            mRect.set(0, distance, RectWidth, RectHeight + distance);
            mPaint.setColor(Color.GRAY);
            canvas.drawRect(mRect, mPaint);
            drawTextInRect(canvas,i+1);
            rectCount++;
        }
        totalRectHeight = RectHeight*rectCount;
    }

    private void drawTextInRect(Canvas canvas,int position){
        String testString = position+"层";
        mPaint.setTextSize(40);
        mPaint.setColor(Color.RED);
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
    public void smoothScrollBy(int dx, int dy) {
        int scrollY = mScroller.getFinalY()+dy;
        //分三种情况
        if(scrollY < 0) { //执行下滑操作,到达顶端时执行的操作
            dy = -1 * mScroller.getFinalY();
        }
        if(0 < scrollY && scrollY < getHeight()){//正常情况下:未到顶部,未到底部

        }
        if(scrollY >= totalRectHeight-getHeight()){
            //执行上滑操作,到达底端时执行的操作
            dy = totalRectHeight-getHeight() - mScroller.getFinalY();
        }
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 根据瞬时速度，让画布滑行
     */
    public void fling(int velocityX, int velocityY) {
        //最后两个是参数是允许的超过边界值的距离
        mScroller.fling(mScroller.getFinalX(), mScroller.getFinalY(), velocityX, velocityY, 0, 0, 0, totalRectHeight-getHeight());
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    public static int getLeftNeedCount(){
        return needRectCount;
    }


    @Override
    public void getMoveParams_Body(int distanceX, int distanceY) {
        smoothScrollBy(0, distanceY);
    }

    @Override
    public void getUpFling_Body(int velocityX, int velocityY) {
        fling(velocityX, velocityY);
    }

    @Override
    public void getUpScroll_Body(int distanceX, int distanceY) {
        smoothScrollBy(0, distanceY);
    }
}
