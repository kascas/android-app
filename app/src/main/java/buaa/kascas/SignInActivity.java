package buaa.kascas;

import static util.ActivityUtils.getEncryptedSharedPreferences;
import static util.ActivityUtils.setDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import models.RspModel;
import models.structs.RespStatus;
import models.structs.Token;
import models.structs.User;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.PostRequest;
import util.ActivityUtils;

public class SignInActivity extends AppCompatActivity {

    private ActivityUtils.MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // 发送广播，关闭WelcomeActivity
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CLOSE_ALL");
        sendBroadcast(intent);

        receiver = new ActivityUtils.MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SIGNIN");
        registerReceiver(receiver, filter);

        // 自动填充用户名和密码
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(SignInActivity.this);
        String user = sharedPreferences.getString("user", "");
        String passwd = sharedPreferences.getString("passwd", "");
        ((EditText) findViewById(R.id.editName)).setText(user);
        ((EditText) findViewById(R.id.editPasswd)).setText(passwd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销监听器
        unregisterReceiver(receiver);
    }

    /**
     * 注册按钮的监听事件
     *
     * @param view ...
     */
    public void onClickSignUp(View view) {
        EditText editName = findViewById(R.id.editName);
        EditText editPasswd = findViewById(R.id.editPasswd);
        String user = editName.getText().toString(), passwd = editPasswd.getText().toString();
        if (user.equals("") || passwd.equals("")) {
            setDialog(SignInActivity.this, "出错啦 QAQ", "用户名和密码不能为空");
            return;
        }
        // 通过Retrofit构建请求
        PostRequest postRequest = HttpsManager.getRetrofit(SignInActivity.this).create(PostRequest.class);
        Call<RspModel<Token>> resp = postRequest.signUp(new User(user, passwd));
        resp.enqueue(new MyCallback<Token>() {
            @Override
            protected void success(RespStatus respStatus, Token data) {
                if (data != null) {
                    // 将user和passwd写入sharedPreferences
                    SharedPreferences sharedPreferences = getEncryptedSharedPreferences(SignInActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user", user);
                    editor.putString("passwd", passwd);
                    editor.putString("AccessToken", "Bearer " + data.getAccessToken());
                    editor.putString("RefreshToken", "Bearer " + data.getRefreshToken());
                    editor.apply();
                    // 设置token
                    HttpsManager.setAccessToken("Bearer " + data.getAccessToken());
                    HttpsManager.setRefreshToken("Bearer " + data.getRefreshToken());
                    // 启动Token刷新服务
                    Intent serviceIntent = new Intent(SignInActivity.this, TokenService.class);
                    startService(serviceIntent);
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    //ActivityUtils.setDialog(SignInActivity.this, "注册成功 =ω=", "");
                    Log.d("signup", "AccessToken: " + HttpsManager.getAccessToken() + "\n" + "RefreshToken: " + HttpsManager.getRefreshToken());
                } else {
                    Log.d("signup", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
                    ActivityUtils.setDialog(SignInActivity.this, "数据弄丢啦 QAQ", respStatus.getMsg());
                }
            }

            @Override
            protected void failed(RespStatus respStatus, Call<RspModel<Token>> call) {
                ActivityUtils.setDialog(SignInActivity.this, "似乎出了点问题 QAQ", respStatus.getMsg());
                Log.d("signup", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
            }
        });
    }

    /**
     * 登录按钮的监听事件
     *
     * @param view ...
     */
    public void onClickSignIn(View view) {
        EditText editName = findViewById(R.id.editName);
        EditText editPasswd = findViewById(R.id.editPasswd);
        String user = editName.getText().toString(), passwd = editPasswd.getText().toString();
        if (user.equals("") || passwd.equals("")) {
            setDialog(SignInActivity.this, "出错啦 QAQ", "用户名和密码不能为空");
            return;
        }
        // 通过Retrofit构建请求
        PostRequest postRequest = HttpsManager.getRetrofit(SignInActivity.this).create(PostRequest.class);
        Call<RspModel<Token>> resp = postRequest.signIn(new User(user, passwd));
        resp.enqueue(new MyCallback<Token>() {
            @Override
            protected void success(RespStatus respStatus, Token data) {
                if (data != null) {
                    // 将user和passwd写入sharedPreferences
                    SharedPreferences sharedPreferences = getEncryptedSharedPreferences(SignInActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user", user);
                    editor.putString("passwd", passwd);
                    editor.putString("AccessToken", "Bearer " + data.getAccessToken());
                    editor.putString("RefreshToken", "Bearer " + data.getRefreshToken());
                    editor.apply();
                    // 设置token
                    HttpsManager.setAccessToken("Bearer " + data.getAccessToken());
                    HttpsManager.setRefreshToken("Bearer " + data.getRefreshToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    //ActivityUtils.setDialog(SignInActivity.this, "登录成功 =ω=", "");
                    Log.d("signin", "AccessToken: " + HttpsManager.getAccessToken() + "\n" + "RefreshToken: " + HttpsManager.getRefreshToken());
                } else {
                    ActivityUtils.setDialog(SignInActivity.this, "数据弄丢啦 QAQ", respStatus.getMsg());
                    Log.d("signin", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
                }
            }

            @Override
            protected void failed(RespStatus respStatus, Call<RspModel<Token>> call) {
                ActivityUtils.setDialog(SignInActivity.this, "似乎出了点问题 QAQ", respStatus.getMsg());
                Log.d("signin", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
            }
        });
    }
}