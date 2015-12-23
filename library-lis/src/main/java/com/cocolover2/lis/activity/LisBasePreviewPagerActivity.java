package com.cocolover2.lis.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.R;
import com.cocolover2.lis.fragment.ImagePageFragment;

import java.util.ArrayList;


public abstract class LisBasePreviewPagerActivity<T> extends AppCompatActivity
        implements ImagePageFragment.OnShowContentViewListener {

    private FrameLayout topLayout, bottomLayout;
    private ImagePageFragment mImagePageFragment;
    private ImagePageFragment.OnSimplePagerSelectListener pagerSelectListener;

    private int startPos;
    private int imgShowedDataSize;
    private ArrayList<T> imgShowDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previewpager);
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }
        initView();
        imgShowDatas = (ArrayList<T>) getIntent().getParcelableArrayListExtra(LISConstant.PRE_IMG_DATAS);
        startPos = getIntent().getIntExtra(LISConstant.PRE_IMG_START_POSITION, 0);
        if (imgShowDatas != null) {
            imgShowedDataSize = imgShowDatas.size();
        }
        onMyCreate(savedInstanceState);
        mImagePageFragment = new ImagePageFragment();
        mImagePageFragment.initArguments(startPos, imgShowDatas);
        mImagePageFragment.setOnPagerSelectListener(pagerSelectListener);
        mImagePageFragment.setOnShowContentViewListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.previewpager_content, mImagePageFragment).commit();
    }

    public T getItem(int position) {
        return imgShowDatas.get(position);
    }

    public void setOnSimplePagerSelectListener(ImagePageFragment.OnSimplePagerSelectListener listener) {
        pagerSelectListener = listener;
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
    }


    protected int getStartPos() {
        return startPos;
    }

    protected int getAllDatasSize() {
        return imgShowedDataSize;
    }

    public abstract int getTopBarLayoutId();

    public abstract int getBottomLayoutId();

    public abstract void onMyCreate(Bundle savedInstanceState);

    public abstract Fragment showContentFragment(T content);

    @Override
    public Fragment getContentFragment(Object content) {
        return showContentFragment((T) content);
    }


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
}
