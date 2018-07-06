package com.example.matao.weatherforecast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.matao.weatherforecast.lintener.MyLocationListener;

/**
 * 百度地图Activity
 * Created by matao on 2018/7/4.
 */

public class MapActivity extends AppCompatActivity {

    private MapView mMapView = null;

    private BaiduMap mBaiduMap = null;

    private LocationClient mLocationClient = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidu_map);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        //百度地图定位服务
        mLocationClient = new LocationClient(getApplicationContext());
        configLocationClient();
        mLocationClient.registerLocationListener(new MyLocationListener(mBaiduMap));
        mLocationClient.start();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestory时执行map.Ondestory,实现地图生命周期管理
        mMapView.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        if(!MapActivity.isOPen(MapActivity.this)) {
//            Toast.makeText(MapActivity.this,"当前GPS定位未开启，请打开！",Toast.LENGTH_LONG).show();
            alertDialog();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void alertDialog() {
        //Alert Dialog
        AlertDialog close = new AlertDialog.Builder(MapActivity.this)
                .setTitle("警告")
                .setMessage("当前GPS定位未开启，请打开！")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing - it will close on its own
                    }
                })
                .show();

    };

    /**
     * 判断GPS是否开启
     * @param context
     * @return
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }


    private void configLocationClient() {
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);


        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setCoorType("bd09ll");

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(1000);

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);

        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setWifiCacheTimeOut(5*60*1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);
    }



}
