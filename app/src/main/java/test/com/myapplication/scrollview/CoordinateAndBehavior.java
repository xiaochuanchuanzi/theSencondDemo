package test.com.myapplication.scrollview;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhangsixia on 18/4/23.
 */

public class CoordinateAndBehavior extends CoordinatorLayout.Behavior<View> {


    public CoordinateAndBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return    dependency instanceof LeftScrollView
                ||dependency instanceof TopScrollView
                ||dependency instanceof BodyScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        /*child.setX(dependency.getX()+200);
        child.setY(dependency.getY()+200);*/
        return true;
    }

}
