package com.example.matao.weatherforecast;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.example.matao.weatherforecast.entity.TodayWeather;
import com.example.matao.weatherforecast.lintener.MyLocationListener;
import com.example.matao.weatherforecast.util.ExamNetworkUtil;
import com.example.matao.weatherforecast.util.HttpURLUtils;
import com.example.matao.weatherforecast.util.ThreadPoolUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String WEATHER_JSON_URL = "http://wthrcdn.etouch.cn/weather_mini?citykey={0}";

    private static final String WEATHRE_XML_URL = "http://wthrcdn.etouch.cn/WeatherApi?citykey={0}";

    private static final String BEIJING_CITY_CODE = "101010100";

    private ImageView selectCityBtn;

    private ImageView locateCityBtn;


    private TextView cityText,timeText,humidityText,weekText,pm25Text,pmQualityText,
        weeikdayText,tempratureText,weatherStateText,windText,cityNameText;

    //未来
    private TextView weekText1,weekText2,weekText3,tempratureText1,tempratureText2,tempratureText3,
            weatherStateText1,weatherStateText2,weatherStateText3,windText1,windText2,windText3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String resultData = bundle.getString("resultData");
            Log.i(TAG,"主界面返回结果:" + resultData);

            //解析json数据
//            JSONObject result = JSONObject.parseObject(resultData);
//            Integer status = result.getInteger("status");
//            String statusDesc = result.getString("desc");
//            JSONObject data = result.getJSONObject("data");
//            String cityName = data.getString("city");
//            String temprature = data.getString("wendu");
//            JSONArray weekArray = data.getJSONArray("forecast");
//            JSONObject today = weekArray.getJSONObject(0);
            Toast.makeText(MainActivity.this,"返回状态:OK",Toast.LENGTH_SHORT).show();


