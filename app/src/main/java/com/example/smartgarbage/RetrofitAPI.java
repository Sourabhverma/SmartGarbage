package com.example.smartgarbage;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @GET("bins-all")
    Call<JsonObject> getBinAll();

    @GET("bins-alert")
    Call<JsonObject> getBins(@Query("region") String region, @Query("status")String status);
}
