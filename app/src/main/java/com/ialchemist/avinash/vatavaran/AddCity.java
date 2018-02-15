/*Name: Avinash Rohidas Gunjal
  Android app developer
  @copyright & reserved content
*/
package com.ialchemist.avinash.vatavaran;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;

import static com.ialchemist.avinash.vatavaran.R.id.Addcity;

public class AddCity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    ListView lview;
    ArrayList<String> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcity);
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            //... Display the dialog message here ...
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.intro);
            TextView skip = (TextView) dialog.findViewById(R.id.skip);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }


        //city_name = (EditText) findViewById(R.id.etcity);
        lview = (ListView) findViewById(R.id.lview);

        sqLiteDatabase = openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists citytable(name varchar(30));");

        //google autocomplete
        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                //Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put("name", String.valueOf(place.getName()));
                long status = sqLiteDatabase.insert("citytable", null, cv);
                if (status != -1) {
                    Toast.makeText(getApplicationContext(), "City Added..", Toast.LENGTH_SHORT).show();
                    display();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add city..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        places.setFilter(typeFilter);
        //end
    }

    public void display() {
        Cursor result = sqLiteDatabase.rawQuery("select *from citytable", null);
        mylist = new ArrayList<>();
        if(result.getCount()<=0){
            result.close();
        }
        else{
            while (result.moveToNext()){
                mylist.add(result.getString(0));
                lview.setAdapter(new MyAdapter(AddCity.this,mylist));
            }
        }
    }


    public void backtohome(View v){
        this.onContentChanged();
        if(mylist.isEmpty()){
            Toast.makeText(this, "No City added", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Intent i = new Intent(AddCity.this,MainActivity.class);
            startActivity(i);
            this.finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        display();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        display();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AddCity.this,MainActivity.class);
        startActivity(i);
    }
    
}
