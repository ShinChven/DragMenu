package com.GitHub.ShinChven.DragMenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * Created by ShinChven on 2015/1/26.
 */
public class DragMenu extends FrameLayout {

    public static final String DRAG_LISTENER_IS_NOT_SET = "drag listener is not set.";
    private GestureDetectorCompat mGestureDetector;
    private ViewDragHelper mDragHelper;
    private DragListener mDragListener;
    private int mMenuViewId;
    private int mContentViewId;
    private RelativeLayout mMenuView;
    private DragContentLayout mContentView;
    private int width;
    private int height;
    private int dragRange;
    private DragStatus mDragStatus;
    private boolean transformEnabled;

    public boolean isTransformEnabled() {
        return transformEnabled;
    }

    public void setTransformEnabled(boolean transformEnabled) {
        this.transformEnabled = transformEnabled;
    }

    public DragMenu(Context context) {
        this(context, null);
    }

    public DragMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragStatus getDragStatus() {
        if (contentLeft == 0) {
            mDragStatus = DragStatus.Close;
        } else if (contentLeft == dragRange) {
            mDragStatus = DragStatus.Open;
        } else {
            mDragStatus = DragStatus.Drag;
        }
        return mDragStatus;
    }


    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) <= Math.abs(distanceX);
        }
    }

    private int contentLeft;
    private final ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View view, int i) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // keep menu location inside dragRange
            if (contentLeft + dx < 0) {
                return 0;
            } else if (contentLeft + dx > dragRange) {
                return dragRange;
            } else {
                return left;
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return width;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                openMenu();
            } else if (xvel < 0) {
                closeMenu();
            } else if (releasedChild == mContentView && contentLeft > dragRange * 0.3) {
                openMenu();
            } else if (releasedChild == mMenuView && contentLeft > dragRange * 0.7) {
                openMenu();
            } else {
                closeMenu();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // define moving position
            if (changedView == mContentView) {
                contentLeft = left;
            } else {
                contentLeft = contentLeft + left;
            }
            if (contentLeft < 0) {
                contentLeft = 0;
            } else if (contentLeft > dragRange) {
                contentLeft = dragRange;
            }
            if (changedView == mMenuView) {
                mMenuView.layout(0, 0, width, height);
                mContentView.layout(contentLeft, 0, contentLeft + width, height);
            }
            doDrag(contentLeft);
        }
    };

    public void openMenu() {
        open(true);
    }

    private void open(boolean animated) {
        if (animated) {
            if (mDragHelper.smoothSlideViewTo(mContentView, dragRange, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mContentView.layout(dragRange, 0, dragRange * 2, height);
            doDrag(dragRange);
        }
    }

    public void closeMenu() {
        closeMenu(true);
    }

    private void closeMenu(boolean animated) {
        if (animated) {
            if (mDragHelper.smoothSlideViewTo(mContentView, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mContentView.layout(0, 0, width, height);
            doDrag(dragRange);
        }
    }

    private static final String TAG = DragMenu.class.getSimpleName();

    private void doDrag(int contentLeft) {

        if (!transformEnabled) {
            return;
        }
        float percent = contentLeft / ((float) dragRange);
        animateView(percent);
        try {
            mDragListener.onDrag(percent);
        } catch (Exception e) {
            Log.i(TAG, DRAG_LISTENER_IS_NOT_SET);
        }
        DragStatus lastStatus = mDragStatus;
        if (lastStatus != getDragStatus() && mDragStatus == DragStatus.Close) {
            try {
                mDragListener.onClose();
            } catch (Exception e) {
                Log.i(TAG, DRAG_LISTENER_IS_NOT_SET);
            }
        } else if (lastStatus != getDragStatus() && mDragStatus == DragStatus.Open) {
            try {
                mDragListener.onOpen();
            } catch (Exception e) {
                Log.i(TAG, DRAG_LISTENER_IS_NOT_SET);
            }
        }
    }

    enum DragStatus {
        Open, Drag, Close
    }

    private void animateView(float percent) {
        float f1 = 1 - percent * 0.3f;
        ViewCompat.setScaleX(mContentView, f1);
        ViewCompat.setScaleY(mContentView, f1);
        ViewCompat.setTranslationX(mMenuView, -mMenuView.getWidth() / 2.3f + mMenuView.getWidth() / 2.3f * percent);
        ViewCompat.setScaleX(mMenuView, 0.5f + 0.5f * percent);
        ViewCompat.setScaleY(mMenuView, 0.5f + 0.5f * percent);
        ViewCompat.setAlpha(mMenuView, percent);
    }

    public DragMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        mGestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback);

    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DragMenu);
        mMenuViewId = ta.getResourceId(R.styleable.DragMenu_menu_view_id, -1);
        mContentViewId = ta.getResourceId(R.styleable.DragMenu_content_view_id, -1);
        ta.recycle();
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView = (RelativeLayout) findViewById(mMenuViewId);
        mContentView = (DragContentLayout) findViewById(mContentViewId);
        mContentView.setDragMenu(this);
        mMenuView.setClickable(true);
        mContentView.setClickable(true);
    }

    public RelativeLayout getmMenuView() {
        return mMenuView;
    }

    public DragContentLayout getmContentView() {
        return mContentView;
    }

    public interface DragListener {
        public void onOpen();

        public void onClose();

        public void onDrag(float percent);
    }

    public void setDragListener(DragListener l) {
        mDragListener = l;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = mMenuView.getMeasuredWidth();
        height = mMenuView.getMeasuredHeight();
        dragRange = ((int) (width * 0.6f));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mMenuView.layout(0, 0, width, height);
        mContentView.layout(contentLeft, 0, contentLeft + width, height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}












