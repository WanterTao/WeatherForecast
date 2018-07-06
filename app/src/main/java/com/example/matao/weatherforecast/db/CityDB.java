package com.example.matao.weatherforecast.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.matao.weatherforecast.entity.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matao on 2018/7/2.
 */

public class CityDB {

    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";

    private SQLiteDatabase db;

    public CityDB(Context context,String path) {
//        db = context.openOrCreateDatabase(CITY_DB_NAME, Context.MODE_PRIVATE,null);
        db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
    }


    public List<City> getCityList() {

        List<City> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME,null);
        while (cursor.moveToNext()) {
            String province = cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String allPY = cursor.getString(cursor.getColumnIndex("allpy"));
            String allFirstPY = cursor.getString(cursor.getColumnIndex("allfirstpy"));
            String firstPY = cursor.getString(cursor.getColumnIndex("firstpy"));
            City item = new City(province,city,number,allPY,allFirstPY,firstPY);
            list.add(item);
        }

        return list;
    }
}
