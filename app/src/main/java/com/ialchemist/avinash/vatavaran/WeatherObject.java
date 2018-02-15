/*Name: Avinash Rohidas Gunjal
  Android app developer
  @copyright & reserved content
*/
package com.ialchemist.avinash.vatavaran;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by AVI on 15-09-2017.
 */

public class WeatherObject {

    @SerializedName("main")
    Weather weathers;

    @SerializedName("weather")
    ArrayList<WeatherDesc> wd;

    @SerializedName("sys")
    SunsetDetails sunsetDetails;

    public SunsetDetails getSunsetDetails() {
        return sunsetDetails;
    }

    public void setSunsetDetails(SunsetDetails sunsetDetails) {
        this.sunsetDetails = sunsetDetails;
    }

    public Weather getWeathers() {
        return weathers;
    }

    public void setWeathers(Weather weathers) {
        this.weathers = weathers;
    }

    public ArrayList<WeatherDesc> getWd() {
        return wd;
    }

    public void setWd(ArrayList<WeatherDesc> wd) {
        this.wd = wd;
    }

    @SerializedName("name")
    private String city_name;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
}
