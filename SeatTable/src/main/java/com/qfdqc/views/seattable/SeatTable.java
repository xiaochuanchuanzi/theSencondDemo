package com.qfdqc.views.seattable;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 电影票网上选座位的自定义View
 */
public class SeatTable extends View implements AnimationUtils.ZoomAndMoveAfterAnimation{
    private final boolean DBG = false;//是否获取绘制所花费的时间
    //整个视图的画笔
    Paint paint = new Paint();
    //用于绘制概览图的画笔
    Paint overviewPaint=new Paint();
    //用于绘制数字的画笔
    Paint lineNumberPaint;
    float lineNumberTxtHeight;
    /**
     * 设置行号 默认显示 1,2,3....数字
     */
    public void setLineNumbers(ArrayList<String> lineNumbers) {
        this.lineNumbers = lineNumbers;
        invalidate();
    }
    /**
     * 用来保存所有行号
     */
    ArrayList<String> lineNumbers = new ArrayList<>();
    Paint.FontMetrics lineNumberPaintFontMetrics;
    Matrix matrix = new Matrix();

    //座位水平间距
    int spacing;
    //座位垂直间距
    int verSpacing;
    //行号宽度
    int numberWidth;
    //行数
    int row;
    //列数
    int column;
    /**
     * 可选时座位的图片
     */
    Bitmap seatBitmap;
    //选中时座位的图片
    Bitmap checkedSeatBitmap;
    //座位已经售出时的图片
    Bitmap seatSoldBitmap;

    Bitmap overviewBitmap;
    int lastX;
    int lastY;
    /**
     * 整个座位图的宽度
     */
    int seatBitmapWidth;
    //整个座位图的高度
    int seatBitmapHeight;

    /**
     * 标识是否需要绘制座位图
     */
    boolean isNeedDrawSeatBitmap = true;


    /**
     * 荧幕高度
     */
    float screenHeight;
    //荧幕默认宽度与座位图的比例
    float screenWidthScale = 0.5f;
    //荧幕最小宽度
    int defaultScreenWidth;

    /**
     * 标识是否正在缩放
     */
    boolean isScaling;
    float scaleX, scaleY;

    /**
     * 是否是第一次缩放
     */
    boolean firstScale = true;

    /**
     * 最多可以选择的座位数量
     */
    int maxSelected = Integer.MAX_VALUE;
    //对座位状态的检查
    private SeatChecker seatChecker;

    /**
     * 荧幕名称
     */
    private String screenName = "";




    Paint headPaint;
    Bitmap headBitmap;

    /**
     * 概览图白色方块高度
     */
    float rectHeight;
    //概览图白色方块的宽度
    float rectWidth;
    //概览图上方块的水平间距
    float overviewSpacing;
    //概览图上方块的垂直间距
    float overviewVerSpacing;
    //概览图的比例
    float overviewScale = 4.8f;
    //整个概览图的高度
    float rectH;
    //整个概览图的宽度
    float rectW;
    //标识是否需要绘制概览图
    boolean isDrawOverview = false;
    //标识是否需要更新概览图
    boolean isDrawOverviewBitmap = true;
    //概览图的检查及状态
    int overview_checked;
    int overview_sold;
    int txt_color;
    int seatCheckedResID;
    int seatSoldResID;
    int seatAvailableResID;

    //是否是点击了屏幕
    boolean isOnClick;

    /**
     * 座位已售
     */
    private static final int SEAT_TYPE_SOLD = 1;

    /**
     * 座位已经选中
     */
    private static final int SEAT_TYPE_SELECTED = 2;

    /**
     * 座位可选
     */
    private static final int SEAT_TYPE_AVAILABLE = 3;

    /**
     * 座位不可用
     */
    private static final int SEAT_TYPE_NOT_AVAILABLE = 4;

    private int downX, downY;
    private boolean pointer;

    /**
     * 顶部高度,可选,已选,已售区域的高度
     */
    float headHeight;

