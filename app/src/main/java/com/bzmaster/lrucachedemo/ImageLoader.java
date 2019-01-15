package com.bzmaster.lrucachedemo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Author:Administrator
 * Date:2019/1/15
 * Email:zxh1786619259@163.com
 * Desc: 创建一个图片加载的类，用于对缓存的一些操作，重写LruCache的sizeOf方法：
 */
public class ImageLoader {
    //图片缓存
    private LruCache<String , Bitmap> mLruCache;

    public ImageLoader() {
        //可使用的最大内存
        long maxMemory=Runtime.getRuntime().maxMemory();
         //取1/4的可用内存作为缓存
        int cacheSize= (int) (maxMemory/4);
        mLruCache=new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    /**
     * 把Bitmap对象加入到缓存中
     * @param key
     * @param bitmap
     * */
    public void addBitmapToMemory(String key,Bitmap bitmap){
        if(getBitmapFromCache(key)==null){ //如果缓存中不存在
            mLruCache.put(key,bitmap);
        }
    }

    /**
     * 从缓存中获取bitmap对象
     * @param key
     * */
    public Bitmap getBitmapFromCache(String key){
        Log.e("==mLruCache===", "lrucache size: " + mLruCache.size());
        return mLruCache.get(key);
    }
    /**
     * 从缓存中删除bitmap对象
     * */
    public void removeBitmapFromCache(String key){
        mLruCache.remove(key);
    }
}
