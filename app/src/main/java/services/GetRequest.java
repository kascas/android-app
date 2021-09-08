package services;

import models.RspModel;
import models.structs.Token;
import models.structs.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface GetRequest {
    /**
     * 刷新AccessToken
     *
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "GET", path = "/user/refresh")
    Call<RspModel<String>> refresh(
            @Header("Authorization") String refreshToken
    );
}
