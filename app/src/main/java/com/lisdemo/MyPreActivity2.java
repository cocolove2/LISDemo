package com.lisdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.ShowImageView;
import com.cocolover2.lis.activity.LisSimplePreviewPagerActivity;
import com.cocolover2.lis.entity.ImageItem;
import com.cocolover2.lis.interf.OnPagerUpdateListener;

import java.util.ArrayList;

public class MyPreActivity2 extends LisSimplePreviewPagerActivity<String> {
    private TextView topTitle;

    @Override
    public int getTopBarLayoutId() {
        return R.layout.pre_top_bar;
    }

    @Override
    public int getBottomLayoutId() {
        return 0;
    }

    @Override
    public void onMyCreate(Bundle savedInstanceState) {
        topTitle = (TextView) findViewById(R.id.pre_top_bar_title);
        topTitle.setText("(" + (getStartPos() + 1) + "/" + getAllDatasSize() + ")");
        setOnPagerUpdateListener(pagerSelectListener);
    }

    @Override
    public Fragment showContentFragment(String content) {
        ShowImageView fragment = ShowImageView.newInstance(content);
        fragment.setOnImgClickListener(this);
        return fragment;
    }

    private OnPagerUpdateListener pagerSelectListener = new OnPagerUpdateListener() {
        @Override
        public void onSelect(int position) {
            topTitle.setText("(" + (position + 1) + "/" + getAllDatasSize() + ")");
        }

        @Override
        public void onDeleted(int position) {

        }
    };

    public static void actionIntent(Context context, ArrayList<String> data) {
        context.startActivity(new Intent(context, MyPreActivity2.class).putStringArrayListExtra(LISConstant.PRE_IMG_DATAS, data));
    }
}
