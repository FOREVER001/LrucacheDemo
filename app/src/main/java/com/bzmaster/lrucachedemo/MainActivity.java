package com.bzmaster.lrucachedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageLoader mImageLoader;
    private ImageView imageView;
    private Button btn_load,btn_remove;
    private String bitmapUrl="http://wx4.sinaimg.cn/large/a1b61d0aly1fn2h3xwat6j20dw0dwtbp.jpg";
    private static final String TAG="====MainActivity==";
    private static final int DOWNLOAD_IMAGE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.iv);
        btn_load=findViewById(R.id.btn_load);
        btn_remove=findViewById(R.id.btn_remove);
        mImageLoader=new ImageLoader();
        initEvent();
    }

    private void initEvent() {
        btn_load.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_load:
                showBitmap(bitmapUrl,imageView);
                break;
                case R.id.btn_remove:
                    remove();
                    break;
        }
    }


    class BitmapThread extends Thread{
         private String bitmapUrl;
           BitmapThread(String bitmapUrl) {
            this.bitmapUrl = bitmapUrl;
        }
        @Override
        public void run() {
            super.run();
            Log.e(TAG, "run: " + Thread.currentThread().getName());
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            URL url = null;
            try {
                url = new URL(bitmapUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                mImageLoader.addBitmapToMemory("bitmap", bitmap);
                handler.obtainMessage(DOWNLOAD_IMAGE, bitmap).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "=====hanlder handleMessage:==== " + Thread.currentThread().getName());
            switch (msg.what) {
                case DOWNLOAD_IMAGE:
                    imageView.setImageBitmap((Bitmap) msg.obj);
                    break;
            }
        }
    };

    /**
     * 显示图片
     * @param iv
     * */
    public void showBitmap(String imgUrl,ImageView iv) {
        Bitmap bitmap = mImageLoader.getBitmapFromCache("bitmap");

        if (bitmap == null) {
            Log.e("==showBitmap===", "缓存中没有，直接去网络下载，并且添加到内存缓存中" );

            new BitmapThread(imgUrl).start();
        } else {
            Log.e("==showBitmap===", "bitmap 从缓存中取值" );

            iv.setImageBitmap(bitmap);
        }
    }

    /**
     * 移除缓存
     * */
    public void remove() {
        mImageLoader.removeBitmapFromCache("bitmap");
    }
}
