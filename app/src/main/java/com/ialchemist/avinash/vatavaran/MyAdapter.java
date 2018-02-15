package com.ialchemist.avinash.vatavaran;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by AVI on 19-09-2017.
 */

public class MyAdapter extends BaseAdapter {

    ArrayList<String> citylist;
    AddCity addCity;


    public MyAdapter(AddCity addCity,ArrayList<String> citylist){
        this.addCity = addCity;
        this.citylist = citylist;
    }

    @Override
    public int getCount() {
        return citylist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(addCity);
        View v = layoutInflater.inflate(R.layout.myadapter,null);
        TextView tv = (TextView)v.findViewById(R.id.tv);
        ImageButton deletebtn = (ImageButton)v.findViewById(R.id.delete);

        tv.setText(citylist.get(position));
      final SQLiteDatabase sqLiteDatabase =addCity.openOrCreateDatabase("mydb",Context.MODE_PRIVATE,null);
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = sqLiteDatabase.delete("citytable","name=?",new String[]{citylist.get(position)});
                sqLiteDatabase.close();
                citylist.remove(position);
                notifyDataSetChanged();
                if (count > 0) {

                    return;                } else {
                    Toast.makeText(v.getContext(), "Failed to delete...", Toast.LENGTH_LONG).show();
                }
            }
        });


        MyAdapter.this.notifyDataSetChanged();
        return v;
    }
}
