package com.pti.cashfreetes;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("")
    Call<CheckSumContainer> getChecksum(@Url String url,
                                        @Header("x-client-id") String clientid,
                                        @Header("x-client-secret") String secret,
                                        @Body RequestBody body);
}
