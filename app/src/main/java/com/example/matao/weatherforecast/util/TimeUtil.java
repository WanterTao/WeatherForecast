package com.example.matao.weatherforecast.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by matao on 2018/7/1.
 */

public class TimeUtil {

    public static ThreadLocal<DateFormat> DATEFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };



    public static String getNowTime() {
        SimpleDateFormat dateFormat = (SimpleDateFormat) DATEFORMAT.get();
        return dateFormat.format(new Date());

    }
}
