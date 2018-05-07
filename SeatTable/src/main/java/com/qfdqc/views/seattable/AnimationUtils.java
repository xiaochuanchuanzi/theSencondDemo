package com.qfdqc.views.seattable;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by zhangsixia on 18/4/24.
 */

public class AnimationUtils {

    /*获取单例*/
    private static AnimationUtils mAnimationUtils = null;
    public static AnimationUtils getInstance(){
        synchronized (AnimationUtils.class){
            if(mAnimationUtils == null){
                mAnimationUtils = new AnimationUtils();
            }
        }
        return mAnimationUtils;
    }

    public AnimationUtils(){

    }

    public void moveAnimate(Point start, Point end) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MoveEvaluator(), start, end);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        MoveAnimation moveAnimation = new MoveAnimation();
        valueAnimator.addUpdateListener(moveAnimation);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    public void zoomAnimate(float cur, float tar) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cur, tar);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation();
        valueAnimator.addUpdateListener(zoomAnim);
        valueAnimator.addListener(zoomAnim);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    class MoveEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            int x = (int) (startPoint.x + fraction * (endPoint.x - startPoint.x));
            int y = (int) (startPoint.y + fraction * (endPoint.y - startPoint.y));
            return new Point(x, y);
        }
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float zoom = (Float) animation.getAnimatedValue();
            mZoomAndMoveAfterAnimation.animationZooms(zoom);
        }
        @Override
        public void onAnimationCancel(Animator animation) {
        }
        @Override
        public void onAnimationEnd(Animator animation) {
        }
        @Override
        public void onAnimationRepeat(Animator animation) {
        }
        @Override
        public void onAnimationStart(Animator animation) {
        }
    }

    class MoveAnimation implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Point p = (Point) animation.getAnimatedValue();
            mZoomAndMoveAfterAnimation.animationMove(p);
        }
    }
    public ZoomAndMoveAfterAnimation mZoomAndMoveAfterAnimation;
    public interface ZoomAndMoveAfterAnimation{
        void animationZooms(float zoom);
        void animationMove(Point p);
    }
    public void setInterface(ZoomAndMoveAfterAnimation A){
        this.mZoomAndMoveAfterAnimation = A;
    }

}
