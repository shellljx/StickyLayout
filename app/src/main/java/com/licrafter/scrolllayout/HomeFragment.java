package com.licrafter.scrolllayout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * author: shell
 * date 16/10/9 下午2:30
 **/
public class HomeFragment extends Fragment {

    ViewPager mGoodsViewPager;
    ImageView mAdvHeader;
    TabLayout mTabLayout;
    GoodsPageAdapter mPageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdvHeader = (ImageView) view.findViewById(R.id.header01);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mGoodsViewPager = (ViewPager) view.findViewById(R.id.goodsViewPager);
        mTabLayout.setupWithViewPager(mGoodsViewPager);


        Picasso.with(getActivity()).load("https://img.alicdn.com/tps/TB1zZD3NFXXXXXxaFXXXXXXXXXX-1920-320.jpg_Q90.jpg")
                .into(mAdvHeader);

        mPageAdapter = new GoodsPageAdapter(getChildFragmentManager());
        mGoodsViewPager.setAdapter(mPageAdapter);
        mGoodsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    public class GoodsPageAdapter extends FragmentPagerAdapter {

        private SparseArray<String> mFragmentTags = new SparseArray<>();
        private String[] titles = new String[]{"最新", "进度", "需求", "热门"};

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

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        public PageFragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return (PageFragment) getChildFragmentManager().findFragmentByTag(tag);
        }

    }
}
