package com.ialchemist.avinash.vatavaran;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by AVI on 15-09-2017.
 */

public interface ApiInterface {

    @GET("weather?")
    Call<WeatherObject> getWeatherObjectCall(@Query("q") String city,@Query("units") String units,@Query("appid") String appid );

    @GET("weather?")
    Call<WeatherObject>getWeatherObjectCall1(@Query("lat") String lat,@Query("lon") String lon,@Query("units") String units,@Query("appid") String appid);

}
