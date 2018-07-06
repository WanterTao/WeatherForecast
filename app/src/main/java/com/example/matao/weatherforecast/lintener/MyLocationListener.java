package com.example.matao.weatherforecast.lintener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.matao.weatherforecast.WeatherApplication;

import java.util.Map;

/**
 * Created by matao on 2018/7/4.
 */

public class MyLocationListener extends BDAbstractLocationListener {

    private BaiduMap mBaiduMap;

    private boolean isFirstLoc = true; // 是否首次定位

    public MyLocationListener() {
    }

    public MyLocationListener(BaiduMap mBaiduMap) {
        super();
        this.mBaiduMap = mBaiduMap;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {

        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取地址相关的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        String addr = location.getAddrStr();    //获取详细地址信息
        String country = location.getCountry();    //获取国家
        String province = location.getProvince();    //获取省份
        String city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        String street = location.getStreet();    //获取街道信息

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
        String coorType = location.getCoorType();




        WeatherApplication weatherApplication = (WeatherApplication) WeatherApplication.getInstance();
        Map<String,String> cityNameToCode = weatherApplication.cityNameToCode();

        String localCode = cityNameToCode.get(city.substring(0,city.length() - 1));
        Log.d("MyLocationListener",city + "，城市编码：" + localCode);

        //用Shareperference 存储当前城市的citycode
        SharedPreferences sharedPreferences = weatherApplication.getSharedPreferences("localCityCodePreference",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cityCode",localCode);
        editor.commit();



        //更新到最近一次访问的城市
        SharedPreferences preferences = weatherApplication.getSharedPreferences("latestCityCodePreference",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editorLatest = preferences.edit();
        editorLatest.putString("cityCode",localCode.equals("") ? "101010100" : localCode);
        editorLatest.commit();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(radius)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(latitude)
                .longitude(longitude).build();

        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);

        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(latitude,
                    longitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

    }
}
