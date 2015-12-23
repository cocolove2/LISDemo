package com.cocolover2.lis.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;

public class ImageUtils {
    private ImageUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 压缩图片的像素
     *
     * @param path
     * @param requestWidth
     * @param requestHeight
     * @return
     */
    public static Bitmap compressImgBySize(String path, int requestWidth, int requestHeight) {
        if (null == path || TextUtils.isEmpty(path) || !new File(path).exists())
            return null;
        //第一次解析图片的时候将inJustDecodeBounds设置为true,来获取图片的大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int requestWidth, int requestHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > requestWidth || height > requestHeight) {
            final int heightRatio = Math.round((float) height / (float) requestHeight);
            final int widthRatio = Math.round((float) width / (float) requestWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
