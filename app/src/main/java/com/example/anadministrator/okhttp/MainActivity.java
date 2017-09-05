package com.example.anadministrator.okhttp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String path = "http://www.csdn.net/";
    String ImagePath = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2447865526,1440260817&fm=27&gp=0.jpg";
    String FilePath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 同步请求
     */
    private Button mButSyn;
    /**
     * 异步请求
     */
    private Button mButAsyn;
    private RelativeLayout mActivityMain;
    /**
     * 本地缓存
     */
    private Button mButCache;
    /**
     * 本地缓存
     */
    private Button mButPost;
    /**
     * 下载图片
     */
    private Button mButDownloadImage;
    /**
     * 上传图片
     */
    private Button mButUploadImage;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void SynRequest() {
        new Thread() {
            @Override
            public void run() {
                //创建Client
                OkHttpClient client = new OkHttpClient.Builder().build();
                //创建请求对象
                Request request = new Request.Builder().url(path).build();
                try {
                    //同步
                    Response response = client.newCall(request).execute();

                    String string = response.body().string();

                    System.out.println(string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    //异步
    private void okRequest() {
        //先创建okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个Request
        Request request = new Request.Builder()
                .url(path)
                .build();
        //得到一个newcall
        Call call = okHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                System.out.println(string);
            }
        });
    }


    private void initView() {
        mButSyn = (Button) findViewById(R.id.butSyn);
        mButSyn.setOnClickListener(this);
        mButAsyn = (Button) findViewById(R.id.butAsyn);
        mButAsyn.setOnClickListener(this);
        mActivityMain = (RelativeLayout) findViewById(R.id.activity_main);
        mButCache = (Button) findViewById(R.id.butCache);
        mButCache.setOnClickListener(this);
        mButPost = (Button) findViewById(R.id.butPost);
        mButPost.setOnClickListener(this);
        mButDownloadImage = (Button) findViewById(R.id.butDownloadImage);
        mButDownloadImage.setOnClickListener(this);
        mButUploadImage = (Button) findViewById(R.id.butUploadImage);
        mButUploadImage.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.ImageView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butSyn://同步请求数据
                SynRequest();
                break;
            case R.id.butAsyn://异步请求数据
                okRequest();
                break;
            case R.id.butCache://缓存
                Cache();
                break;
            case R.id.butPost://跳转到第二个界面 进行Post请求
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                break;
            case R.id.butDownloadImage://下载图片
                DownloadImage();
                break;
            case R.id.butUploadImage://上传图片
                //注意:有时候上传图片失败,是服务器规定还要一个Key,如果开发中关于网络这一块处理问题,需要和web端进行交流~
                UploadImage();

                break;
        }
    }

    private void UploadImage() {
        //上传到的服务器网址
        String  url="http://123.206.14.104:8080/FileUploadDemo/FileUploadServlet";
        //要上传的文件
        File file = new File(FilePath, "a.jpg");
        //创建RequestBody封装参数
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        //创建MultipartBody,给RequestBody进行设置
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "a.jpg", requestBody)
                .build();
        //创建okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        //创建Request对象
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        //上传完之后得到服务器的反馈
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                System.out.println("服务器返回的:"+string);
            }
        });


    }

    private void DownloadImage() {
        //先创建okHttpClient对象   异步
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //创建一个Request
        Request request = new Request.Builder()
                .url(ImagePath)
                .build();
        //得到一个newcall
        Call call = okHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(bitmap);
                    }
                });
                //将图片保存到本地
                File file = new File(FilePath, "a.jpg");
//                System.out.println("地址:"+file.getPath());//地址:/mnt/sdcard/a.jpg
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//压缩成图片  保存在fos
            }
        });

    }


    private void Cache() {
        //首先定义缓存大小
        int CacheSize = 10 * 1024 * 1024;//默认为字节 1kb=1024b  此处为10M

        //创建Cache对象,文件存放在私有目录
        Cache cache = new Cache(getCacheDir(), CacheSize);
        System.out.println("地址:" + getCacheDir().getPath());//地址:/data/data/com.example.anadministrator.okhttp/cache
        //先创建okHttpClient对象

        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache).build();
        //创建一个Request
        Request request = new Request.Builder()
                .url(path)
                .build();
        //得到一个newcall
        Call call = okHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                System.out.println(string);
            }
        });
    }



}
