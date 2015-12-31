package com.lisdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.activity.LisSimpleListImgsActivity;
import com.cocolover2.lis.entity.ImageItem;

import java.util.ArrayList;

public class MainActivity extends LisSimpleListImgsActivity {
    TextView mTextView;
    Button sureBtn;
    int maxSize;

    @Override
    public int getTopLayoutId() {
        return R.layout.layout_top_bar;
    }

    @Override
    public void onMyCreate(Bundle savedInstanceState) {
        super.onMyCreate(savedInstanceState);
        mTextView = (TextView) findViewById(R.id.top_bar_tv);
        sureBtn = (Button) findViewById(R.id.layout_top_bar_sure);
        mTextView.setText("我是topbar");
        setIsPreView(true);
        maxSize = getIntent().getIntExtra("maxSize", 0);
        setMaxSize(maxSize);

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent().putStringArrayListExtra("datas", getSelectImgPaths());
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getSelectImgs() != null) {
            if (getSelectImgs().size() == 0)
                sureBtn.setText("确定");
            else {
                sureBtn.setText("确定(" + getSelectImgs().size() + "/" + maxSize + ")");
            }
        }
    }

    @Override
    public void onSelectImgs(int selectedCount) {
        super.onSelectImgs(selectedCount);
        sureBtn.setText("确定(" + getSelectImgs().size() + "/" + maxSize + ")");
    }

    public static void actionIntent(Activity context, int maxSize, int requestCode) {
        context.startActivityForResult(new Intent(context, MainActivity.class)
                .putExtra("maxSize", maxSize), requestCode);
    }
}
