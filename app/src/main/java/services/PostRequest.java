package services;

import models.RspModel;
import models.structs.Token;
import models.structs.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;

/**
 * Post请求的接口
 */
public interface PostRequest {

    /**
     * 登录至服务器，body形式application/json
     *
     * @param body Types.User对象
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "POST", path = "signin", hasBody = true)
    Call<RspModel<Token>> signIn(
            @Body User body
    );

    /**
     * 注册至服务器，body形式application/json
     *
     * @param body Types.User对象
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "POST", path = "signup", hasBody = true)
    Call<RspModel<Token>> signUp(
            @Body User body
    );

    /**
     * 注册至服务器，body形式application/json
     *
     * @param data String对象
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "POST", path = "/user/upload", hasBody = true)
    Call<RspModel<String>> upload(
            @Body String data,
            @Header("Authorization") String accessToken
    );
}