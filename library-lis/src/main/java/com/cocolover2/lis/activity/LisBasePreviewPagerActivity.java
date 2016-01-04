package com.cocolover2.lis.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.interf.OnPagerUpdateListener;
import com.cocolover2.lis.R;
import com.cocolover2.lis.view.HackyViewPager;

import java.util.ArrayList;


public abstract class LisBasePreviewPagerActivity<T> extends AppCompatActivity {

    private FrameLayout topLayout, bottomLayout;
    private ImagePagerAdapter mAdapter;

    private int startPos;
    private ArrayList<T> mImgDatas;
    private OnPagerUpdateListener pagerUpdateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previewpager);
        mImgDatas = (ArrayList<T>) getIntent().getParcelableArrayListExtra(LISConstant.PRE_IMG_DATAS);
        startPos = getIntent().getIntExtra(LISConstant.PRE_IMG_START_POSITION, 0);
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }
        initView();
        onMyCreate(savedInstanceState);
    }

    public T getItem(int position) {
        return mImgDatas.get(position);
    }

    protected void setOnPagerUpdateListener(OnPagerUpdateListener listener) {
        pagerUpdateListener = listener;
    }


    private void initView() {
        topLayout = (FrameLayout) findViewById(R.id.previewpager_topbar);
        if (getTopBarLayoutId() > 0) {
            final View topBar = LayoutInflater.from(this).inflate(getTopBarLayoutId(), topLayout, false);
            topLayout.addView(topBar, topBar.getLayoutParams());
        }
        bottomLayout = (FrameLayout) findViewById(R.id.previewpager_bottom_layout);
        if (getBottomLayoutId() > 0) {
            final View bottomBar = LayoutInflater.from(this).inflate(getBottomLayoutId(), bottomLayout, false);
            bottomLayout.addView(bottomBar, bottomBar.getLayoutParams());
        }
        initViewPager();
    }

    private void initViewPager() {
        final HackyViewPager mViewPager = (HackyViewPager) findViewById(R.id.previewpager_viewpager_pager);
              /*禁用ViewPager左右两侧拉到边界的渐变颜色*/
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(startPos);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (pagerUpdateListener != null)
                    pagerUpdateListener.onSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    protected int getStartPos() {
        return startPos;
    }

    protected int getAllDatasSize() {
        return mImgDatas.size();
    }

    protected void remove(int position) {
        if (position < 0 || position > mImgDatas.size()) {
            throw new IndexOutOfBoundsException("index is" + position + "and size is" + mImgDatas.size());
        }
        mImgDatas.remove(position);
        mAdapter.notifyDataSetChanged();
        if (pagerUpdateListener != null)
            pagerUpdateListener.onDeleted(position);
    }

    protected ArrayList<T> getmImgDatas() {
        return mImgDatas;
    }

    public abstract int getTopBarLayoutId();

    public abstract int getBottomLayoutId();

    public abstract void onMyCreate(Bundle savedInstanceState);

    public abstract Fragment showContentFragment(T content);


    private Animation mAnimationTOP, mAnimationBottom;
    protected boolean isShowTopBottom = true;

    protected void showTopAndBottomLayout() {
        if (topLayout.getVisibility() != View.VISIBLE)
            topLayout.setVisibility(View.VISIBLE);
        if (bottomLayout.getVisibility() != View.VISIBLE)
            bottomLayout.setVisibility(View.VISIBLE);
        mAnimationTOP = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mAnimationBottom = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        topLayout.startAnimation(mAnimationTOP);
        bottomLayout.startAnimation(mAnimationBottom);
        isShowTopBottom = true;
    }

    protected void hideTopAndBottomLayout() {
        mAnimationTOP = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mAnimationTOP.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAnimationBottom = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        mAnimationBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottomLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        topLayout.startAnimation(mAnimationTOP);
        bottomLayout.startAnimation(mAnimationBottom);
        isShowTopBottom = false;
    }

    /*这里建议使用FragmentStatePagerAdapter,不建议使用FragmentPagerAdapter,因为FragmentPagerAdapter,会缓存页面，容易造成OOM*/
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return showContentFragment(mImgDatas.get(position));
        }

        @Override
        public int getCount() {
            return mImgDatas == null ? 0 : mImgDatas.size();
        }
    }
}
