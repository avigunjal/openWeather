package com.ialchemist.avinash.vatavaran;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AVI on 15-09-2017.
 */

public class ApiClient {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static Retrofit retrofit= null;

    public static Retrofit getApiClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
