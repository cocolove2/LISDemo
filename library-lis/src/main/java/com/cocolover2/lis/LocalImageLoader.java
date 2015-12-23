package com.cocolover2.lis;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.cocolover2.lis.utils.ImageCache;
import com.cocolover2.lis.utils.ImageUtils;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class LocalImageLoader {
    private final String key_prefix = "loal_thumbnails_";
    private ExecutorService mThreadPool;
    private Type mType = Type.LIFO;
    private LinkedList<Runnable> mTasks;
    private HandlerThread mPoolThread;
    private Handler mPoolThreadHander;
    private WeakHandler mHandler;
    private volatile Semaphore mSemaphore = new Semaphore(0);
    private volatile Semaphore mPoolSemaphore;

    private static LocalImageLoader mInstance;
    private int imgWidth = 180, imgHeight = 180;
    private boolean isFling = false;
    private boolean isScrolling = false;

    public enum Type {
        FIFO, LIFO
    }

    public static LocalImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (LocalImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new LocalImageLoader(3, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private LocalImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<>();
        mType = type == null ? Type.FIFO : type;
        mPoolThread = new HandlerThread("loop thread");
        mPoolThread.start();
        mPoolThreadHander = new Handler(mPoolThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Runnable r = getTask();
                if (r != null) {
                    mThreadPool.execute(r);
                    try {
                        mPoolSemaphore.acquire();
                    } catch (InterruptedException e) {
                    }
                }
                return true;
            }
        });
        // 释放一个信号量
        mSemaphore.release();
    }

    /**
     * 加载图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView, int defaultImgId) {
        if (isScrolling) {
            if (defaultImgId != 0)
                imageView.setImageResource(defaultImgId);
        }

        if (isFling) {
            return;
        }
        // set tag
        imageView.setTag(path);
        // UI线程
        if (mHandler == null) {
            mHandler = new WeakHandler();
        }
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.bitmap = bm;
            holder.imageView = imageView;
            holder.path = path;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    Bitmap bm = ImageUtils.compressImgBySize(path, imgWidth,
                            imgHeight);
                    addBitmapToLruCache(path, bm);
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.bitmap = getBitmapFromLruCache(path);
                    holder.imageView = imageView;
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    mHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }

    }

    /**
     * 设置显示图片的大小
     *
     * @param width
     * @param height
     */
    public void setImgSize(int width, int height) {
        this.imgWidth = width;
        this.imgHeight = height;
    }

    static class WeakHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
            ImageView imageView = holder.imageView;
            Bitmap bm = holder.bitmap;
            String path = holder.path;
            if (imageView.getTag().toString().equals(path) && bm != null) {
                imageView.setImageBitmap(bm);
            }
        }
    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);
        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mTasks.size() == 0)
            return null;
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static LocalImageLoader getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (LocalImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new LocalImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }


    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return ImageCache.getInstance().get(key_prefix + key);
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                ImageCache.getInstance().put(key_prefix + key, bitmap);
        }
    }


    public void clearChache() {
        if (mTasks != null)
            mTasks.clear();
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    int previousFirstVisibleItem = 0;
    long previousEventTime = 0;
    double speed = 0;
    int mScrollState = 0;

    public void setFlingStopLoading(final AbsListView listView) {

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
                if (scrollState == SCROLL_STATE_IDLE) {
                    isScrolling = false;
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previousFirstVisibleItem != firstVisibleItem) {
                    long currTime = System.currentTimeMillis();
                    long timeToScrollOneElement = currTime - previousEventTime;
                    speed = ((double) 1 / timeToScrollOneElement) * 1000;
                    previousFirstVisibleItem = firstVisibleItem;
                    previousEventTime = currTime;
                }
                if (speed > 25 && AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == mScrollState) {
                    isFling = true;
                } else
                    isFling = false;
            }
        });
    }
}
