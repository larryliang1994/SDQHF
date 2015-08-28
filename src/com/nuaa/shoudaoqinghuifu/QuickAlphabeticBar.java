package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * 获取字母表
 */
public class QuickAlphabeticBar extends ImageButton {
    private TextView mDialogText;
    private Handler mHandler;
    private ListView mList;
    private float mHight;
    private String[] letters = new String[]{"#", "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private HashMap<String, Integer> alphaIndexer;

    public QuickAlphabeticBar(Context context) {
        super(context);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Activity ctx) {
        mDialogText = (TextView) ctx.findViewById(R.id.fast_position);
        mDialogText.setVisibility(View.INVISIBLE);
        mHandler = new Handler();
    }

    public void setListView(ListView mList) {
        this.mList = mList;
    }

    public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
        this.alphaIndexer = alphaIndexer;
    }

    public void setHight(float mHight) {
        this.mHight = mHight;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int act = event.getAction();
        float y = event.getY();

        int selectIndex = (int) (y / (mHight / 27));
        if (selectIndex < 27) {
            String key = letters[selectIndex];
            if (alphaIndexer.containsKey(key)) {
                int pos = alphaIndexer.get(key);
                if (mList.getHeaderViewsCount() > 0) {
                    this.mList.setSelectionFromTop(
                            pos + mList.getHeaderViewsCount(), 0);
                } else {
                    this.mList.setSelectionFromTop(pos, 0);
                }
                mDialogText.setText(letters[selectIndex]);
            }
        }
        if (act == MotionEvent.ACTION_DOWN) {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialogText != null
                                && mDialogText.getVisibility() == View.INVISIBLE) {
                            mDialogText.setVisibility(VISIBLE);
                        }
                    }
                });
            }
        } else if (act == MotionEvent.ACTION_UP) {
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialogText != null
                                && mDialogText.getVisibility() == View.VISIBLE) {
                            mDialogText.setVisibility(INVISIBLE);
                        }
                    }
                });
            }
        }
        return super.onTouchEvent(event);
    }
}
