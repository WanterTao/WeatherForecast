package com.example.matao.weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matao.weatherforecast.entity.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matao on 2018/7/2.
 */

public class SelectCityActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView backBtn;
    private TextView citytextView;

    private EditText searchEt;
    private ImageView searchBtn;

    private ListView cityListView;

    private WeatherApplication weatherApplication;
    private List<City> cityList;
    private List<String > nameList;

    private String updateCityCode = "-1";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

//        citytextView = (TextView) findViewById(R.id.title_selectcity_name);
//        backBtn = (ImageView) findViewById(R.id.title_selectcity_back);
//        backBtn.setOnClickListener(this);

        searchEt = (EditText) findViewById(R.id.selectcity_search);
        searchBtn = (ImageView) findViewById(R.id.selectcity_search_btn);
        searchBtn.setOnClickListener(this);

        weatherApplication = (WeatherApplication) getApplication();
        cityList = weatherApplication.getCityList();
        nameList = new ArrayList<>();
        String no,number,provinceName,cityName;
        City city = null;
        for (int i = 0 ,length = cityList.size() ;i < length; i++) {
            city = cityList.get(i);
            no = Integer.toString(i + 1);
            number = city.getNumber();
            provinceName = city.getProvince();
            cityName = city.getCity();
            nameList.add("No."+no+":"+number+"-"+provinceName+"-"+cityName);
        }


        cityListView = (ListView) findViewById(R.id.body_citylist);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.city_item,nameList);

        cityListView.setAdapter(stringArrayAdapter);


        //添加ListView的点击事件的动作
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String text = textView.getText().toString();
                String number = text.substring(text.lastIndexOf(":") + 1,text.indexOf("-"));
//                updateCityCode = cityList.get(position).getNumber();
//                Log.d("update city code", updateCityCode);
//                citytextView.setText("当前城市：" + cityList.get(position).getCity());

                //用Shareperference 存储最近一次的citycode
                SharedPreferences sharedPreferences = getSharedPreferences("latestCityCodePreference",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cityCode",number);
                editor.commit();


                Intent mainIntent = new Intent(SelectCityActivity.this,MainActivity.class);
                Log.d("当前选项" ,text + "当前城市编码：" + number);
                mainIntent.putExtra("cityCode",number);
                startActivity(mainIntent);
            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.title_selectcity_back:
//                Intent mainIntent = new Intent(this,MainActivity.class);
//                mainIntent.putExtra("cityCode",updateCityCode);
//                startActivity(mainIntent);
//                break;
            case R.id.selectcity_search_btn:
                String citycode = searchEt.getText().toString();
                String value = null;
                List<String> newNameList = new ArrayList<>();
                for (int i = 0,size = nameList.size(); i < size; i++) {
                    value = nameList.get(i);
                    if(value.contains(citycode)) {
                        newNameList.add(value);
                    }
                }
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this,
                        R.layout.city_item,newNameList);
                cityListView.setAdapter(stringArrayAdapter);
                stringArrayAdapter.notifyDataSetChanged();

                break;
            default:
                break;
        }

    }
}
