package test.com.myapplication.newestversion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import test.com.R;


/**
 * Created by zhangsixia on 18/4/22.
 */

public class BodyScrollView extends View {

    private OverScroller mScroller;//用于辅助View拖动或滑行
    private VelocityTracker mVelocityTracker = null; //速度追踪器
    public static final int SNAP_VELOCITY = 600;  //最小的滑动速率,小于这个速率不滑行
    private int RectWidth;
    private int RectHeight;
    private Rect mRect;
    private Paint mPaint;
    private int color;
    private int paintWidth = 2;
    private int needRectCount_X = TopScrollView.getTopNeedCount();
    private int needRectCount_Y = LeftScrollView.getLeftNeedCount();

    public BodyScrollView(Context context) {
        super(context);
        init();
    }

    public BodyScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BodyScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        color = ContextCompat.getColor(getContext(), R.color.green_disable_qb);
        mScroller = new OverScroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
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

    @Override
    public void computeScroll() {
        //返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。这是一个很重要的方法，通常放在View.computeScroll()中，用来判断是否滚动是否结束
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    int lastY,lastX;
    int currentY,currentX;
    int distanceY,distanceX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        //根据触摸位置计算每像素的移动速率。
        mVelocityTracker.computeCurrentVelocity(1000);
        //计算速率
        int velocityX = (int) mVelocityTracker.getXVelocity() * (-1);
        int velocityY = (int) mVelocityTracker.getYVelocity() * (-1);
        //判断操作类别
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当滚动的过程中,按下手指的时候,就停止滚动的动画
                if (mScroller != null) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }
                //获取当前的坐标值
                lastY = (int) event.getY();
                lastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //计算出两次动作间的滑动距离
                currentY = (int) event.getY();
                currentX = (int) event.getX();
                distanceY = currentY - lastY;
                distanceX = currentX - lastX;
                //翻转滑动方向
                distanceY = distanceY * -1;
                distanceX = distanceX * -1;
                //拖动滑动
                smoothScrollBy(distanceX, distanceY);
                mBodyInterface.getMoveParams_Body(distanceX,distanceY);
                //拖动结束时将当前
                lastY = currentY;
                lastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
                //计算出两次动作间的滑动距离
                currentY = (int) event.getY();
                currentX = (int) event.getX();
                distanceX = currentX - lastX;
                distanceX = currentX * -1;
                distanceY = currentY - lastY;
                distanceY = currentY * -1;
                //如果速率大于最小速率要求，执行滑行，否则拖动到位置
                if (Math.abs(velocityY) > SNAP_VELOCITY){
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    fling(velocityX, velocityY);
                    mBodyInterface.getUpFling_Body(velocityX,velocityY);
                } else{
                    smoothScrollBy(distanceX, distanceY);
                    mBodyInterface.getUpScroll_Body(distanceX,distanceY);
                }
                lastY = currentY;
                lastX = currentX;
                break;
        }
        return true;
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
       /* if(scrollY >= totalRectHeight-getHeight()){
            //执行上滑操作,到达底端时执行的操作
            dy = totalRectHeight-getHeight() - mScroller.getFinalY();
        }*/
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 根据瞬时速度，让画布滑行
     */
    public void fling(int velocityX, int velocityY) {
        //最后两个是参数是允许的超过边界值的距离
       // mScroller.fling(mScroller.getFinalX(), mScroller.getFinalY(), velocityX, velocityY, 0, 0, 0, totalRectHeight-getHeight());
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }


    public BodyInterface mBodyInterface;
    public interface BodyInterface {
        void getMoveParams_Body(int distanceX, int distanceY);
        void getUpFling_Body(int velocityX, int velocityY);
        void getUpScroll_Body(int distanceX, int distanceY);
    }
    public void setInterface(BodyInterface mBodyInterface){
        this.mBodyInterface = mBodyInterface;
    }

}
