package com.cocolover2.lis.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片实体类
 */
public class ImageItem implements Parcelable {
    public int imageId;
    public String imagePath;//原图的路径
    public long imageSize;
    public long createTime;
    public boolean isSelected;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(imagePath);
        dest.writeLong(imageSize);
        dest.writeLong(createTime);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            //读取要和写入的顺序一致
            ImageItem item = new ImageItem();
            item.imageId = source.readInt();
            item.imagePath = source.readString();
            item.imageSize = source.readLong();
            item.createTime = source.readLong();
            item.isSelected = (source.readByte() != 0);
            return item;
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
