package test.com.myapplication.selfwordview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by zhangsixia on 18/4/20.
 * 左边竖向的view
 */

public class LeftView {

    private Rect leftRect = new Rect();
    private Rect waicengRect;
    private Paint waicengPiant = new Paint();
    private boolean canScroll = true;
    private WordView wordView;
    private int rectCount;
    private int totalHeight;
    public LeftView(WordView mWordView){
        this.wordView = mWordView;
        waicengPiant.setColor(Color.WHITE);
        waicengPiant.setAntiAlias(true);
        waicengPiant.setStyle(Paint.Style.FILL_AND_STROKE);
        waicengRect = new Rect(0,200,200,mWordView.getHeight());

    }
    /*获取单例*/
    private static LeftView mLeftView = null;
    public static LeftView getInstance(WordView mWordView){
        synchronized (LeftView.class){
            if(mLeftView == null){
                mLeftView = new LeftView(mWordView);
            }
        }
        return mLeftView;
    }

    /*绘制左边竖向view的方法*/
    public void onDraw(int leftItem,int topItem,Canvas mCanvas,Paint mPaint){
        mCanvas.drawRect(waicengRect,waicengPiant);
        for(int i=0;i<20;i++){
            int distance = topItem+i*100;
            leftRect.set(getItemRect(leftItem,distance));
            mCanvas.drawRect(leftRect,mPaint);
        }

    }

    /*获取单元itemRect的方法*/
    private Rect getItemRect(int leftItem,int topItem){
        return new Rect(leftItem,topItem,leftItem+200,topItem+100);
    }


}
