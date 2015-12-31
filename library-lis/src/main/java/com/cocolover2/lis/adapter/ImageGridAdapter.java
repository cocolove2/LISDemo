package com.cocolover2.lis.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cocolover2.lis.AlbumHelper;
import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.LocalImageLoader;
import com.cocolover2.lis.interf.OnSelectResultListener;
import com.cocolover2.lis.R;
import com.cocolover2.lis.entity.ImageItem;

import java.util.ArrayList;


public class ImageGridAdapter extends BaseAdapter {
    final String FILTER_COLOR = "#88000000";
//    private int maxSize = 9;
    private OnSelectResultListener resultCallBack = null;
    private ArrayList<ImageItem> dataList;// 数据源
    private Context context;
    private RelativeLayout.LayoutParams lp = null;
    private static boolean isPreView;//是否支持预览

//    public void setMaxSelect(int maxSize) {
//        this.maxSize = maxSize;
//    }

    public static void setIsPreView(boolean flag) {
        isPreView = flag;
    }

    public void setResultCallBack(OnSelectResultListener resultCallBack) {
        this.resultCallBack = resultCallBack;
    }


    public ImageGridAdapter(Context context, ArrayList<ImageItem> dataList, int imgSize) {
        this.dataList = dataList;
        this.context = context;
        lp = new RelativeLayout.LayoutParams(imgSize, imgSize);
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void refresh() {
        AlbumHelper.initDataList(dataList);
        notifyDataSetChanged();
    }

    @Override
    public ImageItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final ImageItem item = dataList.get(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.gridview_select_pic_item, parent, false);
            holder.iv = (ImageView) convertView
                    .findViewById(R.id.select_pic_item_iv_img);
            holder.selected = (ImageView) convertView
                    .findViewById(R.id.select_pic_item_iv_check);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.iv.setLayoutParams(lp);

        if (AlbumHelper.getMaxSize() <= 1) {
            holder.selected.setVisibility(View.INVISIBLE);
        } else {
            holder.selected.setVisibility(View.VISIBLE);
            if (item.isSelected) {
                holder.selected.setImageResource(R.drawable.ic_select_yes);
                //设置选中变暗
                holder.iv.setColorFilter(Color.parseColor(FILTER_COLOR));
            } else {
                holder.selected.setImageResource(R.drawable.ic_select_no);
                holder.iv.setColorFilter(null);
            }
        }
        LocalImageLoader.getInstance().loadImage(item.imagePath, holder.iv, R.drawable.default_img);
        setClickListener(holder, item, position);
        return convertView;
    }

    private void setClickListener(final Holder holder, final ImageItem item, final int position) {
        holder.selected.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelectClicked(item, holder);
            }
        });
        holder.iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPreView && AlbumHelper.getMaxSize() > 1) {
                    doPreViewImgClicked(position);
                } else {
                    doSelectClicked(item, holder);
                }
            }
        });

    }

    private void doPreViewImgClicked(int position) {
        Intent intent = new Intent(LISConstant.ACTION_PRE);
        intent.addCategory(context.getPackageName() + LISConstant.CATEGORY_SUFFIX);
        intent.putExtra(LISConstant.PRE_IMG_START_POSITION, position);
        intent.putParcelableArrayListExtra(LISConstant.PRE_IMG_DATAS, dataList);
        context.startActivity(intent);
    }

    private void doSelectClicked(ImageItem item, Holder holder) {
        if (item.isSelected) {
            holder.selected.setImageResource(R.drawable.ic_select_no);
            AlbumHelper.removeItem(item);
            holder.iv.setColorFilter(null);
            item.isSelected = false;
        } else {
            if (AlbumHelper.getHasSelectCount() >= AlbumHelper.getMaxSize()) {
                if (AlbumHelper.getMaxSize() > 0)
                    Toast.makeText(context, "最多选择" + AlbumHelper.getMaxSize() + "张图片", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "当前你不能选择图片", Toast.LENGTH_SHORT).show();
            } else {
                holder.selected.setImageResource(R.drawable.ic_select_yes);
                holder.iv.setColorFilter(Color.parseColor(FILTER_COLOR));
                AlbumHelper.addtoSelectImgs(item);
                item.isSelected = true;
            }
        }
        if (resultCallBack != null)
            resultCallBack.onSelectImgs(AlbumHelper.getHasSelectCount());
    }


    final static class Holder {
        private ImageView iv;
        private ImageView selected;
    }

}
