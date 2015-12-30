package com.cocolover2.lis;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocolover2.lis.interf.OnImageClickListener;
import com.cocolover2.lis.utils.ImageCache;
import com.cocolover2.lis.utils.ImageUtils;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ShowImageView extends Fragment {
    private String key_prefix = "big_img_";
    private PhotoView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_preview_img, container, false);
    }

    Bitmap bp;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            imageView = (PhotoView) getView().findViewById(R.id.layout_preview_img_img);
        }
        final String url = getArguments().getString("imagePath");
        if (ImageCache.getInstance().get(key_prefix + url) == null) {
            bp = ImageUtils.compressImgBySize(url, 480, 800);
            ImageCache.getInstance().put(key_prefix + url, bp);
        } else {
            bp = ImageCache.getInstance().get(key_prefix + url);
        }


        imageView.setImageBitmap(bp);
        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null)
                    listener.onImgClick();
            }
        });
    }


    public static ShowImageView newInstance(String imgPath) {
        ShowImageView fragment = new ShowImageView();
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", imgPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    private OnImageClickListener listener;

    public void setOnImgClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

}
