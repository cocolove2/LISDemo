package com.lisdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.cocolover2.lis.entity.ImageItem;

import java.util.ArrayList;


public class ShowSelectedImgs extends AppCompatActivity {
    private ArrayList<ImageItem> mDatas = new ArrayList<>();
    private Button modifyBtn;
    private GridView mGridView;
    ShowSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img);
        modifyBtn = (Button) findViewById(R.id.select_img_modify_btn);
        mGridView = (GridView) findViewById(R.id.select_img_gv);
        adapter = new ShowSelectAdapter(this, mDatas);
        mGridView.setAdapter(adapter);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actionIntent(ShowSelectedImgs.this, mDatas, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            mDatas.clear();
            final ArrayList<ImageItem> tmp = data.getParcelableArrayListExtra("datas");
            mDatas.addAll(tmp);
            adapter.notifyDataSetChanged();
        }
    }

}
