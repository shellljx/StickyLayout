#StickyLayout

[相关介绍博文](http://licrafter.com/2016/10/15/AndroidStickyLayout%E4%B8%8E%E8%A7%A6%E6%91%B8%E4%BA%8B%E4%BB%B6/)

一个Android中拥有黏性头部的滑动布局，整体布局内容滑动和列表滑动完美切换，惯性传递的自定义控件。
---

![gif](http://7vzpfd.com1.z0.glb.clouddn.com/giphy.gif)

##How to use

这不是一个第三发库，只是根据需求对  `Android`  的触摸事件的一次实践， `StickyLayout`  继承自  `LinearLayout`  嵌套在头布局和列表布局的最外层即可，使用起来非常简单。
```xml
<com.licrafter.scrolllayout.view.StickyLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clickable="true"
    android:orientation="vertical">

    <LinearLayout
        android:id="@+id/header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#ffffff"
        android:clickable="true"
        android:orientation="vertical">

        <ImageView
            android:id="@+id/header01"
            android:layout_width="match_parent"
            android:layout_height="150dp"
            android:background="#50ae"
            android:clickable="true"
            android:scaleType="centerCrop"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:gravity="center_vertical"
            android:orientation="horizontal">

            <View
                android:background="#eb4f38"
                android:layout_width="wrap_content"
                android:layout_weight="1"
                android:layout_height="wrap_content"/>

            <View
                android:background="#ea8010"
                android:layout_width="wrap_content"
                android:layout_weight="1"
                android:layout_height="wrap_content"/>

            <View
                android:background="#f4c600"
                android:layout_width="wrap_content"
                android:layout_weight="1"
                android:layout_height="wrap_content"/>

            <View
                android:background="#9d55b8"
                android:layout_width="wrap_content"
                android:layout_weight="1"
                android:layout_height="wrap_content"/>
        </LinearLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:background="#50de"
            android:clickable="true">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:src="@mipmap/bg_01"/>
        </LinearLayout>

        <android.support.design.widget.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>
    </LinearLayout>

    <android.support.v4.view.ViewPager
        android:id="@+id/goodsViewPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

</com.licrafter.scrolllayout.view.StickyLayout>
```