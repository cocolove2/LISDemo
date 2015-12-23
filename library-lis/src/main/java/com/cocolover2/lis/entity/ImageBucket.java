package com.cocolover2.lis.entity;

import java.util.ArrayList;

/**
 * 图片文件夹实体类
 */
public class ImageBucket{
    public int count = 0;
    public String bucketName;//文件夹名
    public ArrayList<ImageItem> imageList;//文件夹下所有文件的绝对路径
    public boolean isSelected = false;//文件夹是否被选中
}
