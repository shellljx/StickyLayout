#StickyLayout

[相关博文](http://licrafter.com/2016/10/15/AndroidStickyLayout%E4%B8%8E%E8%A7%A6%E6%91%B8%E4%BA%8B%E4%BB%B6/)

---

# 前言

前段时间因项目需求在  `Android`  上要实现悬浮头部  +  `ViewPager`  +  列表的功能，综合考虑了各种方案的可行性,最后决定使用拦截触摸事件的方式来实现。网上也有一些类似的例子，但是功能和需求有些不符而且有一些  Bug，于是自己把整个  `Touch`  事件从头理了一遍，解决了一些Bug. 对  `Android` 对触摸事件有了更深刻的理解。
<!--more-->

整个界面布局最外层是一个  `LinearLayout`  ，上部分为黏性头布局，下部分为一个内嵌  `RecyclerView`  的  `ViewPager`  。最外层  `LinearLayout`  的高度为  整个屏幕的高度  +  黏性头布局高度  -  `TabLayout`  高度。当头部没有悬浮的时候，最外层布局拦截滑动事件，然后自己去处理，使整个布局内容滑动。完美实现滚动悬浮的无缝切换和惯性的传递。
## 演示视频:

<video width="200px" id="video" controls="" preload="none" poster="http://7vzpfd.com1.z0.glb.clouddn.com/scrolllayout-MainActivity-10152016221531.png">
      <source id="mp4" src="http://7vzpfd.com1.z0.glb.clouddn.com/shamuMMB29Klijx10152016215357.mp4" type="video/mp4">
</video>。
# 什么时候需要拦截滑动事件？
理清整个场景的各个临界点的状态，了解  `Android`  的事件传递顺序是非常关键的。当理清状态以后写起来就非常清晰了。
根据我遇到的使用场景分析，当需要整体布局内容滚动的时候(阻止列表滚动)就需要阻止滑动事件传递到  `RecyclerView`。有如下几种情况：
1. 当头部没有悬浮，列表没有滑动到顶部并且向上滑动。
2. 当头部没有悬浮并且列表已经滑动到顶部。
3. 当头部悬浮，并且列表滑动到顶部。

```java
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            //省略部分代码
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
                //省略部分代码
        }
        return super.onInterceptTouchEvent(ev);
    }
```
# 处理滑动
当滑动事件被拦截后由  `StickyLayout`  自己的  `OnTouchEvent`  方法处理滑动事件，使整个布局内容移动。
```java
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
                //省略部分代码
        }
        return super.onTouchEvent(event);
    }
```
# 临界点事件转发
1. 当整体布局滑动到黏性状态的临界点时就要转发事件，让嵌套的  `RecyclerView`  响应滑动。
```java
    if (isSticky()) {
        event.setAction(MotionEvent.ACTION_DOWN);
        dispatchTouchEvent(event);
        event.setAction(MotionEvent.ACTION_CANCEL);
        //此处还是整体滑动阶段,所以还会走intrcept up事件
    }
```
2. 当内嵌的  `Recyclerview`  滑动到顶部并且头布局处于黏性状态时，要转发事件，让  `StickyLayout`  重新获取滑动事件。
```java
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                if (dy < 0 && Math.abs(dy) > mTouchSlop && mIsControlled && isRecyclerViewTop(getRecyclerView())) {
                    mIsControlled = false;
                    mReDirect = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    dispatchTouchEvent(ev);
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev);
                } else {
                    mReDirect = false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
```
# 惯性的传递
惯性的传递就是  `Scroller`  和重写  `computeScroll()`  方法进行配合来实现。

# 发现的问题
当把  `MotionEvent.ACTION_DOWN`  事件重置为  `MotionEvent.ACTION_CANCEL`  的时候，不会再去执行  `onInterceptTouchEvent`  方法，而直接由自己的  `onTouch`  方法来处理后续事件。

# 源码
[https://github.com/shellljx/StickyLayout](http://github.com/shellljx/StickyLayout)


