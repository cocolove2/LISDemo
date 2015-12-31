package com.cocolover2.lis.activity;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.cocolover2.lis.AlbumHelper;
import com.cocolover2.lis.interf.OnImageClickListener;
import com.cocolover2.lis.entity.ImageItem;
import com.cocolover2.lis.ShowImageView;


public abstract class LisSimplePreviewPagerActivity<T> extends LisBasePreviewPagerActivity
        implements OnImageClickListener {
    final long PER_MB = 1024 * 1024;
    final long PER_KB = 1024;

    //针对本地图片
    protected void removeSelectItem(ImageItem item) {
        AlbumHelper.removeItem(item);
        item.isSelected = false;
    }

    protected boolean addToSelectList(ImageItem item) {
        if (AlbumHelper.getHasSelectCount() >= AlbumHelper.getMaxSize()) {
            item.isSelected = false;
            Toast.makeText(this, "最多选择" + AlbumHelper.getMaxSize() + "张图片", Toast.LENGTH_SHORT).show();
            return false;
        }
        item.isSelected = true;
        return AlbumHelper.addtoSelectImgs(item);
    }

    protected String getSelectImgsSize() {
        int size = 0;
        for (ImageItem i : AlbumHelper.getHasSelectImgs()) {
            size += i.imageSize;
        }
        if (size < PER_KB) {//小于1KB
            return size + "B";
        }
        if (size < PER_MB) {
            return size / PER_KB + "KB";
        }
        return size / PER_MB + "MB";
    }

    //针对本地图片(其他的展示界面重写该方法)
    @Override
    public Fragment showContentFragment(T content) {
        if (content instanceof ImageItem) {
            ShowImageView fragment = ShowImageView.newInstance(((ImageItem) content).imagePath);
            fragment.setOnImgClickListener(this);
            return fragment;
        }
        return null;
    }



    @Override
    public void onImgClick() {
        if (isShowTopBottom) {
            hideTopAndBottomLayout();
        } else {
            showTopAndBottomLayout();
        }
    }
}
