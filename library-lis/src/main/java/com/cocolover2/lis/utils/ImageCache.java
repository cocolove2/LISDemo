package com.cocolover2.lis.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

public class ImageCache {
    private static LruCache<String, Bitmap> mCache;

    private ImageCache() {
    }

    public static LruCache<String, Bitmap> getInstance() {
        if (mCache == null)
            synchronized (ImageCache.class) {
                if (mCache == null)
                    mCache = new LruCache<String, Bitmap>((int) Runtime.getRuntime().maxMemory() / 8) {
                        @Override
                        protected int sizeOf(String key, Bitmap value) {
                            if (Build.VERSION.SDK_INT > 12)
                                return value.getByteCount();
                            else
                                return value.getRowBytes() * value.getHeight();
                        }
                    };
            }
        return mCache;
    }
}
