package com.licrafter.scrolllayout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * author: shell
 * date 16/10/9 下午2:30
 **/
public class HomeFragment extends Fragment implements View.OnClickListener {

    ViewPager goodsViewPager;
    LinearLayout headerView;
    ViewPager header01;
    ImageView iv01, iv02, iv03, iv04;
    Button btn01, btn02, btn03, btn04;
    int currPosition = 0;
    GoodsPageAdapter pageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        headerView = (LinearLayout) view.findViewById(R.id.header);
        header01 = (ViewPager) view.findViewById(R.id.header01);
        goodsViewPager = (ViewPager) view.findViewById(R.id.goodsViewPager);
        iv01 = (ImageView) view.findViewById(R.id.iv01);
        iv02 = (ImageView) view.findViewById(R.id.iv02);
        iv03 = (ImageView) view.findViewById(R.id.iv03);
        iv04 = (ImageView) view.findViewById(R.id.iv04);
        btn01 = (Button) view.findViewById(R.id.btn01);
        btn02 = (Button) view.findViewById(R.id.btn02);
        btn03 = (Button) view.findViewById(R.id.btn03);
        btn04 = (Button) view.findViewById(R.id.btn04);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);

        iv04.setOnClickListener(this);
        iv03.setOnClickListener(this);
        iv02.setOnClickListener(this);
        iv01.setOnClickListener(this);
        pageAdapter = new GoodsPageAdapter(getChildFragmentManager());
        goodsViewPager.setAdapter(pageAdapter);
        goodsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class GoodsPageAdapter extends FragmentPagerAdapter{

        SparseArray<String> mFragmentTags = new SparseArray<>();

        public GoodsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }

        public PageFragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return (PageFragment) getChildFragmentManager().findFragmentByTag(tag);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv01:
                android.util.Log.d("ljx", "click imageview");
                break;
            case R.id.iv02:
                android.util.Log.d("ljx", "click imageview");
                break;
            case R.id.iv03:
                android.util.Log.d("ljx", "click imageview");
                break;
            case R.id.iv04:
                android.util.Log.d("ljx", "click imageview");
                break;
            case R.id.btn01:
                if (currPosition!=0){
                    goodsViewPager.setCurrentItem(0,false);
                    android.util.Log.d("ljx", "click button");
                    currPosition=0;
                }
                break;
            case R.id.btn02:
                if (currPosition!=1){
                    goodsViewPager.setCurrentItem(1,false);
                    android.util.Log.d("ljx", "click button");
                    currPosition = 1;
                }
                break;
            case R.id.btn03:
                if (currPosition!=2){
                    goodsViewPager.setCurrentItem(2,false);
                    android.util.Log.d("ljx", "click button");
                    currPosition=2;
                }
                break;
            case R.id.btn04:
                if (currPosition!=3){
                    goodsViewPager.setCurrentItem(3,false);
                    android.util.Log.d("ljx", "click button");
                    currPosition=3;
                }
                break;
        }
    }
}
