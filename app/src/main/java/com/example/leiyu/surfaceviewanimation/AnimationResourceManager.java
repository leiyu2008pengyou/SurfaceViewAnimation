package com.example.leiyu.surfaceviewanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leiyu on 2016/4/14.
 * function
 */
public class AnimationResourceManager {
    private Context mContext;
    private int mTotalFrameCount = 0;
    private int mPlayIndex = -1;    // 播放图片的索引

    public AnimationResourceManager() {
        mContext = LiveBaseApplication.getInstance().getApplicationContext();
        initOriginalBitmaps();
    }

    private void initOriginalBitmaps(){
        try {
            String[] files = mContext.getAssets().list("hundred");
            mTotalFrameCount = files.length;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Bitmap nextFrame(){
        mPlayIndex ++;
        if(mPlayIndex < mTotalFrameCount){
            try {
                String fileName = "hundred/ZZ" + mPlayIndex + ".png";
                InputStream inputStream = mContext.getAssets().open(fileName);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public void reset(){
        mPlayIndex = -1; // 播放到哪个了恢复为－1
    }
}
