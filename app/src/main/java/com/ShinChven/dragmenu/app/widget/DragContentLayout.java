package com.ShinChven.dragmenu.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by ShinChven on 2015/1/26.
 */
public class DragContentLayout extends RelativeLayout {
    private DragMenu mDragMenu;

    public DragContentLayout(Context context) {
        this(context, null);
    }

    public DragContentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDragMenu(DragMenu dragMenu) {
        this.mDragMenu = dragMenu;
    }

    public DragMenu getDragMenu() {
        return mDragMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragMenu.getDragStatus() != DragMenu.DragStatus.Close) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragMenu.getDragStatus() != DragMenu.DragStatus.Close) {
            mDragMenu.closeMenu();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
