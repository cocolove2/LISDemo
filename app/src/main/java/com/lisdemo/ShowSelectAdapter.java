package com.lisdemo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cocolover2.lis.LocalImageLoader;
import com.cocolover2.lis.entity.ImageItem;

import java.util.ArrayList;

public class ShowSelectAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private Context context;

    public ShowSelectAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selectimg_show, null);
            vh.iv = (ImageView) convertView.findViewById(R.id.item_selectimg);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            MyPreActivity2.actionIntent(context,data);
            }
        });
        LocalImageLoader.getInstance().loadImage(data.get(position), vh.iv, R.drawable.default_img);
        return convertView;
    }

    static class ViewHolder {
        ImageView iv;
    }
}
