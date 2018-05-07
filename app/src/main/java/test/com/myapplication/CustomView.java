package test.com.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

/**
 * Created by zhangsixia on 18/4/21.
 */

public class CustomView extends View {
    Paint paint = new Paint();
    OverScroller mScroller; //用于辅助View拖动或滑行
    //处理触摸的速率
    private VelocityTracker mVelocityTracker = null; //速度追踪器
    public static final int SNAP_VELOCITY = 600;  //最小的滑动速率,小于这个速率不滑行

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mScroller = new OverScroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, 1080, getMeasuredHeight(), paint);

        paint.setColor(Color.YELLOW);
        canvas.drawRect(1080, 0, 2160, getMeasuredHeight(), paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(2160, 0, 3240, getMeasuredHeight(), paint);
    }


    int lastX;
    int currentX;
    int distanceX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当滚动的过程中,按下手指的时候,就停止滚动的动画
                if (mScroller != null) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }
                //获取当前的坐标值
                lastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //计算出两次动作间的滑动距离
                currentX = (int) event.getX();
                distanceX = currentX - lastX;
                //翻转滑动方向
                distanceX = distanceX * -1;
                //拖动滑动
                smoothScrollBy(distanceX, 0);
                //拖动结束时将当前
                lastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
                //根据触摸位置计算每像素的移动速率。
                mVelocityTracker.computeCurrentVelocity(1000);
                //计算速率
                int velocityX = (int) mVelocityTracker.getXVelocity() * (-1);
                int velocityY = (int) mVelocityTracker.getYVelocity();

                //计算出两次动作间的滑动距离
                currentX = (int) event.getX();
                distanceX = currentX - lastX;
                distanceX = distanceX * -1;
                //如果速率大于最小速率要求，执行滑行，否则拖动到位置
                if (Math.abs(velocityX) > SNAP_VELOCITY) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    fling(velocityX, velocityY);
                } else {
                    smoothScrollBy(distanceX, 0);
                }
                lastX = currentX;
//
                break;
        }
        return true;
    }

    /**
     * 调用Scroller的startScroll后，Scroller会根据偏移量是时间计算当前的X坐标和Y坐标，执行invalidte会让View执行draw()方法，从而调用computeScroll()方法
     *
     * @param dx
     * @param dy
     */
    public void smoothScrollBy(int dx, int dy) {
        if (mScroller.getFinalX() + dx > 2160) {
            dx = 2160 - mScroller.getFinalX();
        } else if (mScroller.getFinalX() + dx < 0) {
            dx = -1 * mScroller.getFinalX();
        }
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 根据瞬时速度，让画布滑行
     *
     * @param velocityX X轴速度，有正负方向，正数画布左移
     * @param velocityY
     */
    public void fling(int velocityX, int velocityY) {
        //最后两个是参数是允许的超过边界值的距离
        mScroller.fling(mScroller.getFinalX(), mScroller.getFinalY(), velocityX, velocityY, 0, 2160, 0, 0, 200, 200);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 当draw()调用时此方法被调用，用来将内容滚动到Scroller的当前坐标
     */
    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }



}
