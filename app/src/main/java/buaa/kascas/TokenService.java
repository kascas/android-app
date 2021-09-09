package buaa.kascas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import models.RspModel;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.GetRequest;
import util.ActivityUtils;

public class TokenService extends Service {

    private Handler tokenHandler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        setTokenHandler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        tokenHandler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void setTokenHandler() {
        tokenHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Runnable run = this;
                Context context = ActivityUtils.getContext();
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
                        tokenHandler.removeCallbacks(run);
                        Log.d("setTokenHandler-3", respStatus.getMsg());
                    }
                });
                tokenHandler.postDelayed(this, 1000 * 50);
            }
        };
        tokenHandler.postDelayed(runnable, 1000 * 50);
    }
}