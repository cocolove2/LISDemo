package com.cocolover2.lis.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cocolover2.lis.AlbumHelper;
import com.cocolover2.lis.LISConstant;
import com.cocolover2.lis.LocalImageLoader;
import com.cocolover2.lis.interf.OnSelectResultListener;
import com.cocolover2.lis.R;
import com.cocolover2.lis.adapter.ImageBucketAdapter;
import com.cocolover2.lis.adapter.ImageGridAdapter;
import com.cocolover2.lis.entity.ImageBucket;
import com.cocolover2.lis.entity.ImageItem;

import java.util.ArrayList;


public abstract class LisBaseListImgsActivity extends AppCompatActivity {
    private ArrayList<ImageItem> dataList = new ArrayList<>();//本地图片的数据源
    private ImageBucket selectedBucket;//先去选择的文件夹
    private ArrayList<ImageBucket> mBucketLists = new ArrayList<>(); //所有图片文件夹

    private int itemWidth;//图片宽度
    private Handler alphaHandler;
    private boolean isChangeAlpha;
    private int mAlpha = 0;
    private OnSelectResultListener selectResultListener;


    private GridView gridView;
    private ImageGridAdapter adapter;
    //pop
    private LinearLayout popLayout;
    private ListView popListview;
    private Animation popAnim;
    private boolean isPopShow;

    private boolean isrefresh;


    public abstract int getTopLayoutId();

    public abstract int getBottomLayoutId();

    public abstract void onMyCreate(Bundle savedInstanceState);

    public abstract void onBucketSelect(String bucketName);


    protected boolean isPopShow() {
        return isPopShow;
    }

