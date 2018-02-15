package com.ialchemist.avinash.vatavaran;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AVI on 15-09-2017.
 */

public class SunsetDetails {

    @SerializedName("sunrise")
    private String sunrise;

    @SerializedName("sunset")
    private String sunset;

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }
}
