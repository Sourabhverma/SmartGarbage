package com.example.smartgarbage;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Field;


public interface RetrofitAPI {

    @GET("bins-all")
    Call<JsonObject> getBinAll();

    @GET("bins-alert")
    Call<JsonObject> getBins(@Query("region") String region, @Query("status")String status);

    @PUT("reset-bin-status")
    Call<JsonObject> updateBinsStatus(@Field("bin_id") String bin_id, @Field("region")String region);
}
