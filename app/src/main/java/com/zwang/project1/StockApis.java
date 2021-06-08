package com.zwang.project1;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface StockApis {
    @Headers({"x-rapidapi-key: def66e2ad7msh812217da7d5b03fp19e120jsnd95c93d205bb",
              "x-rapidapi-host: twelve-data1.p.rapidapi.com"})
    @GET("price")
    Call<ResponseBody> getRealTime(@Query("symbol") String symbol);


    @Headers({"x-rapidapi-key: def66e2ad7msh812217da7d5b03fp19e120jsnd95c93d205bb",
            "x-rapidapi-host: twelve-data1.p.rapidapi.com"})
    @GET("quote")
    Call<Stock> getDetail(@Query("symbol") String symbol);
}
