package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import buaa.kascas.SignInActivity;
import models.RspModel;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.GetRequest;

public class ActivityUtils {

    private static Handler tokenHandler;

    public static void setTokenHandler(Context context) {
        tokenHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Runnable runnable1 = this;
                GetRequest getRequest = HttpsManager.getRetrofit(context).create(GetRequest.class);
                Call<RspModel<String>> resp = getRequest.refresh(HttpsManager.getRefreshToken());
                resp.enqueue(new MyCallback<String>() {
                    @Override
                    protected void success(RespStatus respStatus, String token) {
                        if (token != null) {
                            HttpsManager.setAccessToken("Bearer " + token);
                            Log.d("setTokenHandler-1", "AccessToken: " + HttpsManager.getAccessToken());
                        } else {
                            Log.d("setTokenHandler-2", respStatus.getMsg());
                        }
                    }

                    @Override
                    protected void failed(RespStatus respStatus, Call<RspModel<String>> call) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                        tokenHandler.removeCallbacks(runnable1);
                        Log.d("setTokenHandler-3", respStatus.getMsg());
                    }
                });
                tokenHandler.postDelayed(this, 1000 * 60);
            }
        };
        tokenHandler.post(runnable);
    }

    /**
     * 弹出对话框（AlertDialog）
     *
     * @param title 对话框标题
     * @param msg   对话框内容
     */
    public static void setDialog(Context context, String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }

    public static SharedPreferences getEncryptedSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = null;
        try {
            MasterKey.Builder builder = new MasterKey.Builder(context);
            MasterKey masterKey = builder.setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "dp-app",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return sharedPreferences;
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((Activity) context).finish();
        }
    }

}
