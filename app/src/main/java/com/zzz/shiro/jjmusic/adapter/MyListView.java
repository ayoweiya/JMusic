package com.zzz.shiro.jjmusic.adapter;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by wc on 2016/12/2.
 */
public class MyListView extends ListView implements GestureOverlayView.OnGestureListener,
        View.OnTouchListener {

    private boolean mScrollable = true;

    public MyListView(Context context) {
        super(context);
    }

    @Override
    public void onGestureStarted(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

    }

    @Override
    public void onGesture(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

    }

    @Override
    public void onGestureEnded(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

    }

    @Override
    public void onGestureCancelled(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) {
                    return super.onTouchEvent(motionEvent);
                }
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(motionEvent);
        }
    }
}