    Paint pathPaint;
    RectF rectF;

    /**
     * 头部下面横线的高度
     */
    int borderHeight = 1;
    Paint redBorderPaint;

    /**
     * 默认的座位图宽度,如果使用的自己的座位图片比这个尺寸大或者小,会缩放到这个大小
     */
    private float defaultImgW = 40;

    /**
     * 默认的座位图高度
     */
    private float defaultImgH = 34;

    /**
     * 座位图片的宽度
     */
    private int seatWidth;

    /**
     * 座位图片的高度
     */
    private int seatHeight;

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

    public SeatTable(Context context) {
        super(context);
    }

    public SeatTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public SeatTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    /**
     * 初始化数据
     * @param context
     * @param attrs
     */
    private void init(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeatTableView);
        overview_checked = typedArray.getColor(R.styleable.SeatTableView_overview_checked, Color.parseColor("#5A9E64"));
        overview_sold = typedArray.getColor(R.styleable.SeatTableView_overview_sold, Color.RED);
        txt_color=typedArray.getColor(R.styleable.SeatTableView_txt_color,Color.WHITE);
        seatCheckedResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_checked, R.drawable.seat_green);
        seatSoldResID = typedArray.getResourceId(R.styleable.SeatTableView_overview_sold, R.drawable.seat_sold);
        seatAvailableResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_available, R.drawable.seat_gray);
        typedArray.recycle();
        //注册动画的监听
        AnimationUtils.getInstance().setInterface(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取当前开始绘制时的开始时间
        long startTime = System.currentTimeMillis();
        if (row <= 0 || column == 0) {
            return;
        }
        //Body实体类---座位
        drawSeat(canvas);
        //绘制左边一列的条布局view
        drawNumber(canvas);
        //绘制最顶部的三种状态的顶部布局
        if (headBitmap == null) {
            headBitmap = drawHeadInfo();
        }
        //绘制顶部显示的用于显示三种状态的头部布局
        canvas.drawBitmap(headBitmap, 100, 0, null);
        //绘制座位前面的电影屏幕
        drawScreen(canvas);
        //是否需要绘制概览图----悬浮的缩略图
        if (isDrawOverview) {
            long s = System.currentTimeMillis();
            //是否需要更新概览图
            if (isDrawOverviewBitmap) {
                //获取概览图---悬浮的缩略图
                drawOverview();
            }
            /**
             * 将Bitmap悬浮的缩略图---绘制到左上角
             */
            canvas.drawBitmap(overviewBitmap, 0, 0, null);
            drawOverview(canvas);
        }
        if (DBG) {
            long drawTime = System.currentTimeMillis() - startTime;
            Log.d("drawTime", "totalDrawTime:" + drawTime);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
        super.onTouchEvent(event);
        //将事件传递给缩放的手势监听器和多手势操作的监听器
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
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
                handler.removeCallbacks(hideOverviewRunnable);
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
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候,将开启线程继续隐藏悬浮的缩略图-----延迟时常为1.5秒
                handler.postDelayed(hideOverviewRunnable, 1500);
                autoScale();
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


    private Runnable hideOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            isDrawOverview = false;
            invalidate();
        }
    };

    /**
     * 获取最顶部呈现三种状态的顶部布局需要的图片
     */
    Bitmap drawHeadInfo() {
        String txt = "已售";
        float txtY = getBaseLine(headPaint, 0, headHeight);
        int txtWidth = (int) headPaint.measureText(txt);
        float spacing = dip2Px(10);
        float spacing1 = dip2Px(5);
        float y = (headHeight - seatBitmap.getHeight()) / 2;
        float width = seatBitmap.getWidth() + spacing1 + txtWidth + spacing + seatSoldBitmap.getWidth() + txtWidth + spacing1 + spacing + checkedSeatBitmap.getHeight() + spacing1 + txtWidth;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), (int) headHeight, Bitmap.Config.ARGB_8888);
        //绘制已售的文字和图片
        Canvas canvas = new Canvas(bitmap);

        //绘制背景Rect
        canvas.drawRect(0, 0, getWidth(), headHeight, headPaint);

        headPaint.setColor(Color.BLACK);
        float startX = (getWidth() - width) / 2;
        tempMatrix.setScale(xScale1,yScale1);
        tempMatrix.postTranslate(startX,(headHeight - seatHeight) / 2);
        canvas.drawBitmap(seatBitmap, tempMatrix, headPaint);
        canvas.drawText("可选", startX + seatWidth + spacing1, txtY, headPaint);

        float soldSeatBitmapY = startX + seatBitmap.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(xScale1,yScale1);
        tempMatrix.postTranslate(soldSeatBitmapY,(headHeight - seatHeight) / 2);
        canvas.drawBitmap(seatSoldBitmap, tempMatrix, headPaint);
        canvas.drawText("已售", soldSeatBitmapY + seatWidth + spacing1, txtY, headPaint);

        float checkedSeatBitmapX = soldSeatBitmapY + seatSoldBitmap.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(xScale1,yScale1);
        tempMatrix.postTranslate(checkedSeatBitmapX,y);
        canvas.drawBitmap(checkedSeatBitmap, tempMatrix, headPaint);
        canvas.drawText("已选", checkedSeatBitmapX + spacing1 + seatWidth, txtY, headPaint);

        //绘制分割线
        headPaint.setStrokeWidth(1);
        headPaint.setColor(Color.GRAY);
        canvas.drawLine(0, headHeight, getWidth(), headHeight, headPaint);
        return bitmap;

    }

    /**
     * 绘制中间屏幕
     */
    void drawScreen(Canvas canvas) {
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));
        //Y轴方向上开始的距离是最顶部布局的高度+中间的一条线
        float startY = headHeight + borderHeight;

        float centerX = seatBitmapWidth * getMatrixScaleX() / 2 + getTranslateX();
        float screenWidth = seatBitmapWidth * screenWidthScale * getMatrixScaleX();
        if (screenWidth < defaultScreenWidth) {
            screenWidth = defaultScreenWidth;
        }

        Path path = new Path();
        path.moveTo(centerX, startY);
        path.lineTo(centerX - screenWidth / 2, startY);
        path.lineTo(centerX - screenWidth / 2 + 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2 - 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2, startY);

        canvas.drawPath(path, pathPaint);

        pathPaint.setColor(Color.BLACK);
        pathPaint.setTextSize(20 * getMatrixScaleX());

        canvas.drawText(screenName, centerX - pathPaint.measureText(screenName) / 2, getBaseLine(pathPaint, startY, startY + screenHeight * getMatrixScaleY()), pathPaint);
    }

    Matrix tempMatrix = new Matrix();
    private float zoom;
    /***
     * 绘制座位-----及实体数据body
     */
    void drawSeat(Canvas canvas) {
        //获得X轴方向上的缩放
        zoom = getMatrixScaleX();
        //获取开始时间
        long startTime = System.currentTimeMillis();
        //获取X,Y轴方向的位移
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        //获取X,Y轴方向的缩放
        float scaleX = zoom;
        float scaleY = zoom;

        for (int i = 0; i < row; i++) {//绘制每一行
            //座位的高度*初始的缩放比例*Y轴的变化缩放率+ 每行座位的垂直间距*Y轴变化的缩放率+Y轴方向的位移
            float top = i * seatBitmap.getHeight() * yScale1 * scaleY + i * verSpacing * scaleY + translateY;
            //top + 座位的高度*Y轴的缩放率
            float bottom = top + seatBitmap.getHeight() * yScale1 * scaleY;
            /**
             * 判断跳过本次循环绘制,进行下一行------------为了控制最多绘制一页这么大
             */
            if (bottom < 0 || top > getHeight()) {
                continue;
            }
            //绘制每一列
            for (int j = 0; j < column; j++) {
                //left = 座位图的宽度 * X轴的缩放率  +  座位之间的水平距离*X轴上的缩放率 + X轴方向上的位移
                float left = j * seatBitmap.getWidth() * xScale1 * scaleX + j * spacing * scaleX + translateX;
                //right = left + 座位的宽度 * X轴上的缩放率*Y轴上的缩放率
                float right = left + seatBitmap.getWidth() * xScale1 * scaleX;
                /**
                 * 判断跳过本次循环绘制,进行下一列------------为了控制最多绘制一页这么大
                 */
                if (right < 0 || left > getWidth()) {
                    continue;
                }

                int seatType = getSeatType(i, j);
                //相对左上角坐标原点进行向右下方的移动
                tempMatrix.setTranslate(left, top);
                //进行初始缩放率的缩放
                tempMatrix.postScale(xScale1, yScale1, left, top);
                /**
                 *进行拉伸回弹效果的缩放
                 */
                tempMatrix.postScale(scaleX, scaleY, left, top);
                //通过座位的类型----在座位的位置上绘制不同的状态
                switch (seatType) {
                    case SEAT_TYPE_AVAILABLE:
                        canvas.drawBitmap(seatBitmap, tempMatrix, paint);
                        break;
                    case SEAT_TYPE_NOT_AVAILABLE:
                        break;
                    case SEAT_TYPE_SELECTED:
                        canvas.drawBitmap(checkedSeatBitmap, tempMatrix, paint);
                        drawText(canvas, i, j, top, left);
                        break;
                    case SEAT_TYPE_SOLD:
                        canvas.drawBitmap(seatSoldBitmap, tempMatrix, paint);
                        break;
                }

            }
        }
        if (DBG) {
            long drawTime = System.currentTimeMillis() - startTime;
            Log.d("drawTime", "seatDrawTime:" + drawTime);
        }
    }

    /**
     * 获取座位的类型--是否可以进行购买
     */
    private int getSeatType(int row, int column) {
        if (isHave(getID(row, column)) >= 0) {
            return SEAT_TYPE_SELECTED;//座位已经被选中
        }
        if (seatChecker != null) {
            if (!seatChecker.isValidSeat(row, column)) {
                //返回座位不可用
                return SEAT_TYPE_NOT_AVAILABLE;
            } else if (seatChecker.isSold(row, column)) {
                //座位已被卖出
                return SEAT_TYPE_SOLD;
            }
        }
        //否则返回座位是可选的
        return SEAT_TYPE_AVAILABLE;
    }

    /**
     * 返回每个座位的位置ID
     */
    private int getID(int row, int column) {
        return row * this.column + (column + 1);
    }

    /**
     * 绘制选中座位的行号列号
     *
     * @param row
     * @param column
     */
    private void drawText(Canvas canvas, int row, int column, float top, float left) {

        String txt = (row + 1) + "排";
        String txt1 = (column + 1) + "座";

        if(seatChecker!=null){
            String[] strings = seatChecker.checkedSeatTxt(row, column);
            if(strings!=null&&strings.length>0){
                if(strings.length>=2){
                    txt=strings[0];
                    txt1=strings[1];
                }else {
                    txt=strings[0];
                    txt1=null;
                }
            }
        }

        TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(txt_color);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float seatHeight = this.seatHeight * getMatrixScaleX();
        float seatWidth = this.seatWidth * getMatrixScaleX();
        txtPaint.setTextSize(seatHeight / 3);

        //获取中间线
        float center = seatHeight / 2;
        float txtWidth = txtPaint.measureText(txt);
        float startX = left + seatWidth / 2 - txtWidth / 2;

        //只绘制一行文字
        if(txt1==null){
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + seatHeight), txtPaint);
        }else {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + center), txtPaint);
            canvas.drawText(txt1, startX, getBaseLine(txtPaint, top + center, top + center + seatHeight / 2), txtPaint);
        }

        if (DBG) {
            Log.d("drawTest:", "top:" + top);
        }
    }

    int bacColor = Color.parseColor("#7e000000");

    /**
     * 绘制左边一列的条布局view
     */
    void drawNumber(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        lineNumberPaint.setColor(bacColor);
        //获取Y轴方向上的位移   以及Y轴方向上的缩放值
        int translateY = (int) getTranslateY();
        float scaleY = getMatrixScaleY();
        //只支持上下的平移和缩放
        rectF.top = translateY - lineNumberTxtHeight / 2;
        rectF.bottom = translateY + (seatBitmapHeight * scaleY) + lineNumberTxtHeight / 2;
        rectF.left = 0;
        rectF.right = numberWidth;
        //绘制一个带圆角的形状
        canvas.drawRoundRect(rectF, numberWidth / 2, numberWidth / 2, lineNumberPaint);
        //接下来将画笔设置成白色就行字体的绘制
        lineNumberPaint.setColor(Color.WHITE);
        for (int i = 0; i < row; i++) {
            float top = (i *seatHeight + i * verSpacing) * scaleY + translateY;
            float bottom = (i * seatHeight + i * verSpacing + seatHeight) * scaleY + translateY;
            float baseline = (bottom + top - lineNumberPaintFontMetrics.bottom - lineNumberPaintFontMetrics.top) / 2;
            canvas.drawText(lineNumbers.get(i), numberWidth / 2, baseline, lineNumberPaint);
        }
        if (DBG) {
            long drawTime = System.currentTimeMillis() - startTime;
            Log.d("drawTime", "drawNumberTime:" + drawTime);
        }
    }

    /**
     * 在绘制好的概览图上面进行动态的绘制动图--就像地图一样
     */
    void drawOverview(Canvas canvas) {
        //绘制红色框
        //获取相反方向上X轴的位移
        int left = (int) -getTranslateX();
        if (left < 0) {
            left = 0;
        }
        //概览图的缩放比例以及X轴方向的缩放
        left /= overviewScale;
        left /= getMatrixScaleX();

        int currentWidth = (int) (getTranslateX() + (column * seatWidth + spacing * (column - 1)) * getMatrixScaleX());
        if (currentWidth > getWidth()) {
            currentWidth = currentWidth - getWidth();
        } else {
            currentWidth = 0;
        }
        int right = (int) (rectW - currentWidth / overviewScale / getMatrixScaleX());

        float top = -getTranslateY() + headHeight;
        if (top < 0) {
            top = 0;
        }
        top /= overviewScale;
        top /= getMatrixScaleY();
        if (top > 0) {
            top += overviewVerSpacing;
        }

        int currentHeight = (int) (getTranslateY() + (row * seatHeight + verSpacing * (row - 1)) * getMatrixScaleY());
        if (currentHeight > getHeight()) {
            currentHeight = currentHeight - getHeight();
        } else {
            currentHeight = 0;
        }
        int bottom = (int) (rectH - currentHeight / overviewScale / getMatrixScaleY());

        canvas.drawRect(left, top, right, bottom, redBorderPaint);
    }

    /**
     * 获取悬浮的缩略图
     * @return
     */
    Bitmap drawOverview() {
        //默认设置为--不去更新缩略图的联动效果
        isDrawOverviewBitmap = false;

        int bac = Color.parseColor("#7e000000");
        overviewPaint.setColor(bac);
        overviewPaint.setAntiAlias(true);
        overviewPaint.setStyle(Paint.Style.FILL);
        overviewBitmap.eraseColor(Color.TRANSPARENT);
        //创建一个画布--及悬浮框的Bitmap
        Canvas canvas = new Canvas(overviewBitmap);
        //绘制透明灰色背景
        canvas.drawRect(0, 0, rectW, rectH, overviewPaint);
        //再绘制里面的座位小圆白点---以及座位是否被选中的状态
        overviewPaint.setColor(Color.WHITE);
        for (int i = 0; i < row; i++) {
            //概览图中缩小了的座位的尺寸及位置
            float top = i * rectHeight + i * overviewVerSpacing + overviewVerSpacing;
            for (int j = 0; j < column; j++) {
                int seatType = getSeatType(i, j);
                switch (seatType) {
                    case SEAT_TYPE_AVAILABLE:
                        overviewPaint.setColor(Color.WHITE);
                        break;
                    case SEAT_TYPE_NOT_AVAILABLE:
                        continue;
                    case SEAT_TYPE_SELECTED:
                        overviewPaint.setColor(overview_checked);
                        break;
                    case SEAT_TYPE_SOLD:
                        overviewPaint.setColor(overview_sold);
                        break;
                }
                float left = j * rectWidth + j * overviewSpacing + overviewSpacing;
                canvas.drawRect(left, top, left + rectWidth, top + rectHeight, overviewPaint);
            }
        }

        return overviewBitmap;
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
        float currentSeatBitmapWidth = seatBitmapWidth * getMatrixScaleX();
        float currentSeatBitmapHeight = seatBitmapHeight * getMatrixScaleY();
        float moveYLength = 0;
        float moveXLength = 0;

        //处理左右滑动的情况
        if (currentSeatBitmapWidth < getWidth()) {
            //缩小的时候
            if (getTranslateX() < 0 || getMatrixScaleX() < numberWidth + spacing) {
                //计算要移动的距离
                if (getTranslateX() < 0) {
                    moveXLength = (-getTranslateX()) + numberWidth + spacing;
                } else {
                    moveXLength = numberWidth + spacing - getTranslateX();
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
                    moveXLength = -getTranslateX() + numberWidth + spacing;
                }
            }
        }

        float startYPosition = screenHeight * getMatrixScaleY() + verSpacing * getMatrixScaleY() + headHeight + borderHeight;
        //处理上下滑动
        if (currentSeatBitmapHeight+headHeight < getHeight()) {
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

    /**
     * 当手指离开手机屏幕的时候进行自动的缩放
     */
    private void autoScale() {
        if (getMatrixScaleX() > 2.2) {
            AnimationUtils.getInstance().zoomAnimate(getMatrixScaleX(), 2.0f);
        } else if (getMatrixScaleX() < 0.98) {
            AnimationUtils.getInstance().zoomAnimate(getMatrixScaleX(), 1.0f);
        }
    }

    Handler handler = new Handler();
    //存储已选择了的座位集合
    ArrayList<Integer> selects = new ArrayList<>();

    public ArrayList<String> getSelectedSeat(){
        ArrayList<String> results=new ArrayList<>();
        for(int i=0;i<this.row;i++){
            for(int j=0;j<this.column;j++){
                if(isHave(getID(i,j))>=0){
                    results.add(i+","+j);
                }
            }
        }
        return results;
    }

    /**
     * 查找选中的座位当中是否有当前的这个位置
     */
    private int isHave(Integer seat) {
        return Collections.binarySearch(selects, seat);
    }

    /**
     * 删除选中的座位
     * @param index
     */
    private void remove(int index) {
        selects.remove(index);
    }

    private float dip2Px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        int baseline = (int) ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseline;
    }

    @Override
    public void animationZooms(float zoom) {
        zoom(zoom);
    }
    @Override
    public void animationMove(Point p) {
        move(p);
    }
    private void zoom(float zoom) {
        float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, scaleX, scaleY);
        invalidate();
    }
    private void move(Point p) {
        float x = p.x - getTranslateX();
        float y = p.y - getTranslateY();
        matrix.postTranslate(x, y);
        invalidate();
    }

    /**
     * 设置行和列的数据
     * @param row
     * @param column
     */
    public void setData(int row, int column) {
        this.row = row;
        this.column = column;
        init();
        invalidate();
    }

    /**
     * 初始化缩放操作
     */
    float xScale1 = 1;
    float yScale1 = 1;
    private void init() {
        spacing = (int) dip2Px(5);
        verSpacing = (int) dip2Px(10);
        defaultScreenWidth = (int) dip2Px(80);

        seatBitmap = BitmapFactory.decodeResource(getResources(), seatAvailableResID);

        float scaleX = defaultImgW / seatBitmap.getWidth();
        float scaleY = defaultImgH / seatBitmap.getHeight();
        xScale1 = scaleX;
        yScale1 = scaleY;

        seatHeight= (int) (seatBitmap.getHeight()*yScale1);
        seatWidth= (int) (seatBitmap.getWidth()*xScale1);

        checkedSeatBitmap = BitmapFactory.decodeResource(getResources(), seatCheckedResID);
        seatSoldBitmap = BitmapFactory.decodeResource(getResources(), seatSoldResID);

        seatBitmapWidth = (int) (column * seatBitmap.getWidth()*xScale1 + (column - 1) * spacing);
        seatBitmapHeight = (int) (row * seatBitmap.getHeight()*yScale1 + (row - 1) * verSpacing);
        paint.setColor(Color.RED);
        numberWidth = (int) dip2Px(20);

        screenHeight = dip2Px(20);
        headHeight = dip2Px(30);

        headPaint = new Paint();
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setTextSize(24);
        headPaint.setColor(Color.WHITE);
        headPaint.setAntiAlias(true);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));

        redBorderPaint = new Paint();
        redBorderPaint.setAntiAlias(true);
        redBorderPaint.setColor(Color.RED);
        redBorderPaint.setStyle(Paint.Style.STROKE);
        redBorderPaint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);

        rectF = new RectF();

        rectHeight = seatHeight / overviewScale;
        rectWidth = seatWidth / overviewScale;
        overviewSpacing = spacing / overviewScale;
        overviewVerSpacing = verSpacing / overviewScale;

        rectW = column * rectWidth + (column - 1) * overviewSpacing + overviewSpacing * 2;
        rectH = row * rectHeight + (row - 1) * overviewVerSpacing + overviewVerSpacing * 2;
        overviewBitmap = Bitmap.createBitmap((int) rectW, (int) rectH, Bitmap.Config.ARGB_4444);

        lineNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineNumberPaint.setColor(bacColor);
        lineNumberPaint.setTextSize(getResources().getDisplayMetrics().density * 16);
        lineNumberTxtHeight = lineNumberPaint.measureText("4");
        lineNumberPaintFontMetrics = lineNumberPaint.getFontMetrics();
        lineNumberPaint.setTextAlign(Paint.Align.CENTER);

        if(lineNumbers==null){
            lineNumbers=new ArrayList<>();
        }else if(lineNumbers.size()<=0) {
            for (int i = 0; i < row; i++) {
                lineNumbers.add((i + 1) + "");
            }
        }

        matrix.postTranslate(numberWidth + spacing, headHeight + screenHeight + borderHeight + verSpacing);
    }

    /**
     * 两个手指进行缩放的ScaleGestureDetector手势操作
     */
    ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;
            ////与上次事件相比，得到的比例因子
            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (firstScale) {
                scaleX = detector.getCurrentSpanX();
                scaleY = detector.getCurrentSpanY();
                firstScale = false;
            }

            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = 0.5f / getMatrixScaleY();
            }
            matrix.postScale(scaleFactor, scaleFactor, scaleX, scaleY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
            firstScale = true;
        }
    });
    /**
     * GestureDetector手势的监听
     */
    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        //在按下并抬起时,   单击之后短时间内没有再次单击，才会触发该函数。   双击的时候不会触发
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isOnClick = true;
            int x = (int) e.getX();
            int y = (int) e.getY();
            //发生的单点事件之后
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    //循环找到找到被点击了的那个座位
                    int tempX = (int) ((j * seatWidth + j * spacing) * getMatrixScaleX() + getTranslateX());
                    int maxTemX = (int) (tempX + seatWidth * getMatrixScaleX());
                    int tempY = (int) ((i * seatHeight + i * verSpacing) * getMatrixScaleY() + getTranslateY());
                    int maxTempY = (int) (tempY + seatHeight * getMatrixScaleY());
                    //如果当前的坐标是个座位的位置,并且是未被售出的情况下,改变座位的选择状态
                    if (seatChecker != null && seatChecker.isValidSeat(i, j) && !seatChecker.isSold(i, j)) {
                        if (x >= tempX && x <= maxTemX && y >= tempY && y <= maxTempY) {
                            int id = getID(i, j);
                            int index = isHave(id);
                            if (index >= 0) {
                                remove(index);
                                if (seatChecker != null) {
                                    seatChecker.unCheck(i, j);
                                }
                            } else {
                                if (selects.size() >= maxSelected) {
                                    Toast.makeText(getContext(), "最多只能选择" + maxSelected + "个", Toast.LENGTH_SHORT).show();
                                    return super.onSingleTapConfirmed(e);
                                } else {
                                    addChooseSeat(i, j);
                                    if (seatChecker != null) {
                                        seatChecker.checked(i, j);
                                    }
                                }
                            }
                            isNeedDrawSeatBitmap = true;
                            isDrawOverviewBitmap = true;
                            float currentScaleY = getMatrixScaleY();

                            if (currentScaleY < 1.7) {
                                scaleX = x;
                                scaleY = y;
                                AnimationUtils.getInstance().zoomAnimate(currentScaleY, 1.9f);
                            }
                            invalidate();
                            break;
                        }
                    }
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    });

    /**
     * 添加选择的座位
     */
    private void addChooseSeat(int row, int column) {
        int id = getID(row, column);
        for (int i = 0; i < selects.size(); i++) {
            int item = selects.get(i);
            if (id < item) {
                selects.add(i, id);
                return;
            }
        }
        selects.add(id);
    }

    public interface SeatChecker {
        /**
         * 当前坐标是否是座位所处的位置
         */
        boolean isValidSeat(int row, int column);
        /**
         * 座位是否已售
         */
        boolean isSold(int row, int column);
        void checked(int row, int column);
        void unCheck(int row, int column);
        /**
         * 获取选中后座位上显示的文字
         * @return 返回2个元素的数组,第一个元素是第一行的文字,第二个元素是第二行文字,如果只返回一个元素则会绘制到座位图的中间位置
         */
        String[] checkedSeatTxt(int row,int column);

    }

    /**
     * 设置屏幕的名字
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * 设置最多可选的数目
     */
    public void setMaxSelected(int maxSelected) {
        this.maxSelected = maxSelected;
    }

    /**
     * 对座位状态的检查
     */
    public void setSeatChecker(SeatChecker seatChecker) {
        this.seatChecker = seatChecker;
        invalidate();
    }

    /**
     * 根据行数  获取行号
     */
    private int getRowNumber(int row){
        int result=row;
        if(seatChecker==null){
            return -1;
        }
        for(int i=0;i<row;i++){
            for (int j=0;j<column;j++){
                if(seatChecker.isValidSeat(i,j)){
                    break;
                }
                if(j==column-1){
                    if(i==row){
                        return -1;
                    }
                    result--;
                }
            }
        }
        return result;
    }

    /**
     * 根据行和列   获取列的号码
     */
    private int getColumnNumber(int row,int column){
        int result=column;
        if(seatChecker==null){
            return -1;
        }
        for(int i=row;i<=row;i++){
            for (int j=0;j<column;j++){

                if(!seatChecker.isValidSeat(i,j)){
                    if(j==column){
                        return -1;
                    }
                    result--;
                }
            }
        }
        return result;
    }
}
