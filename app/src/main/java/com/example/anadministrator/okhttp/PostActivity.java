package com.example.anadministrator.okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 请输入qq号码
     */
    private EditText mEtQq;
    /**
     * 请输入密码
     */
    private EditText mEtPwd;
    /**
     * 登陆状态:
     */
    private TextView mTvStatus;
    /**
     * 登陆
     */
    private Button mButLogin;
    String path = "http://169.254.53.96:8080/web/LoginServlet";
    String ImagePath = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2447865526,1440260817&fm=27&gp=0.jpg";
    //正则表达式
    String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initView();

    }

    private void initView() {
        mEtQq = (EditText) findViewById(R.id.et_qq);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mButLogin = (Button) findViewById(R.id.butLogin);
        mButLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butLogin://登录时 提交
                final String edQQ = getEdString(mEtQq);
                final String edPwd = getEdString(mEtPwd);
                if(edQQ==null){

                    Toast.makeText(PostActivity.this,"账号不为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edPwd==null){
                    Toast.makeText(PostActivity.this,"密码不为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                //利用正则表达式
                boolean matches = edPwd.matches(regex);
                if(matches==false){
                    Toast.makeText(PostActivity.this,"密码:字母文字都要有,8-16位之间",Toast.LENGTH_SHORT).show();
                }
                new Thread(){
                    @Override
                    public void run() {
                        //创建OkHttpClient对象,并设置超时,读取,写入时间
                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(10, TimeUnit.SECONDS)
                                .writeTimeout(10, TimeUnit.SECONDS)
                                .build();
                        //创建表单对象  Key 固定 照着服务器来
                        FormBody formBody = new FormBody.Builder()
                                .add("qq", edQQ)
                                .add("pwd", edPwd)
                                .build();

                        //创建请求对象
                        Request request = new Request.Builder()
                                .post(formBody)
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
                                final String string = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTvStatus.setText(string);
                                    }
                                });
                            }
                        });

                    }
                }.start();


                break;
        }
    }

    public String getEdString(EditText editText){
            String s = editText.getText().toString();
            if(s!=null && !s.equals("")){
                return  s;
            }
            return  null;
        }
}
