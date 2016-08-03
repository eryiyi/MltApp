package com.Lbins.Mlt.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by zhl on 2016/7/3.
 */
public class MarqueeButton extends Button {

    public MarqueeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
// TODO Auto-generated method stub
        if(focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
// TODO Auto-generated method stub
        if(hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus);
    }
    @Override
    public boolean isFocused() {
        return true;
    }
}
