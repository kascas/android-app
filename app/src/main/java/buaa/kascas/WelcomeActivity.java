package buaa.kascas;

import static util.ActivityUtils.getEncryptedSharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import models.RspModel;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.GetRequest;
import util.ActivityUtils;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityUtils.MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //取消显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        HttpsManager.retrofitInitialize(WelcomeActivity.this);

        // 延时2秒跳转下一个activity
        handler.sendEmptyMessageDelayed(0, 2000);

        // 接收器，在跳转到下一个activity后接受广播信息，关闭此activity
        receiver = new ActivityUtils.MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_WELCOME");
        filter.addAction("android.intent.action.CLOSE_ALL");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销监听器
        unregisterReceiver(receiver);
    }

    // handler延时提交任务
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            getNewToken();
        }
    };

    private void getNewToken() {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(WelcomeActivity.this);
        HttpsManager.setAccessToken(sharedPreferences.getString("AccessToken", ""));
        HttpsManager.setRefreshToken(sharedPreferences.getString("RefreshToken", ""));

        GetRequest getRequest = HttpsManager.getRetrofit(WelcomeActivity.this).create(GetRequest.class);
        Call<RspModel<String>> resp = getRequest.refresh(HttpsManager.getRefreshToken());
        resp.enqueue(new MyCallback<String>() {
            @Override
            protected void success(RespStatus innerRespStatus, String token) {
                if (token != null) {
                    HttpsManager.setAccessToken("Bearer " + token);
                    Log.d("getNewToken-1", "AccessToken: " + HttpsManager.getAccessToken());
                    //获取token后跳转到下一个Activity
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("user", sharedPreferences.getString("user", ""));
                    startActivity(intent);
                } else {
                    Log.d("getNewToken-2", innerRespStatus.getMsg());
                }
            }

            @Override
            protected void failed(RespStatus respStatus, Call<RspModel<String>> call) {
                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(intent);
                Log.d("getNewToken-3", respStatus.getMsg());
            }
        });
    }
}