//            initView(cityName, TimeUtil.getNowTime(),"","","",
//                today.getString("date"),temprature + "摄氏度",today.getString("type"),today.getString("fengxiang"));
            TodayWeather todayWeather = parseXML(resultData);
            initView(todayWeather);
        }
    };


    private void initView(TodayWeather todayWeather) {
        //标题
//        cityNameText = findViewById(R.id.ti);
        //today weather

        cityText.setText(todayWeather.getCity());
        timeText.setText(todayWeather.getUpdateTime());
        humidityText.setText("湿度:" + todayWeather.getShidu());
        pm25Text.setText(todayWeather.getPm25());
        pmQualityText.setText(todayWeather.getQuality());
        weekText.setText(todayWeather.getDate());
        tempratureText.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        weatherStateText.setText(todayWeather.getType());
        windText.setText("风力:" + todayWeather.getFengli());

        //未来三天
        weekText1.setText(todayWeather.getTomorrowDate());
        weekText2.setText(todayWeather.getAfterTomorrowDate());
        weekText3.setText(todayWeather.getAfterThreeDayDate());
        tempratureText1.setText(todayWeather.getTomorrowHigh() + "~" + todayWeather.getTomorrowLow());
        tempratureText2.setText(todayWeather.getAfterTomorrowHigh() + "~" + todayWeather.getAfterTomorrowLow());
        tempratureText3.setText(todayWeather.getAfterThreeDayHigh() + "~" + todayWeather.getAfterThreeDayLow());
        weatherStateText1.setText(todayWeather.getTomorrowType());
        weatherStateText2.setText(todayWeather.getAfterTomorrowType());
        weatherStateText3.setText(todayWeather.getAfterThreeDayType());
        windText1.setText("风力:" +todayWeather.getTomorrowFengli());
        windText2.setText("风力:" +todayWeather.getAfterTomorrowFengli());
        windText3.setText("风力:" +todayWeather.getAfterThreeDayFengli());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView refreshBtn = (ImageView) findViewById(R.id.title_city_update);
        refreshBtn.setOnClickListener(this);

        locateCityBtn = (ImageView) findViewById(R.id.title_city_locate);
        locateCityBtn.setOnClickListener(this);



        //检查网络连接状态
        if(ExamNetworkUtil.getNetState(this) == ExamNetworkUtil.NET_NONE) {
            Log.d(TAG,"网络不通");
            Toast.makeText(MainActivity.this,"网络不通",Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG,"网络OK");
            Toast.makeText(MainActivity.this,"网络OK",Toast.LENGTH_SHORT).show();
        }

        cityText = (TextView) findViewById(R.id.todayinfoA_cityName);//城市名称
        timeText = (TextView) findViewById(R.id.todayinfoA_updateTime);//发布时间
        humidityText = (TextView) findViewById(R.id.todayinfoA_humidity);//空气湿度
        pm25Text = (TextView) findViewById(R.id.todayinfoA_pm25);
        pmQualityText = (TextView) findViewById(R.id.todayinfoA_pm25status);
        weekText = (TextView) findViewById(R.id.todayinfoB_weekday);//周几
        tempratureText = (TextView) findViewById(R.id.todayinfoB_temperature);//温度
        weatherStateText = (TextView) findViewById(R.id.todayinfoB_weatherState);//天气状况
        windText = (TextView) findViewById(R.id.todayinfoB_wind);


        //明天
        weekText1 = (TextView) findViewById(R.id.future_day1_weekday);
        tempratureText1 = (TextView) findViewById(R.id.future_day1_temperature);
        weatherStateText1 = (TextView) findViewById(R.id.future_day1_weatherState);
        windText1 = (TextView) findViewById(R.id.future_day1_wind);

        //后天
        weekText2 = (TextView) findViewById(R.id.future_day2_weekday);
        tempratureText2 = (TextView) findViewById(R.id.future_day2_temperature);
        weatherStateText2 = (TextView) findViewById(R.id.future_day2_weatherState);
        windText2 = (TextView) findViewById(R.id.future_day2_wind);

        //大后天
        weekText3 = (TextView) findViewById(R.id.future_day3_weekday);
        tempratureText3 = (TextView) findViewById(R.id.future_day3_temperature);
        weatherStateText3 = (TextView) findViewById(R.id.future_day3_weatherState);
        windText3 = (TextView) findViewById(R.id.future_day3_wind);

        selectCityBtn = (ImageView) findViewById(R.id.title_city_manager);
        selectCityBtn.setOnClickListener(this);



        requestPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        String updateCityCode = this.getIntent().getStringExtra("cityCode");
        if(updateCityCode == null || "".equals(updateCityCode)) {
            //将当前定位存储下来
            SharedPreferences localSharedPreferences = getSharedPreferences("localCityCodePreference",
                    Activity.MODE_PRIVATE);
            updateCityCode = localSharedPreferences.getString("cityCode","");

            if(!updateCityCode.equals("") && updateCityCode!= null) {
                multipleThreadNetRequest(updateCityCode);
            } else {
                multipleThreadNetRequest(BEIJING_CITY_CODE);
            }
        } else {
            multipleThreadNetRequest(updateCityCode);
            this.getIntent().putExtra("cityCode","");
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_city_update:
                SharedPreferences sharedPreferences = getSharedPreferences("latestCityCodePreference",
                        Activity.MODE_PRIVATE);
                String shareCode = sharedPreferences.getString("cityCode","");
                if(!shareCode.equals("")) {
                    multipleThreadNetRequest(shareCode);
                } else {
                    multipleThreadNetRequest(BEIJING_CITY_CODE);
                }
                break;
            case R.id.title_city_manager:
                Intent selectCityIntent = new Intent();
                selectCityIntent.setClass(MainActivity.this,SelectCityActivity.class);
                startActivity(selectCityIntent);
                break;
            case R.id.title_city_locate:

                //跳转到MapActivity
                Intent mapIntent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(mapIntent);
                break;
            default:
                break;
        }

    }




    private String getWeatherJson(String cityCode) {
        String url = MessageFormat.format(WEATHRE_XML_URL,cityCode);
        Log.i(TAG+"/请求url",url);
        String result = HttpURLUtils.sendGet(url);
        return result;
    }


    /**
     * 使用线程池处理网络请求
     */
    private void multipleThreadNetRequest(final String cityCode) {
       Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String resultData = getWeatherJson(cityCode);
                Message msg = handler.obtainMessage();
                Bundle data = new Bundle();
                data.putString("resultData",resultData);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };
        ThreadPoolUtil.execute(runnable);
    }


    /**
     * 百度地图对运行时权限进行申请获取
     */
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Verify that all required contact permissions have been granted.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Contacts permissions have not been granted.
                Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE
                }, 321);
            }
        }
    }




    private TodayWeather parseXML(String xml) {
        int fengliCount = 0;
        int fengxiangCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        TodayWeather todayWeather = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xml));

            int eventType = xmlPullParser.getEventType();
            Log.d(TAG,"start parse xml");
            while(eventType != xmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //文档开始位置
                    case XmlPullParser.START_DOCUMENT:
                        Log.d("parse","start doc");
                        break;
                    //标签元素开始位置
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if(xmlPullParser.getName().equals("city")) {
                            eventType = xmlPullParser.next();
                            Log.d("city",xmlPullParser.getText());
                            todayWeather.setCity(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            Log.d("updatetime",xmlPullParser.getText());
                            todayWeather.setUpdateTime(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            Log.d("wendu",xmlPullParser.getText());
                            todayWeather.setTemprature(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().equals("fengli")) {
                            eventType = xmlPullParser.next();
                            Log.d("fengli",xmlPullParser.getText());
                            if(fengliCount == 0) {
                                todayWeather.setFengli(xmlPullParser.getText());
                            }else if(fengliCount == 1) {
                                todayWeather.setTomorrowFengli(xmlPullParser.getText());
                            } else if(fengliCount == 2) {
                                todayWeather.setAfterTomorrowFengli(xmlPullParser.getText());
                            } else if(fengliCount == 3) {
                                todayWeather.setAfterThreeDayFengli(xmlPullParser.getText());
                            }

                            fengliCount++;
                        } else if(xmlPullParser.getName().equals("shidu")) {
                            eventType = xmlPullParser.next();
                            Log.d("shidu",xmlPullParser.getText());
                            todayWeather.setShidu(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().equals("fengxiang")) {
                            eventType = xmlPullParser.next();
                            Log.d("fengxiang",xmlPullParser.getText());
                            if(fengxiangCount == 0) {
                                todayWeather.setFengxiang(xmlPullParser.getText());
                            }else if(fengxiangCount == 1) {
                                todayWeather.setTomorrowFengxiang(xmlPullParser.getText());
                            } else if(fengxiangCount == 2) {
                                todayWeather.setAfterTomorrowFengxiang(xmlPullParser.getText());
                            } else if(fengxiangCount == 3) {
                                todayWeather.setAfterThreeDayFengxiang(xmlPullParser.getText());
                            }
                            fengxiangCount++;
                        } else if(xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            Log.d("pm25",xmlPullParser.getText());
                            todayWeather.setPm25(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            Log.d("quality",xmlPullParser.getText());
                            todayWeather.setQuality(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().equals("date")) {
                            eventType = xmlPullParser.next();
                            Log.d("date",xmlPullParser.getText());
                            if(dateCount == 0) {
                                todayWeather.setDate(xmlPullParser.getText());
                            }else if(dateCount == 1) {
                                todayWeather.setTomorrowDate(xmlPullParser.getText());
                            } else if(dateCount == 2) {
                                todayWeather.setAfterTomorrowDate(xmlPullParser.getText());
                            } else if(dateCount == 3) {
                                todayWeather.setAfterThreeDayDate(xmlPullParser.getText());
                            }
                            dateCount++;
                        } else if(xmlPullParser.getName().equals("high")) {
                            eventType = xmlPullParser.next();
                            Log.d("high",xmlPullParser.getText());
                            todayWeather.setHigh(xmlPullParser.getText());
                            if(highCount == 0) {
                                todayWeather.setHigh(xmlPullParser.getText());
                            }else if(highCount == 1) {
                                todayWeather.setTomorrowHigh(xmlPullParser.getText());
                            } else if(highCount == 2) {
                                todayWeather.setAfterTomorrowHigh(xmlPullParser.getText());
                            } else if(highCount == 3) {
                                todayWeather.setAfterThreeDayHigh(xmlPullParser.getText());
                            }
                            highCount++;
                        }else if(xmlPullParser.getName().equals("low")) {
                            eventType = xmlPullParser.next();
                            Log.d("low",xmlPullParser.getText());
                            if(lowCount == 0) {
                                todayWeather.setLow(xmlPullParser.getText());
                            }else if(lowCount == 1) {
                                todayWeather.setTomorrowLow(xmlPullParser.getText());
                            } else if(lowCount == 2) {
                                todayWeather.setAfterTomorrowLow(xmlPullParser.getText());
                            } else if(lowCount == 3) {
                                todayWeather.setAfterThreeDayLow(xmlPullParser.getText());
                            }
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("type")) {
                            eventType = xmlPullParser.next();
                            Log.d("type",xmlPullParser.getText());
                            if(typeCount == 0) {
                                todayWeather.setType(xmlPullParser.getText());
                            }else if(typeCount == 1) {
                                todayWeather.setTomorrowType(xmlPullParser.getText());
                            } else if(typeCount == 2) {
                                todayWeather.setAfterTomorrowType(xmlPullParser.getText());
                            } else if(typeCount == 3) {
                                todayWeather.setAfterThreeDayType(xmlPullParser.getText());
                            }

                            typeCount++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return todayWeather;

    }








}
