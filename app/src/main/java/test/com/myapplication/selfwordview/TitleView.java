package test.com.myapplication.selfwordview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by zhangsixia on 18/4/20.
 */

public class TitleView {

    private Rect waicengRect;
    private Paint waicengPiant = new Paint();
    public TitleView(WordView mWordView){
        waicengPiant.setColor(Color.WHITE);
        waicengPiant.setAntiAlias(true);
        waicengPiant.setStyle(Paint.Style.FILL);
        waicengRect = new Rect(200,0,mWordView.getWidth(),200);
    }
    /*获取单例*/
    private static TitleView mTitleView = null;
    public static TitleView getInstance(WordView mWordView){
        synchronized (TitleView.class){
            if(mTitleView == null){
                mTitleView = new TitleView(mWordView);
            }
        }
        return mTitleView;
    }

    /*绘制左边竖向view的方法*/
    public void onDraw(int leftItem, int topItem, Canvas mCanvas, Paint mPaint){
        mCanvas.drawRect(waicengRect,waicengPiant);
        Rect titleRect = new Rect();
        for(int i=0;i<10;i++){
            int distance = leftItem+i*150;
            titleRect.set(getItemRect(distance,topItem));
            mCanvas.drawRect(titleRect,mPaint);
        }

    }

    /*获取单元itemRect的方法*/
    private Rect getItemRect(int leftItem,int topItem){
        return new Rect(leftItem,topItem,leftItem+150,topItem+200);
    }

}
