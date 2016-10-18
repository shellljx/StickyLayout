package com.licrafter.scrolllayout.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.licrafter.scrolllayout.DipConvertUtil;
import com.licrafter.scrolllayout.HomeFragment;

/**
 * author: shell
 * date 16/10/11 下午2:27
 **/
public class StickyLayout extends LinearLayout {

    private ViewPager mContentView;

    private float mLastY;
    private int mLastScrollerY;
    private int mHeaderHeight;
    //头部是否已经隐藏
    private boolean mIsSticky;
    //内嵌滚动控件是否受用户手势的操控
    private boolean mIsControlled;
    //整体布局是否被拖拽
    private boolean mIsDragging;
    //是否是事件转发
    private boolean mReDirect;

    private Scroller mScroller;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity, mMinimumVelocity;
    private DIRECTION mDirection;

    enum DIRECTION {
        UP, DOWN
    }

    public StickyLayout(Context context) {
        this(context, null);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = (ViewPager) getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeaderHeight = DipConvertUtil.dip2px(getContext(), 330);
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mHeaderHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        acquireVelocityTracker(ev);
        int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished() && !isSticky() && isRecyclerViewTop(getRecyclerView()) || !mScroller.isFinished() && !isSticky() && mDirection == DIRECTION.UP) {
                    mScroller.forceFinished(true);
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                if (!mReDirect) {
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                if (dy < 0 && Math.abs(dy) > mTouchSlop && mIsControlled && isRecyclerViewTop(getRecyclerView())) {
                    mLastY = y;
                    mIsControlled = false;
                    mReDirect = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                } else {
                    mReDirect = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                mDirection = velocityY < 0 ? DIRECTION.UP : DIRECTION.DOWN;
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                if (!isSticky() && Math.abs(dy) > mTouchSlop && !isRecyclerViewTop(getRecyclerView()) && dy > 0 || !isSticky() && Math.abs(dy) > mTouchSlop && isRecyclerViewTop(getRecyclerView()) || isSticky() && dy < 0 && isRecyclerViewTop(getRecyclerView())) {
                    mLastY = y;
                    return true;
                } else if (Math.abs(dy) > mTouchSlop) {
                    mIsControlled = true;
                    mIsDragging = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isSticky() && mIsDragging) {
                    mIsDragging = false;
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                mIsControlled = false;
                if (Math.abs(dy) > mTouchSlop && !mIsDragging) {
                    mIsDragging = true;
                }
                if (mIsDragging) {
                    scrollBy(0, (int) (dy + 0.5));
                    if (isSticky()) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        //此处还是整体滑动阶段,所以还会走intrcept up事件
                    }
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                recycleVelocityTracker();
                mIsDragging = false;
                break;
            case MotionEvent.ACTION_UP:
                mIsDragging = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        mScroller.computeScrollOffset();
        mLastScrollerY = mScroller.getCurrY();
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            if (mDirection == DIRECTION.UP) {
                if (isSticky()) {
                    mIsControlled = true;
                    int distance = mScroller.getFinalY() - currY;
                    int duration = mScroller.getDuration() - mScroller.timePassed();
                    getRecyclerView().fling(0, getScrollerVelocity(distance, duration));
                    mScroller.forceFinished(true);
                } else {
                    scrollTo(0, currY);
                }
            } else {
                if (isRecyclerViewTop(getRecyclerView())) {
                    int delta = currY - mLastScrollerY;
                    int toY = getScrollY() + delta;
                    scrollTo(0, toY);
                    if (getScrollY() == 0 && !mScroller.isFinished()) {
                        mScroller.forceFinished(true);
                    }
                }
                invalidate();
            }
            mLastScrollerY = currY;
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mHeaderHeight) {
            y = mHeaderHeight;
        }
        mIsSticky = y == mHeaderHeight;
        super.scrollTo(x, y);
    }

    public boolean isSticky() {
        return mIsSticky;
    }

    private boolean isRecyclerViewTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private RecyclerView getRecyclerView() {
        HomeFragment.GoodsPageAdapter adapter = (HomeFragment.GoodsPageAdapter) mContentView.getAdapter();
        View view = adapter.getFragment(mContentView.getCurrentItem()).getView();
        return (RecyclerView) view;
    }

    private int getScrollerVelocity(int distance, int duration) {
        if (mScroller == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) mScroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
}
