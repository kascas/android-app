package network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import models.RspModel;
import models.structs.RespStatus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MyCallback<T> implements Callback<RspModel<T>> {

    @Override
    public void onResponse(@NonNull Call<RspModel<T>> call, Response<RspModel<T>> response) {
        RespStatus respStatus = new RespStatus();
        respStatus.setCode(response.code());
        RspModel<T> rspModel = null;
        // 如果状态码为200~300
        if (response.isSuccessful()) {
            rspModel = response.body();
            if (rspModel != null) {
                respStatus.setStatus(rspModel.getStatus());
                respStatus.setMsg(rspModel.getMsg());
                if (respStatus.getStatus() == 0) { // 如果status为0
                    Log.d("MyCallback-1", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
                    success(respStatus, rspModel.getData());
                } else { // 如果status不为0
                    Log.d("MyCallback-2", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
                    failed(respStatus, call);// 如果status不为0，则状态码为200~300、status不为0
                }
            } else { // 如果rspModel为空
                respStatus.setStatus(1);
                respStatus.setMsg("NoRspModel");
                failed(respStatus, call);// 如果rspModel为空，则状态码为-1、status为1
            }
        } else { // 如果状态码不在200~300
            ResponseBody errorBody = response.errorBody();
            String rawBody = null;
            if (errorBody != null) {
                try {
                    rawBody = errorBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                    respStatus.setStatus(3);
                    failed(respStatus, call);
                    return;
                }
                // 解析errorBody
                rspModel = new Gson().fromJson(rawBody, new TypeToken<RspModel<String>>() {
                }.getType());
                if (rspModel != null) {
                    respStatus.setStatus(rspModel.getStatus());
                    respStatus.setMsg(rspModel.getMsg());
                } else {
                    respStatus.setStatus(1);
                    respStatus.setMsg("NoRspModel");
                }
            } else {
                respStatus.setStatus(2);
                respStatus.setMsg("NoErrorBody");
            }
            failed(respStatus, call);
        }
    }

    @Override
    public void onFailure(@NonNull Call<RspModel<T>> call, Throwable t) {
        RespStatus respStatus = new RespStatus();
        respStatus.setMsg(t.getMessage());
        Log.d("MyCallback-3", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + t.getMessage());
        t.printStackTrace();
        failed(respStatus, call);
    }

    protected abstract void success(RespStatus respStatus, T data);

    protected abstract void failed(RespStatus respStatus, Call<RspModel<T>> call);
}