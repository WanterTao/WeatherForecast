package com.example.matao.weatherforecast;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.matao.weatherforecast.db.CityDB;
import com.example.matao.weatherforecast.entity.City;
import com.example.matao.weatherforecast.util.ThreadPoolUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matao on 2018/7/2.
 */

public class WeatherApplication extends Application {

    private static final String TAG = WeatherApplication.class.getSimpleName();

    private static Application weatherApp;

    private List<City> cityList;

    private CityDB cityDB;

    @Override
    public void onCreate() {
        super.onCreate();
        weatherApp = this;
        Log.d(TAG, "onCreate");

        cityDB = openCityDB();
        initCityList();
    }

    public static Application getInstance() {
        return weatherApp;
    }

    private boolean prepareCityList() {
        cityList = cityDB.getCityList();
        for(City city : cityList) {
            String cityName = city.getCity();
            Log.d("CityDB",cityName);
        }
        return true;
    }

    private void initCityList() {
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        });
    }


    public CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + CityDB.CITY_DB_NAME;

        Log.d("file path",path);
        File db = new File(path);
        Log.d("db",path);
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getAssets().open("city.db");
            fos = new FileOutputStream(db);
            int len = -1;
            byte[] buffer = new byte[1024];
            while((len = is.read(buffer)) != -1) {
                fos.write(buffer,0,len);
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return new CityDB(this,path);
    }

    public List<City> getCityList() {
        return cityList;
    }

    public Map<String,String> cityNameToCode() {
        if(cityList == null || cityList.isEmpty()) {
            initCityList();
        }
        Map<String,String> cityNameToCodeMap = new HashMap<>();
        City city = null;
        for(int i = 0,size = cityList.size(); i < size; i++) {
            city = cityList.get(i);
            cityNameToCodeMap.put(city.getCity(),city.getNumber());
        }
        return cityNameToCodeMap;
    }


}
