package test.com.myapplication.selfwordview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangsixia on 18/4/27.
 */

public class SelfGroup extends GridLayout {

    private int textView_Height;
    private int textView_Width;
    private int Unit_Count;//单元的数量
    private int houseCount_In_Unit;//每个单元内户型的数量
    private int floadCount;//楼层
    private int total_count;//TextView的总数量
    private int horizontalDistance;
    private int totalRectHeight_X;
    private int totalRectHeight_Y;
    private Matrix mMetrix = new Matrix();
    private float[] array = new float[9];
    private OverScroller mScroller;//用于辅助View拖动或滑行
    private VelocityTracker mVelocityTracker = null; //速度追踪器

    public SelfGroup(Context context) {
        super(context);
        init();
    }

    public SelfGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelfGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new OverScroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();

        horizontalDistance = 30;

        Unit_Count = 3;
        houseCount_In_Unit = 3;
        floadCount = 50;
        total_count = floadCount * houseCount_In_Unit * Unit_Count;

        for (int i = 0; i < total_count; i++) {
            SelfTextView mTextView = new SelfTextView(getContext());
            /*ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(200,200);
            mTextView.setLayoutParams(layoutParams);*/
            mTextView.setBackgroundColor(Color.WHITE);
            mTextView.setText("0"+i);
            addView(mTextView);
            setListener(mTextView, i);
        }
    }

    @Override
    protected void onLayout(boolean f, int l, int t, int r, int b) {
        int horiDistance = 0;
        for (int w = 0; w < Unit_Count; w++) {
            for (int y = w * houseCount_In_Unit; y < houseCount_In_Unit * (w + 1); y++) {
                for (int j = y * floadCount; j < floadCount * (y + 1); j++) {
                    View childView = getChildAt(j);
                    int top = j * textView_Height - y * floadCount * textView_Height;
                    int bottom = top + textView_Height;
                    int left = textView_Width * y + horiDistance;
                    int right = left + textView_Width;
                    childView.layout(left, top, right, bottom);
                }
            }
            horiDistance += horizontalDistance;
        }
        mBodyInterface2.changeParams(totalRectHeight_X, totalRectHeight_Y, floadCount, houseCount_In_Unit * Unit_Count, getWidth(), getHeight());
    }

    /*Handler mHandler = new Handler();
    private Runnable mSingleClick = new Runnable() {
        @Override
        public void run() {

        }
    };*/
    private long lastTime;
    private long currentTime;
    public void setListener(View mTextView, final int position) {
        mTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemsClickListener.OnitemListener(view, position);
            }
        });
        /*mTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                        long tiems = pressEndTime - pressStartTime;
                        Log.i("TAGtime",tiems+"");
                        mOnItemsClickListener.OnitemListener(view, position);
                return false;
            }
        });*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        textView_Height = height * 1 / 14;
        textView_Width = width * 1 / 6;
        for(int i=0;i < getChildCount();i++){
            SelfTextView mTextView = (SelfTextView) getChildAt(i);
            mTextView.setWidth(textView_Width);
            mTextView.setHeight(textView_Height);
            mTextView.setGravity(Gravity.CENTER);
        }
        measureChildren(textView_Width,textView_Height);
        setMeasuredDimension(width, height);
        totalRectHeight_Y = textView_Height * floadCount;
        totalRectHeight_X = (textView_Width * houseCount_In_Unit + horizontalDistance) * Unit_Count;
    }


    private float getTranslateX() {
        mMetrix.getValues(array);
        return array[2];
    }

    private float getTranslateY() {
        mMetrix.getValues(array);
        return array[5];
    }

    private float dip2Px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }


    @Override
    public void computeScroll() {
        //返回值为boolean，true说明滚动尚未完成，false说明滚动已经完成。这是一个很重要的方法，通常放在View.computeScroll()中，用来判断是否滚动是否结束
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
    private boolean isClick = false;
    //GestureDetector手势的监听
    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        //在按下并抬起时, 单击之后短时间内没有再次单击，才会触发该函数。   双击的时候不会触发
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isClick = true;
            return super.onSingleTapConfirmed(e);
        }
    });
    int lastY, lastX;
    int currentY, currentX;
    int distanceY, distanceX;
    static boolean isScrolling = false;
    float touchDownX;
    float touchDownY;
    long pressStartTime;
    long pressEndTime;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //gestureDetector.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressStartTime = System.currentTimeMillis();
                touchDownX = ev.getX();
                touchDownY = ev.getY();
                isScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float distance_X = touchDownX - ev.getX();
                float distance_Y = touchDownY - ev.getY();
                if(Math.abs(distance_X) >= ViewConfiguration.get(getContext()).getScaledTouchSlop()
                        || Math.abs(distance_Y) >= ViewConfiguration.get(getContext()).getScaledTouchSlop()){
                    isScrolling = true;
                }else{
                    isScrolling = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pressEndTime = System.currentTimeMillis();
                isScrolling = false;
                break;
        }
        return isScrolling;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //gestureDetector.onTouchEvent(event);
        //mVelocityTracker.addMovement(event);
        //根据触摸位置计算每像素的移动速率。
        //mVelocityTracker.computeCurrentVelocity(1000);
        /*//计算速率
        int velocityX = (int) mVelocityTracker.getXVelocity() * (-1);
        int velocityY = (int) mVelocityTracker.getYVelocity() * (-1);*/
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
                smoothScroll(distanceX, distanceY);
                //设置左边和顶部联动
                if (mBodyInterface != null) {
                    mBodyInterface.getMoveParams_BODY(distanceX, distanceY);
                    mBodyInterface1.getMoveParams_BODY(distanceX, distanceY);
                }
                //拖动结束时将当前
                lastY = currentY;
                lastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
                mBodyInterface3.mapStatus(false);
                break;
        }
        return true;
    }

    /**
     * 调用Scroller的startScroll后，Scroller会根据偏移量是时间计算当前的X坐标和Y坐标，执行invalidte会让View执行draw()方法，从而调用computeScroll()方法
     */
    public void smoothScroll(int dx, int dy) {
        Log.i("FinalX","   smoothScrollBy   "+mScroller.getFinalX()+"   "+mScroller.getFinalY());
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
        //Log.i("FinalX","   smoothScrollBy   "+mScroller.getFinalX()+"   "+mScroller.getFinalY());
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy,500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
        mBodyInterface2.changeTranselate(mScroller.getFinalX(), mScroller.getFinalY());
        mBodyInterface3.mapStatus(true);




        /*//设置地图缩略图滚动起来
        if (0 < scrollY && scrollY < totalRectHeight_Y - getHeight() && 0 < scrollX && scrollX < totalRectHeight_X - getWidth()) {
            mMetrix.postTranslate(distanceX, distanceY);
            mMetrix.getValues(array);
            mBodyInterface2.changeTranselate(array[2], array[5]);
            mBodyInterface3.mapStatus(true);
        }*/
       // Log.i("scroller","      "+array[2]+"   "+array[5]);
    }

    public SelfGroup.BodyInterface mBodyInterface;

    public interface BodyInterface {
        void getMoveParams_BODY(int distanceX, int distanceY);

        void getUpFling_Body(int velocityX, int velocityY);

        void getUpScroll_BODY(int distanceX, int distanceY);
    }

    public void setInterface(SelfGroup.BodyInterface mBodyInterface) {
        this.mBodyInterface = mBodyInterface;
    }

    public SelfGroup.BodyInterface1 mBodyInterface1;

    public interface BodyInterface1 {
        void getMoveParams_BODY(int distanceX, int distanceY);

        void getUpFling_Body(int velocityX, int velocityY);

        void getUpScroll_BODY(int distanceX, int distanceY);
    }

    public void setInterface1(SelfGroup.BodyInterface1 mBodyInterface1) {
        this.mBodyInterface1 = mBodyInterface1;
    }

    public SelfGroup.BodyInterface2 mBodyInterface2;

    public interface BodyInterface2 {
        void changeParams(int totalWith, int totalHeight, int row, int column, int parentWidth, int patentHeight);

        void changeTranselate(float transX, float transY);
    }

    public void setInterface2(SelfGroup.BodyInterface2 mBodyInterface2) {
        this.mBodyInterface2 = mBodyInterface2;
    }

    public SelfGroup.BodyInterface3 mBodyInterface3;

    public interface BodyInterface3 {
        void mapStatus(boolean isVisiMap);
    }

    public void setInterface3(SelfGroup.BodyInterface3 mBodyInterface3) {
        this.mBodyInterface3 = mBodyInterface3;
    }

    /**
     * 自定义点击事件
     */
    public OnItemsClickListener mOnItemsClickListener;

    public interface OnItemsClickListener {
        void OnitemListener(View view, int position);
    }

    public void setmOnItemsClickListener(OnItemsClickListener mOnItemsClickListener) {
        this.mOnItemsClickListener = mOnItemsClickListener;
    }
}
