package com.example.leiyu.surfaceviewanimation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements View.OnClickListener{
    Button button;
    Button secButton;
    FrameLayout mFrameLayout;
    SponsorAnimationSurfaceView mAnimationSurfaceView = null;
    private static final int SPONSOR_ANIMATION_START = 0xF1;
    private static final int SPONSOR_ANIMATION_PLAYING_INDEX = 0xF2;
    private static final int SPONSOR_ANIMATION_STOP = 0xF3;
    private static final int SPONSOR_ANIMATION_FINISH = 0xF4;
    private static final int PLAY_SPONSOR_FRAME_MESSAGE = 0xFE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.live_dynamic_sponsor_frame_animation_layout);
        button = (Button)findViewById(R.id.give_one_present);
        secButton = (Button)findViewById(R.id.give_more_present);
        button.setOnClickListener(this);
        secButton.setOnClickListener(this);
        secButton.setOnTouchListener(new SponsorTouchListener());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.give_one_present){
            playOneFrameAnimation();
        }
    }

    //单送一个礼物
    private void playOneFrameAnimation() {
        if (mFrameLayout != null) {
            try {
                String fileName = "one/one.png";
                InputStream inputStream = this.getAssets().open(fileName);
                Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);

                int bitmapWidth = mBitmap.getWidth() * 2;
                int bitmapHeight = mBitmap.getHeight() * 2;
                int screenWidth = UIUtil.getScreenWidth(this) / 2;

                double ratio = screenWidth * 1.0 / bitmapWidth;
                int newBitmapHeight = (int) ((bitmapHeight * ratio * 1000) / 1000);
                int frameLayoutWidth = mFrameLayout.getWidth();
                int frameLayoutHeight = mFrameLayout.getHeight();
                OneFrameAnimationView frameView = new OneFrameAnimationView(this);
                frameView.setImageBitmap(mBitmap);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth, newBitmapHeight);
                // 居中
                lp.leftMargin = screenWidth / 2;
                lp.topMargin = frameLayoutHeight / 2 - newBitmapHeight / 2;

                mFrameLayout.removeAllViews();
                mFrameLayout.addView(frameView, lp);
                frameView.startAnimation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isTouchingDown = false;
    private long mSponsorTouchDownTime = 0;
    private boolean isSponsorLongClickEnable = false;   // 默认没有长按
    private boolean mIsSponsorCountNotFull = true;  // 默认没有达到100的最高赞助数量
    private boolean mIsNotStopByNoBalance = true;   // 默认没有因为余额不足而停止动画

    private class LongClickCountThread implements Runnable {

        public LongClickCountThread() {
        }

        @Override
        public void run() {
            while (isTouchingDown && mIsSponsorCountNotFull && mIsNotStopByNoBalance) {
                if (SystemClock.elapsedRealtime() - mSponsorTouchDownTime > 500) {
                    isSponsorLongClickEnable = true;
                    mAdapterHandler.sendMessage(mAdapterHandler.obtainMessage(PLAY_SPONSOR_FRAME_MESSAGE));
                    synchronized (LongClickCountThread.class) {
                        try {
                            LongClickCountThread.class.wait(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (SystemClock.elapsedRealtime() - mSponsorTouchDownTime <= 500) {
                isSponsorLongClickEnable = false;
            }
        }
    }

    private Thread mCountThread = null;
    private int mSponsorNumber = 1;
    private int mSponsorAnimationIndex = 0;

    private class SponsorTouchListener implements View.OnTouchListener {
        public SponsorTouchListener() {
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouchingDown = true;
                    mIsSponsorCountNotFull = true;
                    mIsNotStopByNoBalance = true;   // 默认不会因为余额不足而停止动画
                    mSponsorTouchDownTime = SystemClock.elapsedRealtime();

                    mCountThread = new Thread(new LongClickCountThread());
                    if (!mCountThread.isAlive()) {
                        mCountThread.start();
                    }
                    // 开始计时
                    break;
                case MotionEvent.ACTION_OUTSIDE:

                case MotionEvent.ACTION_CANCEL:

                    if(isTouchingDown){
                        isTouchingDown = false;
                    }
                    if(isSponsorLongClickEnable) {
                        stopMultiFrameAnimation();
                        mIsSponsorCountNotFull = true;
                        isPlayingFrameAnimation = false;
                    }
                    mSponsorNumber = 1;
                    isSponsorLongClickEnable = false;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isTouchingDown) {
                        isTouchingDown = false;
                        synchronized (LongClickCountThread.class) {
                            LongClickCountThread.class.notifyAll();
                        }

                        // 赞助数量大于1
                        if (isSponsorLongClickEnable) {
                            if (mIsSponsorCountNotFull) {
                                // 在没有达到100的情况下，抬手之后立刻停止动画，并发送赞助数量
                                stopMultiFrameAnimation();

                            } else {
                                // 赞助数量达到100的情况下，在动画结束来停止动画，在动画结束的回调中发送赞助数量请求
                                mIsSponsorCountNotFull = true;
                                isPlayingFrameAnimation = false;
                            }

                        } else { // 赞助数量为1

                        }
                        mSponsorNumber = 1;
                        isSponsorLongClickEnable = false;
                    }else{

                    }
                    break;
            }
            return false;
        }
    }
    private void stopMultiFrameAnimation() {
        if (null != mAnimationSurfaceView) {
            mAnimationSurfaceView.stop();
        }
    }
    private Handler mAdapterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PLAY_SPONSOR_FRAME_MESSAGE) {
                // 展示帧动画效果
                playMultiFrameAnimation();
            } else if (msg.what == SPONSOR_ANIMATION_START) {
                // 设置为没有到100
                // 设置赞助数量从0开始计数
                mSponsorNumber = 0;
                // 设置赞助动画播放索引数从0开始计数
                mSponsorAnimationIndex = -1;
            } else if (msg.what == SPONSOR_ANIMATION_PLAYING_INDEX) {
                mSponsorAnimationIndex++;
                if (mSponsorAnimationIndex <= 20) {
                    mSponsorNumber++;
                }else if(mSponsorAnimationIndex == 21 || mSponsorAnimationIndex == 22){
                    mSponsorNumber = 30;
                }else if(mSponsorAnimationIndex == 23 || mSponsorAnimationIndex == 24){
                    mSponsorNumber = 40;
                }else if(mSponsorAnimationIndex == 25 || mSponsorAnimationIndex == 26 || mSponsorAnimationIndex == 27){
                    mSponsorNumber = 50;
                }else if(mSponsorAnimationIndex == 28 || mSponsorAnimationIndex == 29 || mSponsorAnimationIndex == 30){
                    mSponsorNumber = 60;
                }else if(mSponsorAnimationIndex == 31 || mSponsorAnimationIndex == 32 || mSponsorAnimationIndex == 33){
                    mSponsorNumber = 70;
                }else if(mSponsorAnimationIndex == 34 || mSponsorAnimationIndex == 35 || mSponsorAnimationIndex == 36){
                    mSponsorNumber = 80;
                }else if(mSponsorAnimationIndex == 37 || mSponsorAnimationIndex == 38 || mSponsorAnimationIndex == 39){
                    mSponsorNumber = 90;
                }else {
                    mSponsorNumber = 100;
                }

                if (mSponsorNumber >= 100) {
                    mSponsorNumber = 100;
                    mIsSponsorCountNotFull = false;
                }
            } else if (msg.what == SPONSOR_ANIMATION_STOP) {
                mFrameLayout.removeAllViews();
                mAnimationSurfaceView = null;
                isPlayingFrameAnimation = false;
                mSponsorAnimationIndex = -1; // 恢复为-1
                mSponsorNumber = 0;
            } else if (msg.what == SPONSOR_ANIMATION_FINISH) {

                mFrameLayout.removeAllViews();
                mAnimationSurfaceView = null;
                mSponsorAnimationIndex = -1; // 恢复为-1
                mSponsorNumber = 0;
            }
        }
    };

    private boolean isPlayingFrameAnimation = false;

    private void playMultiFrameAnimation() {
        if (!isPlayingFrameAnimation) {
            isPlayingFrameAnimation = true;
            mAnimationSurfaceView = new SponsorAnimationSurfaceView(this);
            mAnimationSurfaceView.setFramePlayCallback(new SponsorAnimationSurfaceView.OnFramePlayCallback() {
                @Override
                public void onPlayIndex() {
                    mAdapterHandler.sendMessage(mAdapterHandler.obtainMessage(SPONSOR_ANIMATION_PLAYING_INDEX));
                }

                @Override
                public void onStart() {
                    mAdapterHandler.sendEmptyMessage(SPONSOR_ANIMATION_START);
                }

                @Override
                public void onStop() {
                    mAdapterHandler.sendMessage(mAdapterHandler.obtainMessage(SPONSOR_ANIMATION_STOP));
                }

                @Override
                public void onFinish() {
                    mAdapterHandler.sendMessage(mAdapterHandler.obtainMessage(SPONSOR_ANIMATION_FINISH));
                }
            });
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mFrameLayout.removeAllViews();
            mFrameLayout.addView(mAnimationSurfaceView, lp);
        }
    }


}
