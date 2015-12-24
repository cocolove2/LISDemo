package com.cocolover2.lis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.OnSelectResultListener;
import com.cocolover2.lis.R;


public abstract class LisSimpleListImgsActivity extends LisBaseListImgsActivity
        implements OnSelectResultListener, View.OnClickListener {
    private Button chooseBucketBtn;
    private ImageView bottomIcon;
    private Button preBtn;


    @Override
    public int getBottomLayoutId() {
        return R.layout.lis_imglist_bottom_layout;
    }

    @Override
    public void onMyCreate(Bundle savedInstanceState) {
        chooseBucketBtn = (Button) findViewById(R.id.lis_imglist_bottom_bucket_btn);
        bottomIcon = (ImageView) findViewById(R.id.lis_imglist_bottom_bucket_icon);
        preBtn = (Button) findViewById(R.id.lis_imglist_bottom_pre_btn);
        chooseBucketBtn.setText("所有图片");
        chooseBucketBtn.setOnTouchListener(bucketTouchListener);
        chooseBucketBtn.setOnClickListener(this);
        if (getMaxSize() > 1) {
            preBtn.setOnClickListener(this);
            preBtn.setEnabled(false);
            preBtn.setTextColor(getResources().getColor(R.color.dark_gray));
        } else {
            preBtn.setVisibility(View.INVISIBLE);
        }
        setOnSelectResultListener(this);
        setIsPreView(true);
        setMaxSize(9);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPreBtn(getSelectImgCount());
    }

    @Override
    public void onBucketSelect(String bucketName) {
        chooseBucketBtn.setText(bucketName);
    }

    private View.OnTouchListener bucketTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int id = v.getId();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (id == R.id.lis_imglist_bottom_bucket_btn) {
                    chooseBucketBtn.setTextColor(getResources().getColor(com.cocolover2.lis.R.color.dark_gray));
                    bottomIcon.setImageResource(com.cocolover2.lis.R.drawable.ic_bottom_mark_press);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (id == R.id.lis_imglist_bottom_bucket_btn) {
                    bottomIcon.setImageResource(com.cocolover2.lis.R.drawable.ic_bottom_mark_normal);
                    chooseBucketBtn.setTextColor(getResources().getColor(com.cocolover2.lis.R.color.gray));
                }
            }
            return false;
        }
    };


    @Override
    public void onSelectImgs(int selectedCount) {
        initPreBtn(selectedCount);
    }

    private void initPreBtn(int selectedCount) {
        if (selectedCount > 0) {
            preBtn.setText("预览(" + selectedCount + ")");
            preBtn.setEnabled(true);
            preBtn.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            preBtn.setText("预览");
            preBtn.setEnabled(false);
            preBtn.setTextColor(getResources().getColor(R.color.dark_gray));
        }
    }

    @Override
    public void onBackPressed() {
        clearSelectedImgs();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.lis_imglist_bottom_bucket_btn) {
            if (isPopShow())
                hidePop();
            else
                showPop();
        } else if (id == R.id.lis_imglist_bottom_pre_btn) {
            Intent intent = new Intent(LISConstant.ACTION_PRE);
            intent.addCategory(getPackageName() + LISConstant.CATEGORY_SUFFIX);
            intent.putExtra(LISConstant.PRE_IMG_START_POSITION, 0);
            intent.putParcelableArrayListExtra(LISConstant.PRE_IMG_DATAS, getSelectImgs());
            startActivity(intent);
        }
    }
}
