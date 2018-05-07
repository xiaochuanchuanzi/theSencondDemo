package test.com.myapplication.scrollview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import java.util.ArrayList;

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
    private int color = Color.GRAY;
    private int paintWidth = 2;
    private int totalRectHeight_X;
    private int totalRectHeight_Y;
    private Matrix mMetrix = new Matrix();
    private float[] array = new float[9];
    private ArrayList<SelectedStatus> rectSelected = new ArrayList<>();//用于存储所有的Rect坐标位置
    private ArrayList<Rect> rectList = new ArrayList<>();//用于存储所有的Rect坐标位置

    /*获取单例*/
    private static BodyScrollView mBodyScrollView = null;

    public static BodyScrollView getInstance(Context context) {
        synchronized (BodyScrollView.class) {
            if (mBodyScrollView == null) {
                mBodyScrollView = new BodyScrollView(context);
            }
        }
        return mBodyScrollView;
    }

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
        RectWidth = width * 1 / 7;
        RectHeight = height * 1 / 15;

    }

    private float dip2Px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }

    private int bigUnitCount = 3;
    private int column = 3;
    private int row = 50;

    @Override
    protected void onDraw(Canvas canvas) {
        for (int k = 0; k < bigUnitCount; k++) {
            //循环完成第一次之后进行第二列的绘制,需要改变X周方向上的参数
            float width = k * column * RectWidth + k * 20;
            for (int i = 0; i < row; i++) {//绘制每一行
                float top = i * RectHeight;
                float bottom = top + RectHeight;
                for (int j = 0; j < column; j++) {//绘制每一列
                    float left = j * RectWidth + width;
                    float right = left + RectWidth;
                    mRect.set((int) left, (int) top, (int) right, (int) bottom);
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mPaint.setColor(Color.WHITE);
                    if(isClicked && mRect.contains(X_POSITION,Y_POSITION)){
                        mPaint.setColor(Color.GREEN);
                        //并获取当前点击的位置文本内容
                    }
                    canvas.drawRect(mRect, mPaint);
                    drawTextInRect(mRect, canvas, i + 1);
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
        totalRectHeight_Y = RectHeight * row;
        totalRectHeight_X = (RectWidth * column + 20) * bigUnitCount;
        mBodyInterface2.changeParams(totalRectHeight_X, totalRectHeight_Y, row, column * bigUnitCount, getWidth(), getHeight());
    }
    private float getTranslateX() {
        mMetrix.getValues(array);
        return array[2];
    }
    private float getTranslateY() {
        mMetrix.getValues(array);
        return array[5];
    }

    private synchronized void drawTextInRect(Rect rect, Canvas canvas, int positionX) {
        String testString = positionX + "0";
        mPaint.setTextSize(30);
        mPaint.setColor(Color.BLACK);
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

    int lastY, lastX;
    int currentY, currentX;
    int distanceY, distanceX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
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
                if (mBodyInterface != null) {
                    mBodyInterface.getMoveParams_BODY(distanceX, distanceY);
                    mBodyInterface1.getMoveParams_BODY(distanceX, distanceY);
                }
                //拖动结束时将当前
                lastY = currentY;
                lastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
                //计算出两次动作间的滑动距离
                currentY = (int) event.getY();
                currentX = (int) event.getX();
                distanceY = currentY - lastY;
                distanceX = currentX - lastX;
                distanceY = currentY * -1;
                distanceX = currentX * -1;
                mBodyInterface3.mapStatus(false);
               /* //如果速率大于最小速率要求，执行滑行，否则拖动到位置
                if (Math.abs(velocityY) > SNAP_VELOCITY){
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    fling(velocityX, velocityY);
                    if (mBodyInterface != null) {
                        mBodyInterface.getUpFling_Body(velocityX,velocityY);
                        mBodyInterface1.getUpFling_Body(velocityX,velocityY);
                    }
                } else{
                    if (mBodyInterface != null) {
                        mBodyInterface.getUpScroll_BODY(distanceX, distanceY);
                        mBodyInterface1.getUpScroll_BODY(distanceX, distanceY);
                    }
                    smoothScrollBy(distanceX, distanceY);
                }*/
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
        int scrollY = mScroller.getFinalY() + dy;
        if (scrollY < 0) { //执行下滑操作,到达顶端时执行的操作
            dy = -1 * mScroller.getFinalY();
        }
        if (scrollY >= totalRectHeight_Y - getHeight()) {
            //执行上滑操作,到达底端时执行的操作
            dy = totalRectHeight_Y - getHeight() - mScroller.getFinalY();
        }
        int scrollX = mScroller.getFinalX() + dx;
        if (scrollX < 0) { //执行下滑操作,到达顶端时执行的操作
            dx = -1 * mScroller.getFinalX();
        }
        if (scrollX >= totalRectHeight_X - getWidth()) {
            //执行上滑操作,到达底端时执行的操作
            dx = totalRectHeight_X - getWidth() - mScroller.getFinalX();
        }
        //mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果

        if (0 < scrollY && scrollY < totalRectHeight_Y - getHeight() && 0 < scrollX && scrollX < totalRectHeight_X - getWidth()) {
            mMetrix.postTranslate(distanceX, distanceY);
            mMetrix.getValues(array);
            mBodyInterface2.changeTranselate(array[2], array[5]);
            mBodyInterface3.mapStatus(true);
        }
       /* if (0 < scrollX && scrollX < totalRectHeight_X - getWidth()) {
            mMetrix.postTranslate(distanceX, distanceY);
            mMetrix.getValues(array);
            mBodyInterface2.changeTranselate(array[2], array[5]);
            mBodyInterface3.mapStatus(true);
        }*/
    }

    /**
     * 根据瞬时速度，让画布滑行
     */
    public void fling(int velocityX, int velocityY) {
        //最后两个是参数是允许的超过边界值的距离
        mScroller.fling(mScroller.getStartX(), mScroller.getStartY(), velocityX, velocityY, 0, totalRectHeight_X - getWidth(), 0, totalRectHeight_Y - getHeight());
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public BodyInterface mBodyInterface;

    public interface BodyInterface {
        void getMoveParams_BODY(int distanceX, int distanceY);

        void getUpFling_Body(int velocityX, int velocityY);

        void getUpScroll_BODY(int distanceX, int distanceY);
    }

    public void setInterface(BodyInterface mBodyInterface) {
        this.mBodyInterface = mBodyInterface;
    }

    public BodyInterface1 mBodyInterface1;

    public interface BodyInterface1 {
        void getMoveParams_BODY(int distanceX, int distanceY);

        void getUpFling_Body(int velocityX, int velocityY);

        void getUpScroll_BODY(int distanceX, int distanceY);
    }

    public void setInterface1(BodyInterface1 mBodyInterface1) {
        this.mBodyInterface1 = mBodyInterface1;
    }

    public BodyInterface2 mBodyInterface2;

    public interface BodyInterface2 {
        void changeParams(int totalWith, int totalHeight, int row, int column, int parentWidth, int patentHeight);

        void changeTranselate(float transX, float transY);
    }

    public void setInterface2(BodyInterface2 mBodyInterface2) {
        this.mBodyInterface2 = mBodyInterface2;
    }

    public BodyInterface3 mBodyInterface3;

    public interface BodyInterface3 {
        void mapStatus(boolean isVisiMap);
    }

    public void setInterface3(BodyInterface3 mBodyInterface3) {
        this.mBodyInterface3 = mBodyInterface3;
    }

    /**
     * 自定义点击事件
     */
    /**
     * GestureDetector手势的监听
     */
    int X_POSITION = 0;
    int Y_POSITION = 0;
    private boolean isClicked = false;
    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        //在按下并抬起时, 单击之后短时间内没有再次单击，才会触发该函数。   双击的时候不会触发
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            X_POSITION = (int) (e.getX());
            Y_POSITION = (int) (e.getY());
            isClicked = true;
            invalidate();
            Log.i("TAG",X_POSITION+"        "+Y_POSITION);
            return super.onSingleTapConfirmed(e);
        }
    });

}
