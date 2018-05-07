package test.com.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 电影票网上选座位的自定义View
 */
public class SeatTable extends View implements AnimationUtils.ZoomAndMoveAfterAnimation {

    public SeatTable(Context context) {
        super(context);
    }

    public SeatTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SeatTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化数据
     */
    int overview_checked;
    int overview_sold;
    int txt_color;
    int seatCheckedResID;
    int seatSoldResID;
    int seatAvailableResID;

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeatTableView);
        overview_checked = typedArray.getColor(R.styleable.SeatTableView_overview_checked, Color.parseColor("#5A9E64"));
        overview_sold = typedArray.getColor(R.styleable.SeatTableView_overview_sold, Color.RED);
        txt_color = typedArray.getColor(R.styleable.SeatTableView_txt_color, Color.WHITE);
        seatCheckedResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_checked, R.drawable.seat_green);
        seatSoldResID = typedArray.getResourceId(R.styleable.SeatTableView_overview_sold, R.drawable.seat_sold);
        seatAvailableResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_available, R.drawable.seat_gray);
        typedArray.recycle();
        //注册动画的监听
        AnimationUtils.getInstance().setInterface(this);
    }

    /**
     * 初始化缩放操作
     */
    private Paint mPaint;
    private Rect mRect_LEFT = new Rect();
    private Rect mRect_BODY = new Rect();
    private Rect mRect_Top = new Rect();
    private Rect Rect_left_top = new Rect();
    private Rect Rect_left_ = new Rect();
    private Rect Rect__top = new Rect();
    private Rect Rect__body = new Rect();
    private float RectHeight_LEFT;
    private float RectHeight_LEFT_TOP;
    private float RectWidth_LEFT_TOP;
    private float topRectWidth;
    private int row = 0;
    private int column = 0;
    private int bigUnitCount = 0;
    private float left_to_top_Distance;
    private float top_to_left_Distance;
    private float body_to_left_Distance;
    private float body_to_top_Distance;
    private float canvas_total_right;
    private float canvas_total_bottom;
    private Matrix matrix = new Matrix();

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(1);
        RectHeight_LEFT_TOP = 100;
        RectWidth_LEFT_TOP = 100;
        RectHeight_LEFT = 150;
        topRectWidth = 200;
        left_to_top_Distance = Rect_left_.top - Rect_left_top.bottom;
        top_to_left_Distance = Rect__top.left - Rect_left_top.right;
        body_to_left_Distance = Rect__body.left - Rect_left_.right;
        body_to_top_Distance = Rect__body.top - Rect__top.bottom;


    }

    /**
     * 设置行和列的数据
     */
    public void setData(int row, int column, int bigUnitCount) {
        this.row = row;
        this.column = column;
        this.bigUnitCount = bigUnitCount;
        init();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取当前开始绘制时的开始时间
        if (row <= 0 || column == 0) {
            return;
        }
        //Body实体类---座位
        drawSeat(canvas);
        //绘制左边一列的条布局view
        drawNumber(canvas);
        //绘制座位前面的电影屏幕
        drawScreen(canvas);
        drawLeftTop(canvas);
    }

    /**
     * 绘制左上角
     */
    void drawLeftTop(Canvas canvas) {
        //获取X,Y轴方向的位移
        for (int i = 0; i < 3; i++) {//绘制行Rect
            float top = i * RectHeight_LEFT_TOP;
            float bottom = top + RectHeight_LEFT_TOP;
            float left = 0;
            float right = left + RectWidth_LEFT_TOP;
            mRect_Top.set((int) left, (int) top, (int) right, (int) bottom);
            mPaint.setColor(Color.GRAY);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawRect(mRect_Top, mPaint);
            drawTextInRect(mRect_Top, canvas, i + 1, 1, TextTag.LEFT_TOP_TEXT);
        }
        int leftRange = (int) (0.00);
        int topRange = (int) (0.00);
        int rightRange = (int) (RectWidth_LEFT_TOP) + leftRange;
        int belowRange = (int) (RectHeight_LEFT * 3) + topRange;
        Rect_left_top.set(leftRange, topRange, rightRange, belowRange);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(Rect_left_top, mPaint);
    }

    /**
     * 绘制左边一列的条布局view
     */
    void drawNumber(Canvas canvas) {
        //获取Y轴方向上的位移   以及Y轴方向上的缩放值
        float translateY = getTranslateY();
        for (int i = 0; i < row; i++) {//绘制行Rect
            float top = i * RectHeight_LEFT + RectHeight_LEFT_TOP * 3 + translateY;
            float bottom = top + RectHeight_LEFT;
            float left = 0;
            float right = RectWidth_LEFT_TOP;
            mRect_LEFT.set((int) left, (int) top, (int) right, (int) bottom);
            mPaint.setColor(Color.GRAY);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawRect(mRect_LEFT, mPaint);
            drawTextInRect(mRect_LEFT, canvas, i + 1, 1, TextTag.LEFT_TEXT);
        }
        int leftRange = (int) (0.00);
        int topRange = (int) (RectHeight_LEFT_TOP * 3 + translateY);
        int rightRange = (int) (RectWidth_LEFT_TOP) + leftRange;
        int belowRange = (int) (RectHeight_LEFT * row) + topRange;
        Rect_left_.set(leftRange, topRange, rightRange, belowRange);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(Rect_left_, mPaint);
    }

    /**
     * 绘制顶部的条布局view
     */
    void drawScreen(Canvas canvas) {
        //获取X,Y轴方向的位移
        float translateX = getTranslateX();
        for (int k = 0; k < bigUnitCount; k++) {
            //循环完成第一次之后进行第二列的绘制,需要改变X周方向上的参数
            float width = k * column * topRectWidth + k * 20;
            for (int i = 0; i < 3; i++) {//绘制行Rect
                float top = i * RectHeight_LEFT_TOP;
                float bottom = top + RectHeight_LEFT_TOP;
                for (int j = 0; j < column; j++) {//绘制列Rect
                    if (i == 0) {
                        if (j == 0) {
                            float left = j * topRectWidth * column + RectWidth_LEFT_TOP + translateX + width;
                            float right = left + topRectWidth * column;
                            mRect_Top.set((int) left, (int) top, (int) right, (int) bottom);
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            mPaint.setColor(Color.WHITE);
                            canvas.drawRect(mRect_Top, mPaint);
                        }
                    } else {
                        float left = j * topRectWidth + RectWidth_LEFT_TOP + translateX + width;
                        float right = left + topRectWidth;
                        mRect_Top.set((int) left, (int) top, (int) right, (int) bottom);
                        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        mPaint.setColor(Color.WHITE);
                        canvas.drawRect(mRect_Top, mPaint);
                    }
                    drawTextInRect(mRect_Top, canvas, i + 1, j + 1, TextTag.TOP_UNIT_TEXT);
                }
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.GRAY);

                int leftLine = (int) (width + RectWidth_LEFT_TOP + translateX);
                int topLine = (int) (i * RectHeight_LEFT_TOP);
                int rightLine = (int) (topRectWidth * column + leftLine);
                int belowLine = topLine;
                canvas.drawLine(leftLine, topLine, rightLine, belowLine, mPaint);
            }
        }
        int leftRange = (int) (RectWidth_LEFT_TOP + translateX) ;
        int topRange = (int) (0.00);
        int rightRange = (int) (topRectWidth * column * bigUnitCount + (bigUnitCount - 1) * 20) + leftRange;
        int belowRange = (int) (RectHeight_LEFT_TOP * 3) + rightRange;
        Rect__top.set(leftRange, topRange, rightRange, belowRange);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(Rect__top, mPaint);
    }

    /***
     * 绘制座位-----及实体数据body
     */
    void drawSeat(Canvas canvas) {
        //获取X,Y轴方向的位移
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        for (int k = 0; k < bigUnitCount; k++) {
            //循环完成第一次之后进行第二列的绘制,需要改变X周方向上的参数
            float width = k * column * topRectWidth + k * 20;
            for (int i = 0; i < row; i++) {//绘制每一行
                float top = i * RectHeight_LEFT + RectHeight_LEFT_TOP * 3 + translateY;
                float bottom = top + RectHeight_LEFT;
                if (bottom < 0 || top > getHeight()) {
                    continue;
                }
                for (int j = 0; j < column; j++) {//绘制每一列
                    float left = j * topRectWidth + RectWidth_LEFT_TOP + width + translateX;
                    float right = left + topRectWidth;
                    if (right < 0 || left > getWidth()) {
                        continue;
                    }
                    mRect_BODY.set((int) left, (int) top, (int) right, (int) bottom);
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mPaint.setColor(Color.WHITE);
                    canvas.drawRect(mRect_BODY, mPaint);
                    drawTextInRect(mRect_BODY, canvas, i + 1, j + 1, TextTag.BODY_TEXT);
                }
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.GRAY);
                int leftLine = (int) (width + RectWidth_LEFT_TOP + translateX);
                int topLine = (int) (RectHeight_LEFT_TOP * 3 + i * RectHeight_LEFT + translateY);
                int rightLine = (int) (topRectWidth * column + leftLine);
                int belowLine = topLine;
                canvas.drawLine(leftLine, topLine, rightLine, belowLine, mPaint);
            }
        }
        int leftRange = (int) (RectWidth_LEFT_TOP + translateX);
        int topRange = (int) (RectHeight_LEFT_TOP * 3 + translateY);
        int rightRange = (int) (topRectWidth * column * bigUnitCount + (bigUnitCount - 1) * 20) + leftRange;
        int belowRange = (int) (RectHeight_LEFT * row) + topRange;
        Rect__body.set(leftRange, topRange, rightRange, belowRange);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(Rect__body, mPaint);
        canvas_total_right = rightRange;
        canvas_total_bottom = belowRange;
    }

    /**
     * 绘制字体
     *
     * @param rect
     * @param canvas
     * @param positionX
     * @param positionY
     * @param mTextTag
     */
    private synchronized void drawTextInRect(Rect rect, Canvas canvas, int positionX, int positionY, TextTag mTextTag) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(20);
        if (TextTag.LEFT_TOP_TEXT.equals(mTextTag)) {
            mPaint.setColor(Color.WHITE);
        }
        if (TextTag.LEFT_TEXT.equals(mTextTag)) {
            mPaint.setColor(Color.WHITE);
        }
        if (TextTag.TOP_UNIT_TEXT.equals(mTextTag)) {
            mPaint.setColor(Color.GRAY);
        }
        if (TextTag.BODY_TEXT.equals(mTextTag)) {
            mPaint.setColor(Color.BLACK);
        }
        String testString = positionY + "0" + positionX;
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline_Y = ((rect.bottom + rect.top) - (fontMetrics.bottom + fontMetrics.top)) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(testString, rect.centerX(), baseline_Y, mPaint);
    }


    int lastX;
    int lastY;
    //是否是点击了屏幕
    boolean isOnClick;
    private int downX, downY;
    private boolean pointer;
    boolean isScaling;
    //标识是否需要绘制概览图
    boolean isDrawOverview = false;
    /**
     * 通过矩阵实现view的平移和缩放
     */
    float[] m = new float[9];
    private float getTranslateX() {
        matrix.getValues(m);
        return m[2];
    }
    private float getTranslateY() {
        matrix.getValues(m);
        return m[5];
    }
    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }
    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[4];
    }

    private Handler handler = new Handler();
    private Runnable hideOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            float translateY = getTranslateY();
            float translateX = getTranslateX();
            while(translateY > 0){
                translateY--;
                invalidate();
            }
            while(translateX > 0){
                translateX--;
                invalidate();
            }
            handler.removeCallbacks(hideOverviewRunnable);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
        super.onTouchEvent(event);
        //获取手指的数量
        int pointerCount = event.getPointerCount();
        //如果手指不少于一个,则触点激活
        if (pointerCount > 1) {
            pointer = true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                downX = x;
                downY = y;
                //显示悬浮窗
                isDrawOverview = true;
                //停止隐藏显示缩略图
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //如果不是缩放也不是点击就执行移动--拖动的效果
                if (!isScaling && !isOnClick) {
                    int downDX = Math.abs(x - downX);
                    int downDY = Math.abs(y - downY);
                    //当滑动大于10的距离,并且手指是一个手指的时候才进行拖动
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        int dx = x - lastX;
                        int dy = y - lastY;
                        /*if (body_to_left_Distance > 0) {
                            if (body_to_top_Distance > 0) {
                                matrix.postTranslate(0, 0);
                            }else{
                                matrix.postTranslate(0, dy);
                            }
                        }else{
                            if (body_to_top_Distance > 0) {
                                matrix.postTranslate(dx, 0);
                            }else{
                                matrix.postTranslate(dx, dy);
                            }
                        }*/
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候,将开启线程继续隐藏悬浮的缩略图-----延迟时常为1.5秒
                int downDX = Math.abs(x - downX);
                int downDY = Math.abs(y - downY);
                if ((downDX > 10 || downDY > 10) && !pointer) {
                    autoScroll();
                }
                break;
        }
        isOnClick = false;
        lastY = y;
        lastX = x;
        return true;
    }

    /**
     * 自动回弹
     * 整个大小不超过控件大小的时候:
     * 往左边滑动,自动回弹到行号右边
     * 往右边滑动,自动回弹到右边
     * 往上,下滑动,自动回弹到顶部
     * <p>
     * 整个大小超过控件大小的时候:
     * 往左侧滑动,回弹到最右边,往右侧滑回弹到最左边
     * 往上滑动,回弹到底部,往下滑动回弹到顶部
     */
    private void autoScroll() {
        float currentSeatBitmapWidth = canvas_total_right * getMatrixScaleX();
        float currentSeatBitmapHeight = canvas_total_bottom * getMatrixScaleY();
        float moveYLength = 0;
        float moveXLength = 0;

        //处理左右滑动的情况
        if (currentSeatBitmapWidth < getWidth()) {
            //缩小的时候
            if (getTranslateX() < 0 || getMatrixScaleX() < RectWidth_LEFT_TOP) {
                //计算要移动的距离
                if (getTranslateX() < 0) {
                    moveXLength = (-getTranslateX()) + RectWidth_LEFT_TOP;
                } else {
                    moveXLength = RectWidth_LEFT_TOP - getTranslateX();
                }
            }
        } else {
            //放大的时候
            if (getTranslateX() < 0 && getTranslateX() + currentSeatBitmapWidth > getWidth()) {
            } else {
                //往左侧滑动
                if (getTranslateX() + currentSeatBitmapWidth < getWidth()) {
                    moveXLength = getWidth() - (getTranslateX() + currentSeatBitmapWidth);
                } else {
                    //右侧滑动
                    moveXLength = -getTranslateX() + RectWidth_LEFT_TOP;
                }
            }
        }

        float startYPosition = RectWidth_LEFT_TOP * 3 * getMatrixScaleY() + getMatrixScaleY() + RectWidth_LEFT_TOP * 3;
        //处理上下滑动
        if (currentSeatBitmapHeight+RectWidth_LEFT_TOP * 3 < getHeight()) {
            if (getTranslateY() < startYPosition) {
                moveYLength = startYPosition - getTranslateY();
            } else {
                moveYLength = -(getTranslateY() - (startYPosition));
            }
        } else {
            if (getTranslateY() < 0 && getTranslateY() + currentSeatBitmapHeight > getHeight()) {
            } else {
                //往上滑动
                if (getTranslateY() + currentSeatBitmapHeight < getHeight()) {
                    moveYLength = getHeight() - (getTranslateY() + currentSeatBitmapHeight);
                } else {
                    moveYLength = -(getTranslateY() - (startYPosition));
                }
            }
        }

        Point start = new Point();
        start.x = (int) getTranslateX();
        start.y = (int) getTranslateY();

        Point end = new Point();
        end.x = (int) (start.x + moveXLength);
        end.y = (int) (start.y + moveYLength);

        AnimationUtils.getInstance().moveAnimate(start, end);
    }

    @Override
    public void animationZooms(float zoom) {
        /*float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, scaleX, scaleY);
        invalidate();*/
    }

    @Override
    public void animationMove(Point p) {
        float x = p.x - getTranslateX();
        float y = p.y - getTranslateY();
        matrix.postTranslate(x, y);
        invalidate();
    }
}
