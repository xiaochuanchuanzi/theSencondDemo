package test.com.library;

import android.content.Context;

/**
 * Created by zhangsixia on 18/4/26.
 */

public class DipToPxUtils {


    /*获取单例*/
    private static DipToPxUtils mDipToPxUtils = null;
    public static DipToPxUtils getInstance(Context context){
        synchronized (DipToPxUtils.class){
            if(mDipToPxUtils == null){
                mDipToPxUtils = new DipToPxUtils(context);
            }
        }
        return mDipToPxUtils;
    }


    private Context mContext;
    public DipToPxUtils(Context context){
        this.mContext = context;
    }

    /**
     * 删除选中的座位
     */
    public float dip2Px(float value) {
        return mContext.getResources().getDisplayMetrics().density * value;
    }
}
