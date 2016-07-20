package kr.co.tmoney.mobiledriverconsole.model;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by jinseo on 2016. 7. 20..
 */
public class MDCViewPager extends ViewPager{

    private boolean isSwappable;

    public MDCViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isSwappable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSwappable) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isSwappable){
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public void setSwappable(boolean value){
        isSwappable = value;
    }

}
