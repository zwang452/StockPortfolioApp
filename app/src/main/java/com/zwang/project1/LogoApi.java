package com.zwang.project1;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LogoApi {

    @GET("suggest")
    Call<List<LogoUrl>> getLogoUrl(@Query("query") String companyName);
}
