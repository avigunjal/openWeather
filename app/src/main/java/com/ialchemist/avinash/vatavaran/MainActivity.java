/*Name: Avinash Rohidas Gunjal
  Android app developer
  @copyright & reserved content
*/

package com.ialchemist.avinash.vatavaran;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    static String appid, units;
    ApiInterface apiInterface;
    int[] mycolors;
    CardView cardView;
    TextView tvcity, tvtemp, tvdesc, tvhumi, tvpres, tvsunset, tvsunrise, tv1, tv2, tv3;
    Spinner sp;
    ArrayAdapter<String> adapter;
    ArrayList<String> citylist;
    SQLiteDatabase sqLiteDatabase;
    TextToSpeech tts;
    String toSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logo);

        tvcity = (TextView) findViewById(R.id.citytv);
        tvtemp = (TextView) findViewById(R.id.tvtemp);
        tvdesc = (TextView) findViewById(R.id.tvdesc);
        tvhumi = (TextView) findViewById(R.id.tvhumi);
        tvpres = (TextView) findViewById(R.id.tvpres);
        tvsunrise = (TextView) findViewById(R.id.tvsunrise);
        tvsunset = (TextView) findViewById(R.id.tvsunset);




        cardView = (CardView) findViewById(R.id.mycard);
        mycolors = getResources().getIntArray(R.array.mycolors);
        int randcolor = mycolors[new Random().nextInt(mycolors.length)];
        cardView.setCardBackgroundColor(randcolor);


        appid = "3cfb8c13c778b8281039c0af669ca025";
        units = "metric";

        sqLiteDatabase = openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists citytable(name varchar(30));");
    }

    //network cheaking
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void init() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        final String city_name = sp.getSelectedItem().toString();
        tvcity.setText(city_name);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        final Call<WeatherObject> weatherObjectCall = apiInterface.getWeatherObjectCall(city_name, units, appid);

        weatherObjectCall.enqueue(new Callback<WeatherObject>() {
            @Override
            public void onResponse(Call<WeatherObject> call, Response<WeatherObject> response) {
                progressDialog.dismiss();

                WeatherObject weatherResponseObj = response.body();

                tvtemp.setText(weatherResponseObj.getWeathers().getTemp() + " \u2103");
                tvdesc.setText(weatherResponseObj.getWd().get(0).getMain());
                tvhumi.setText(weatherResponseObj.getWeathers().getHumidity() + " %");
                tvpres.setText(weatherResponseObj.getWeathers().getPressure() + " hpa");

                // tvsunset.setText(weatherResponseObj.getSunsetDetails().getSunset());
                //tvsunrise.setText(weatherResponseObj.getSunsetDetails().getSunrise());

                long sunrise = Long.parseLong(weatherResponseObj.getSunsetDetails().getSunrise());
                //sunrise date
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(sunrise * 1000L);
                String sunrise_date = DateFormat.format("hh:mm", cal).toString();
                tvsunrise.setText(sunrise_date + " AM");

                long sunset_lg = Long.parseLong(weatherResponseObj.getSunsetDetails().getSunset());
                //sunset date
                Calendar cal1 = Calendar.getInstance(Locale.ENGLISH);
                cal1.setTimeInMillis(sunset_lg * 1000L);
                String sunset_date = DateFormat.format("hh:mm", cal1).toString();
                tvsunset.setText(sunset_date + " PM");


                //image setup
                ImageView img = (ImageView) findViewById(R.id.imageView);
                tv1 = (TextView)findViewById(R.id.tv1);
                tv2 = (TextView)findViewById(R.id.tv2);
                tv3 = (TextView)findViewById(R.id.tv3);
                if (weatherResponseObj.getWd().get(0).getMain().equals("Clouds")) {
                    img.setImageResource(R.drawable.clouds);
                    tv1.setText("It's");
                    tv2.setText("full of cloudy");
                    tv3.setText("NATURE");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });


                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Thunderstorm")) {
                    img.setImageResource(R.drawable.storm);
                    tv1.setText("It's");
                    tv2.setText("Bass,Jazz");
                    tv3.setText("Storm");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });
                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Drizzle")) {
                    img.setImageResource(R.drawable.drizzle);
                    tv1.setText("Test");
                    tv2.setText("the smell of rain");
                    tv3.setText("in Drizzle");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }}
                        }
                    });
                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Rain")) {
                    img.setImageResource(R.drawable.rain);
                    tv1.setText("Wow!..");
                    tv2.setText("It's");
                    tv3.setText("Raining today");

                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }}
                        }
                    });

                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Snow")) {
                    img.setImageResource(R.drawable.snow);
                    tv1.setText("It's");
                    tv2.setText("Snowing");
                    tv3.setText("la-la-la");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });
                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Atmosphere")) {
                    img.setImageResource(R.drawable.atmosphire);
                    tv1.setText("Ohh..");
                    tv2.setText("Shit this");
                    tv3.setText("Atmosphere");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });
                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Clear")) {
                    img.setImageResource(R.drawable.clear);
                    tv1.setText("It's");
                    tv2.setText("clear lovely");
                    tv3.setText("Sunshine");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }}
                    });
                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Extreme")) {
                    img.setImageResource(R.drawable.extrem);
                    tv1.setText("Ahh..");
                    tv2.setText("it's");
                    tv3.setText("Extreme");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });
                } else if (weatherResponseObj.getWd().get(0).getMain().equals("Haze")) {
                    img.setImageResource(R.drawable.haze);
                    tv1.setText("Hmm...");
                    tv2.setText("It's");
                    tv3.setText("Haze today");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });
                }
                else if (weatherResponseObj.getWd().get(0).getMain().equals("Mist")) {
                    img.setImageResource(R.drawable.atmosphire);
                    tv1.setText("Hmm...");
                    tv2.setText("It's");
                    tv3.setText("Mist today");
                    toSpeak =tv1.getText().toString()+tv2.getText().toString()+tv3.getText().toString();
                    tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            {
                                tts.setSpeechRate(0);
                                tts.setLanguage(Locale.UK);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }
                    });
                }
                else {
                    img.setImageResource(R.drawable.atmosphire);
                }
            }

            @Override
            public void onFailure(Call<WeatherObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something goes wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.spinner);
        sp = (Spinner) MenuItemCompat.getActionView(menuItem);
        //add city in list
        Cursor result = sqLiteDatabase.rawQuery("select *from citytable", null);
        if (result.getCount() <= 0) {
            Intent i = new Intent(MainActivity.this, AddCity.class);
            startActivity(i);
        } else {
            citylist = new ArrayList<>();
            while (result.moveToNext()) {
                citylist.add(result.getString(0));
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, citylist);
                adapter.notifyDataSetChanged();

                sp.setAdapter(adapter);
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (sp.getSelectedItem().equals(null)) {
                            Toast.makeText(MainActivity.this, "Please Add City", Toast.LENGTH_LONG).show();
                        } else {
                            if(isNetworkAvailable()){
                                init();}else{
                                showalert();
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }
        return super.onCreateOptionsMenu(menu);
    }


    public void showalert(){
        final AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setTitle("Info");
        alert.setMessage("Internet not available,Cross check you connectivity and try again");
        alert.setIcon(R.drawable.info);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spinner:
                return true;

            case R.id.Addcity:
                Intent i = new Intent(MainActivity.this, AddCity.class);
                startActivity(i);
                return true;

            case R.id.current_location:
                Snackbar.make(findViewById(android.R.id.content), "Location is enabling..", Snackbar.LENGTH_LONG).show();
                if(isNetworkAvailable()) {
                    getLocation();
                }else {
                    showalert();
                }
                return true;

            default:

                Toast.makeText(this, "Can't recognize your action", Toast.LENGTH_SHORT).show();
                return true;
        }
    }


    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "You must have to enable location", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 111);
            return;
        } else {
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lati = location.getLatitude();
                    double longi = location.getLongitude();
                    locationManager.removeUpdates(this);
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

                    ApiInterface apiInterface1 = ApiClient.getApiClient().create(ApiInterface.class);
                    final Call<WeatherObject> weatherObjectCall1 = apiInterface1.getWeatherObjectCall1(String.valueOf(lati), String.valueOf(longi), units, appid);

                    weatherObjectCall1.enqueue(new Callback<WeatherObject>() {
                        @Override
                        public void onResponse(Call<WeatherObject> call, Response<WeatherObject> response) {
                            progressDialog.dismiss();

                            WeatherObject weatherResponseObj = response.body();

                            tvcity.setText(weatherResponseObj.getCity_name());
                            Toast.makeText(MainActivity.this, weatherResponseObj.getCity_name(), Toast.LENGTH_SHORT).show();
                            tvtemp.setText(weatherResponseObj.getWeathers().getTemp() + " \u2103");
                            tvdesc.setText(weatherResponseObj.getWd().get(0).getMain());
                            tvhumi.setText(weatherResponseObj.getWeathers().getHumidity() + " %");
                            tvpres.setText(weatherResponseObj.getWeathers().getPressure() + " hpa");

                            // tvsunset.setText(weatherResponseObj.getSunsetDetails().getSunset());
                            //tvsunrise.setText(weatherResponseObj.getSunsetDetails().getSunrise());

                            long sunrise = Long.parseLong(weatherResponseObj.getSunsetDetails().getSunrise());
                            //sunrise date
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(sunrise * 1000L);
                            String sunrise_date = DateFormat.format("hh:mm", cal).toString();
                            tvsunrise.setText(sunrise_date + " AM");

                            long sunset_lg = Long.parseLong(weatherResponseObj.getSunsetDetails().getSunset());
                            //sunset date
                            Calendar cal1 = Calendar.getInstance(Locale.ENGLISH);
                            cal1.setTimeInMillis(sunset_lg * 1000L);
                            String sunset_date = DateFormat.format("hh:mm", cal1).toString();
                            tvsunset.setText(sunset_date + " PM");


                            //image setup
                            ImageView img = (ImageView) findViewById(R.id.imageView);
                            if (weatherResponseObj.getWd().get(0).getMain().equals("Clouds")) {
                                img.setImageResource(R.drawable.clouds);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Thunderstorm")) {
                                img.setImageResource(R.drawable.storm);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Drizzle")) {
                                img.setImageResource(R.drawable.drizzle);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Rain")) {
                                img.setImageResource(R.drawable.rain);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Snow")) {
                                img.setImageResource(R.drawable.snow);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Atmosphere")) {
                                img.setImageResource(R.drawable.atmosphire);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Clear")) {
                                img.setImageResource(R.drawable.clear);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Extreme")) {
                                img.setImageResource(R.drawable.extrem);
                            } else if (weatherResponseObj.getWd().get(0).getMain().equals("Haze")) {
                                img.setImageResource(R.drawable.haze);
                            } else {
                                img.setImageResource(R.drawable.atmosphire);
                            }


                        }

                        @Override
                        public void onFailure(Call<WeatherObject> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Something goes wrong..", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(MainActivity.this, "Enable location service Settings>Location..", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int randcolor = mycolors[new Random().nextInt(mycolors.length)];
        cardView.setCardBackgroundColor(randcolor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
