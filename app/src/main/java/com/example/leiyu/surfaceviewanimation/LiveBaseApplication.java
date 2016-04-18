package com.example.leiyu.surfaceviewanimation;

import android.app.Application;
import android.os.AsyncTask;

/**
 * Created by leiyu on 2016/4/14.
 * function
 */
public class LiveBaseApplication extends Application{

    private AnimationResourceManager mAnimationResourceManager = null;
    private static LiveBaseApplication mAppInstance = null;
    private static Application mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppInstance = this;
        new InitFrameResourceTask().execute();
    }

    // 初始化赞助动画资源的Task
    private class InitFrameResourceTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            initAnimationResource();
            return true;
        }
    }

    // 初始化资源
    private void initAnimationResource(){
        mAnimationResourceManager = new AnimationResourceManager();
    }

    public AnimationResourceManager getAnimationResourceManager(){
        return mAnimationResourceManager;
    }

    public void resetAnimationResourceManager(){
        mAnimationResourceManager.reset();
    }

    public static LiveBaseApplication getInstance() {
        return mAppInstance;
    }
}