    protected boolean isShowActionBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isShowActionBar() && getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.lis_activity_img_list);
        initView();
        onMyCreate(savedInstanceState);
        setUpView();

    }

    private void initView() {
        itemWidth = getResources().getDisplayMetrics().widthPixels / 3;
        gridView = (GridView) findViewById(R.id.lis_activity_img_list_gv);
        gridView.setColumnWidth(itemWidth);
        popLayout = (LinearLayout) findViewById(R.id.layout_bucket_pop_container);
        popListview = (ListView) findViewById(R.id.layout_bucket_pop_listview);
        if (getTopLayoutId() > 0) {
            final FrameLayout topLayout = (FrameLayout) findViewById(R.id.lis_activity_img_list_top);
            final View topView = LayoutInflater.from(this).inflate(getTopLayoutId(), topLayout, false);
            topLayout.addView(topView, topView.getLayoutParams());
        }
        if (getBottomLayoutId() > 0) {
            final FrameLayout bottomLayout = (FrameLayout) findViewById(R.id.lis_activity_img_list_bottom);
            final View bottomView = LayoutInflater.from(this).inflate(getBottomLayoutId(), bottomLayout, false);
            bottomLayout.addView(bottomView, bottomView.getLayoutParams());
        }
        popLayout.getBackground().setAlpha(0);
        popLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPopShow) {
                    hidePop();
                }
            }
        });
    }

    protected void setOnSelectResultListener(OnSelectResultListener listener) {
        selectResultListener = listener;
    }

    protected int getSelectImgCount() {
        return AlbumHelper.getHasSelectCount();
    }

    private void setUpView() {
        if (mBucketLists.size() <= 0)
            mBucketLists = new AlbumHelper().getImageBucketList(this);
        initPop();
        dataList.clear();
        dataList.addAll(mBucketLists.get(0).imageList);
        adapter = new ImageGridAdapter(this, dataList, itemWidth);
        gridView.setAdapter(adapter);
        LocalImageLoader.getInstance().setFlingStopLoading(gridView);
        adapter.setResultCallBack(selectResultListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isrefresh) {
            adapter.refresh();
        }
        isrefresh = false;
    }

    protected void clearSelectedImgs() {
        AlbumHelper.clearSelectedImgs();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isrefresh = true;
    }

    private void changeBucket(ArrayList<ImageItem> imageList) {
        dataList.clear();
        dataList.addAll(imageList);
        gridView.smoothScrollToPosition(0);
        adapter.refresh();
    }

    protected void setMaxSize(int maxSize) {
        AlbumHelper.setMaxSize(maxSize);
    }

    protected int getMaxSize() {
        return AlbumHelper.getMaxSize();
    }

    protected ArrayList<ImageItem> getSelectImgs() {
        return AlbumHelper.getHasSelectImgs();
    }

    protected ArrayList<String> getSelectImgPaths() {
        final ArrayList<String> datas = new ArrayList<>();
        if (getSelectImgs() == null)
            return null;
        else {
            for (ImageItem item : getSelectImgs()) {
                datas.add(item.imagePath);
            }
        }
        return datas;
    }

    protected void setIsPreView(boolean flag) {
        ImageGridAdapter.setIsPreView(flag);
    }

    protected void showPop() {
        isPopShow = true;
        popAnim = AnimationUtils.loadAnimation(this, R.anim.bucket_pop_in);
        popLayout.setVisibility(View.VISIBLE);
        popAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isChangeAlpha = true;
                changeAlpha(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isChangeAlpha = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        popListview.startAnimation(popAnim);
    }

    protected void hidePop() {
        isPopShow = false;
        popAnim = AnimationUtils.loadAnimation(this, R.anim.bucket_pop_out);
        popAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isChangeAlpha = true;
                changeAlpha(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isChangeAlpha = false;
                popLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        popListview.startAnimation(popAnim);
    }

    private void initPop() {
        selectedBucket = mBucketLists.get(0);
        final ImageBucketAdapter bucketAdapter = new ImageBucketAdapter(this, mBucketLists);
        popListview.setAdapter(bucketAdapter);
        popListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ImageBucket curBucket = mBucketLists.get(position);
                if (curBucket != selectedBucket) {
                    ImageView selectBucketFlagIv = (ImageView) view
                            .findViewById(R.id.img_bucket_item_choose_flag);
                    selectBucketFlagIv.setImageResource(R.drawable.ic_selected);
                    curBucket.isSelected = true;
                    selectedBucket.isSelected = false;
                    selectedBucket = curBucket;
                    onBucketSelect(curBucket.bucketName);
                    changeBucket(curBucket.imageList);
                    bucketAdapter.notifyDataSetChanged();
                }
                hidePop();
            }
        });
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    popLayout.getBackground().setAlpha(0);
                    break;
                case 255:
                    popLayout.getBackground().setAlpha(255);
                    break;
                case 1:
                    popLayout.getBackground().setAlpha(mAlpha);
                    break;
            }
            return true;
        }
    });
    private Runnable upAlphaRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAlpha > 255 || !isChangeAlpha) {
                mHandler.sendEmptyMessage(255);
                alphaHandler.removeCallbacks(upAlphaRunnable);
            } else {
                mHandler.sendEmptyMessage(1);
                alphaHandler.postDelayed(upAlphaRunnable, 15);
            }
            mAlpha += 8;
        }
    };

    private Runnable downAlphaRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAlpha < 0 || !isChangeAlpha) {
                mHandler.sendEmptyMessage(0);
                alphaHandler.removeCallbacks(downAlphaRunnable);
            } else {
                mHandler.sendEmptyMessage(1);
                alphaHandler.postDelayed(downAlphaRunnable, 15);
            }
            mAlpha -= 8;
        }
    };

    //更改透明度
    private void changeAlpha(final boolean isIncrement) {
        if (!isChangeAlpha) {
            return;
        }
        final HandlerThread coverAlphaThread = new HandlerThread("changeAlphaThreadd");
        coverAlphaThread.start();
        alphaHandler = new Handler(coverAlphaThread.getLooper());
        if (!isIncrement) {
            mAlpha = 255;
            alphaHandler.post(downAlphaRunnable);
        } else {
            mAlpha = 0;
            alphaHandler.post(upAlphaRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBucketLists.clear();
        mBucketLists = null;
        AlbumHelper.clearSelectedImgs();
    }
}
