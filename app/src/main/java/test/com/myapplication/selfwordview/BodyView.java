package test.com.myapplication.selfwordview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by zhangsixia on 18/4/20.
 */

public class BodyView {

    private int UNIT_X = 150;//单元表格宽
    private int UNIT_Y = 100;//单元表格高度
    private int lishiLeftMargin;
    private int lishiTopMargin;

    public BodyView(){

    }
    /*获取单例*/
    private static BodyView mBodyView = null;
    public static BodyView getInstance(){
        synchronized (BodyView.class){
            if(mBodyView == null){
                mBodyView = new BodyView();
            }
        }
        return mBodyView;
    }

    /*绘制左边竖向view的方法*/
    public void onDraw(int leftItem, int topItem, Canvas mCanvas, Paint mPaint){
        lishiLeftMargin = leftItem;
        lishiTopMargin = topItem;
        Rect titleRect = new Rect();
        for(int j=0;j<20;j++){//绘制列
            int distanceY = topItem+j*100;
            for(int i=0;i<10;i++){//绘制航行
                int distanceX = leftItem+i*150;
                titleRect.set(getItemRect(distanceX,distanceY));
                mCanvas.drawRect(titleRect,mPaint);
            }
        }

    }

    /*获取单元itemRect的方法*/
    private Rect getItemRect(int leftItem,int topItem){
        return new Rect(leftItem,topItem,leftItem+UNIT_X,topItem+UNIT_Y);
    }

    public int getLeftMargin(){
        return lishiLeftMargin;
    }

    public int getToptMargin(){
        return lishiTopMargin;
    }

}
