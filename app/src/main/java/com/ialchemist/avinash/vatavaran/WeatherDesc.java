package com.ialchemist.avinash.vatavaran;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AVI on 15-09-2017.
 */

public class WeatherDesc {

    @SerializedName("main")
    private String main;

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
