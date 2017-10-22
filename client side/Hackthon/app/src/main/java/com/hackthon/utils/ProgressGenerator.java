package com.hackthon.utils;

/**
 * Created by cody on 20/10/2017.
 */

import android.os.Handler;

import java.util.Random;


import com.dd.processbutton.ProcessButton;
import com.hackthon.MainActivity;

import android.os.Handler;

import java.util.Random;

public class ProgressGenerator {

    public interface OnCompleteListener {

        public void onComplete();
    }

    private OnCompleteListener mListener;
    private int mProgress;

    public ProgressGenerator(OnCompleteListener listener) {
        mListener = listener;
    }

    public void start(final ProcessButton button) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress += 10;
                if (mProgress == 100 && MainActivity.judgeprocess){
                    mProgress = mProgress -11;
                    button.setProgress(mProgress);
                }
                else if (MainActivity.judgeprocess){
                    button.setProgress(mProgress);
                }
                if (MainActivity.judgeprocess && mProgress < 100 ) {
                    handler.postDelayed(this, generateDelay());
                }
                else if(MainActivity.judgeprocess && mProgress > 99) {
                    mProgress = mProgress - 80;
                    handler.postDelayed(this, generateDelay());
                }
                else if (!MainActivity.judgeprocess){
                    mListener.onComplete();
                }
            }
        }, generateDelay());
    }

    private Random random = new Random();

    private int generateDelay() {
        return random.nextInt(1000);
    }
}
