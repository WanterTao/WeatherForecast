package com.example.matao.weatherforecast.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matao on 2018/6/27.
 */

public class HttpURLUtils {

    private static final String TAG = HttpURLUtils.class.getSimpleName();

    /**
     * 发送POST请求
     * @param serverUrl
     * @param httpBody
     * @return
     */
    public static String sendPost(String serverUrl,String httpBody) {
        try {
            //建立连接
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //设置连接属性
            connection.setDoOutput(true);//使用URL连接进行输出
            connection.setDoInput(true);//使用URL进行输入
            connection.setUseCaches(false);//忽略缓存
            connection.setRequestMethod("POST");//设置URL请求方法

            byte[] bodyBytes = httpBody.getBytes();
            connection.setRequestProperty("Content-length","" + httpBody.length());
            connection.setRequestProperty("Content-Type","application/octet-stream");
            connection.setRequestProperty("Connection","Keep-Alive");//保持长连接
            connection.setRequestProperty("Charset","UTF-8");

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            //建立输出流，并写入数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(bodyBytes);
            outputStream.close();

            //获取响应状态
            int responseCode = connection.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode) {
                StringBuffer buffer = new StringBuffer();
                BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                String readline;
                while((readline = responseReader.readLine()) != null) {
                    buffer.append(readline).append("\n");
                }
                responseReader.close();

                Log.d("HttpPOST",buffer.toString());
                return buffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }



    public static String sendGet(String serverUrl) {
        BufferedReader responseReader = null;
        String result = null;
        try{
            //建立连接
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            connection.connect();


            //获取响应状态
            int responseCode = connection.getResponseCode();

            if(HttpURLConnection.HTTP_OK == responseCode) {
                //当正确响应处理数据
                StringBuffer buffer = new StringBuffer();
                String readLine;

                responseReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine);
                }
                result = buffer.toString();

                Log.i(TAG + "/HTTP.GET返回结果",result);
            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(responseReader != null)
                    responseReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }






}
