package com.cocolover2.lis;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;

import com.cocolover2.lis.entity.ImageBucket;
import com.cocolover2.lis.entity.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public final class AlbumHelper {
    //存储文件夹名，和文件夹内容
    private HashMap<Integer, ImageBucket> bucketMap = new HashMap<>();
    //所有的图片集合
    private ArrayList<ImageItem> imageList = new ArrayList<>();
    private static ArrayList<ImageItem> hasSelectImgs = new ArrayList<>();

    /**
     * 获取sd卡中的图片，并创建图片文件夹的列表
     */
    private void buildImagesBucketList(Context context) {
        File file;
        String[] columns = {Media._ID, Media.BUCKET_ID, Media.DATA, Media.BUCKET_DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
                "_id DESC");
        if (cur != null && cur.getCount() <= 0) {
            throw new NullPointerException("do not  find any picture");
        }
        if (cur != null && cur.moveToFirst()) {
            final int photoIdIndex = cur.getColumnIndexOrThrow(Media._ID);
            final int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);//图片路径字段
            final int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
            final int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
            do {
                int id = cur.getInt(photoIdIndex);
                String path = cur.getString(photoPathIndex);
                int bucketId = cur.getInt(bucketIdIndex);
                String bucketname = cur.getString(bucketDisplayNameIndex);
                file = new File(path);
                if (file.exists() && file.length() == 0)
                    continue;
                ImageItem i = new ImageItem();
                i.imageId = id;
                i.imagePath = path;
                i.imageSize = file.length();
                i.createTime = file.lastModified();
                imageList.add(i);
                ImageBucket bucket;
                if (this.bucketMap.containsKey(bucketId))
                    bucket = this.bucketMap.get(bucketId);
                else {
                    bucket = new ImageBucket();
                    bucketMap.put(bucketId, bucket);
                    bucket.bucketName = bucketname;
                    bucket.imageList = new ArrayList<>();
                }
                bucket.count++;
                bucket.imageList.add(i);
            } while (cur.moveToNext());
        }
        if (cur != null)
            cur.close();
    }

    /**
     * 获取图片的文件夹列表
     *
     * @return
     */
    public ArrayList<ImageBucket> getImageBucketList(Context context) {
        if (bucketMap == null || bucketMap.size() == 0)
            buildImagesBucketList(context);
        ArrayList<ImageBucket> allBuckets = new ArrayList<>();
        ImageBucket allBucket = new ImageBucket();
        allBucket.bucketName = LISConstant.ALL_IMG_BUCKET;
        allBucket.imageList = imageList;
        allBucket.count = imageList.size();
        allBucket.isSelected = true;
        allBuckets.add(allBucket);
        for (Map.Entry<Integer, ImageBucket> entry : bucketMap.entrySet()) {
            allBuckets.add(entry.getValue());
        }
        return allBuckets;
    }

    public static void clearSelectedImgs() {
        hasSelectImgs.clear();
    }

    public static ArrayList<ImageItem> getHasSelectImgs() {
        return hasSelectImgs;
    }

    public static boolean addtoSelectImgs(ImageItem imageItem) {
        return hasSelectImgs.add(imageItem);
    }

    public static boolean addtoSelectImgs(ArrayList<ImageItem> list) {
        return hasSelectImgs.addAll(list);
    }

    public static void removeItem(ImageItem item) {
        final int _size = hasSelectImgs.size();
        for (int j = 0; j < _size; j++) {
            if (item.imageId == hasSelectImgs.get(j).imageId) {
                hasSelectImgs.remove(j);
                break;
            }
        }
    }

    public static int getHasSelectCount() {
        return hasSelectImgs.size();
    }

    public static void initDataList(ArrayList<ImageItem> dataRes) {
        final int size = hasSelectImgs.size();
        for (ImageItem img : dataRes) {
            img.isSelected = false;
        }
        for (int i = 0; i < size; i++) {
            for (ImageItem img : dataRes) {
                if (img.imageId == hasSelectImgs.get(i).imageId) {
                    img.isSelected = true;
                    break;
                }
            }
        }
    }
}
