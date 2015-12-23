package com.cocolover2.lis.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocolover2.lis.R;
import com.cocolover2.lis.view.HackyViewPager;

import java.util.ArrayList;

public class ImagePageFragment<T> extends Fragment {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private HackyViewPager mPager;
    private int pagerPosition;
    private ArrayList<T> data;
    private boolean isStringArrayList = true;

    public interface OnSimplePagerSelectListener {
        void onSelect(int position);
    }

    private OnSimplePagerSelectListener listenrer;

    public void setOnPagerSelectListener(OnSimplePagerSelectListener listener) {
        this.listenrer = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_viewpager, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pagerPosition = getArguments().getInt(EXTRA_IMAGE_INDEX, 0);
        if (!isStringArrayList)
            data = (ArrayList<T>) getArguments().getParcelableArrayList(EXTRA_IMAGE_URLS);
        else
            data = (ArrayList<T>) getArguments().getStringArrayList(EXTRA_IMAGE_URLS);
        if (getView() != null)
            mPager = (HackyViewPager) getView().findViewById(R.id.image_viewpager_pager);
        else
            getActivity().finish();
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        mPager.setAdapter(new ImagePagerAdapter(getChildFragmentManager()));
        mPager.setCurrentItem(pagerPosition);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (listenrer != null)
                    listenrer.onSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param position   浏览图片的起始位置
     * @param imagePaths 要浏览的图片集合
     * @return
     */
    public void initArguments(int position, ArrayList<T> imagePaths) {
        if (imagePaths == null || imagePaths.size() == 0)
            return;
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_IMAGE_INDEX, position);
        if (imagePaths.get(0) instanceof String) {
            isStringArrayList = true;
            bundle.putStringArrayList(EXTRA_IMAGE_URLS, (ArrayList<String>) imagePaths);
        } else {
            bundle.putParcelableArrayList(EXTRA_IMAGE_URLS, (ArrayList<Parcelable>) imagePaths);
            isStringArrayList = false;
        }
        setArguments(bundle);
    }

    public interface OnShowContentViewListener<T> {
        Fragment getContentFragment(T content);
    }

    private OnShowContentViewListener listener;

    public void setOnShowContentViewListener(OnShowContentViewListener listener) {
        this.listener = listener;
    }

    /*这里建议使用FragmentStatePagerAdapter,不建议使用FragmentPagerAdapter,因为FragmentPagerAdapter,会缓存页面，容易造成OOM*/
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (listener == null) {
                throw new NullPointerException("OnShowContentViewListener can not be null");
            }
            return listener.getContentFragment(data.get(position));
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }
    }
}
