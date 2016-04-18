package com.example.leiyu.surfaceviewanimation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by leiyu on 2016/4/14.
 * function 赞助一个和任务成功完成的单帧动画
 */
public class OneFrameAnimationView extends ImageView{
    private static final String TAG = "OneFrameAnimationView";


    public OneFrameAnimationView(Context context) {
        super(context);
    }
    public OnFramePlayListener mListener;

    public void setFramePlayListener(OnFramePlayListener listener){
        mListener = listener;
    }

    public void startAnimation(){
        ObjectAnimator xScaleAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.2F, 1.15F);
        xScaleAnimator.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator yScaleAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.2F, 1.15F);
        yScaleAnimator.setInterpolator(new AccelerateInterpolator());
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(xScaleAnimator).with(yScaleAnimator);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                scaleAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {


            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorSet.start();
    }

    private void scaleAnimation(){
        ObjectAnimator xScaleAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 1.15F, 0.95F);
        xScaleAnimator.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator yScaleAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.15F, 0.95F);
        yScaleAnimator.setInterpolator(new DecelerateInterpolator());
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(xScaleAnimator).with(yScaleAnimator);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismissAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {


            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorSet.start();
    }

    private void dismissAnimation(){
        ObjectAnimator xScaleAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.95F, 1.05F);
        xScaleAnimator.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator yScaleAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.95F, 1.05F);
        yScaleAnimator.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, View.ALPHA, 1F, 0F);

        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(xScaleAnimator).with(yScaleAnimator).with(alphaAnimator);
        mAnimatorSet.setDuration(900);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimatorSet.start();
    }

    public interface OnFramePlayListener{
        public void onStart();
        public void onStep();
        public void onStop();
    }
}
