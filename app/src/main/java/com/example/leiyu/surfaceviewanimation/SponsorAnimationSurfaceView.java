package com.example.leiyu.surfaceviewanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by leiyu on 2016/4/14.
 * function
 */
public class SponsorAnimationSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mSurfaceHolder;
    private DrawThread mDrawThread = null;

    private boolean isDrawRunning = false;

    private OnFramePlayCallback mFramePlayCallback = null;
    private static int FRAME_TIME = 100; //ms


    public void setFramePlayCallback(OnFramePlayCallback framePlayCallback){
        mFramePlayCallback = framePlayCallback;
    }

    public SponsorAnimationSurfaceView(Context context) {
        super(context);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void stop(){
        mFramePlayCallback.onStop();
        isDrawRunning = false;
        if(null != mDrawThread){
            mDrawThread.interrupt();
            mDrawThread = null;
        }
        LiveBaseApplication.getInstance().resetAnimationResourceManager();
    }

    // surface holder callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("-----做什么事儿了");
        mDrawThread = new DrawThread();
        mDrawThread.start();
        mFramePlayCallback.onStart();
        isDrawRunning = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawRunning = false;
        LiveBaseApplication.getInstance().resetAnimationResourceManager();
    }


    private class DrawThread extends Thread{
        @Override
        public void run() {
            // draw bitmap
            while (isDrawRunning){
                synchronized (mSurfaceHolder){
                    long lastTime = SystemClock.uptimeMillis();
                    Bitmap bitmap = LiveBaseApplication.getInstance().getAnimationResourceManager().nextFrame();
                    if(null != bitmap){
                        Canvas canvas = mSurfaceHolder.lockCanvas();
                        doDraw(canvas, bitmap);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        long duration = SystemClock.uptimeMillis() - lastTime;

                        if(duration < FRAME_TIME){
                            try {
                                sleep(FRAME_TIME - duration);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        // get null bitmap, should stop animation, and remove view
                        isDrawRunning = false;
                        mFramePlayCallback.onFinish();
                    }
                }
            }
        }
    }

    private void doDraw(Canvas canvas, Bitmap bitmap){
        if(null != canvas){
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(Color.WHITE);

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
//                MyLog.i(TAG, "doDraw[]>>>>>>>>>bitmapWidth = " + bitmapWidth);
//                MyLog.i(TAG, "doDraw[]>>>>>>>>>bitmapHeight = " + bitmapHeight);
//
//                MyLog.i(TAG, "doDraw[]>>>>>>this width = " + getWidth());
//                MyLog.i(TAG, "doDraw[]>>>>>>this height = " + getHeight());

            int bitmapLeft = 0;
            int bitmapTop = 0;
            int bitmapRight = bitmapWidth;
            int bitmapBottom = bitmapHeight;

            // 放大了一倍
            int destLeft = getWidth() / 2 - bitmapWidth;
//                MyLog.i(TAG, "doDraw[]>>>>>>>>>destLeft = " + destLeft);

            int destTop = getHeight() / 2 - bitmapHeight;
//                MyLog.i(TAG, "doDraw[]>>>>>>>>>destTop = " + destTop);

            int destRight = getWidth() / 2 + bitmapWidth;
//                MyLog.i(TAG, "doDraw[]>>>>>>>>>destRight = " + destRight);

            int destBottom = getHeight() / 2 + bitmapHeight;
//                MyLog.i(TAG, "doDraw[]>>>>>>>>>destBottom = " + destBottom);

            canvas.drawBitmap(bitmap,
                    new Rect(bitmapLeft, bitmapTop, bitmapRight, bitmapBottom),
                    new Rect(destLeft, destTop, destRight, destBottom),
                    mPaint);
            bitmap.recycle();
            mFramePlayCallback.onPlayIndex();
        }
    }

    public interface OnFramePlayCallback{
        public void onPlayIndex();
        public void onStart();
        public void onStop();
        public void onFinish();
    }
}
