package com.lisdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocolover2.lis.activity.LisSimplePreviewPagerActivity;
import com.cocolover2.lis.entity.ImageItem;
import com.cocolover2.lis.fragment.ImagePageFragment;

public class MyPreActivity extends LisSimplePreviewPagerActivity<ImageItem> {
    private TextView topTitle;
    private ImageView select;
    private TextView sizeTv;
    ImageItem mItem;
    private int currentPos;

    @Override
    public int getTopBarLayoutId() {
        return R.layout.pre_top_bar;
    }

    @Override
    public int getBottomLayoutId() {
        return R.layout.pre_bottom_bar;
    }

    @Override
    public void onMyCreate(Bundle savedInstanceState) {
        topTitle = (TextView) findViewById(R.id.pre_top_bar_title);
        sizeTv = (TextView) findViewById(R.id.pre_bottom_bar_size);
        select = (ImageView) findViewById(R.id.pre_bottom_bar_select);
        topTitle.setText("(" + getStartPos() + "/" + getAllDatasSize() + ")");
        currentPos = getStartPos();
        mItem = getItem(currentPos);
        setMaxSize(3);
        updateBottom();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem.isSelected) {
                    removeSelectItem(mItem);
                    sizeTv.setVisibility(View.INVISIBLE);
                    select.setImageResource(R.drawable.ic_select_no);
                } else {
                    if (addToSelectList(mItem)) {
                        select.setImageResource(R.drawable.ic_select_yes);
                        sizeTv.setVisibility(View.VISIBLE);
                        sizeTv.setText(getSelectImgsSize());
                    }
                }
            }
        });
        setOnSimplePagerSelectListener(pagerSelectListener);
    }

    private void updateBottom() {
        if (mItem.isSelected) {
            sizeTv.setVisibility(View.VISIBLE);
            sizeTv.setText(getSelectImgsSize());
            select.setImageResource(R.drawable.ic_select_yes);
        } else {
            sizeTv.setVisibility(View.INVISIBLE);
            select.setImageResource(R.drawable.ic_select_no);
        }
    }

    private ImagePageFragment.OnSimplePagerSelectListener pagerSelectListener = new ImagePageFragment.OnSimplePagerSelectListener() {
        @Override
        public void onSelect(int position) {
            currentPos = position;
            mItem = getItem(position);
            updateBottom();
            topTitle.setText("(" + position + "/" + getAllDatasSize() + ")");
        }
    };

}
