package com.example.apple.knowhy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rawght Steven on 8/8/16, 16.
 * Email:rawghtsteven@gmail.com
 */
public class HttpUtil {

    public static String HttpGet(String urls,String method){

        HttpURLConnection connection = null;
        InputStream in = null;
        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL(urls);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode == 200){
                in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while((line = reader.readLine())!=null){
                    response.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                connection.disconnect();
            }
        }
        return response.toString();
    }

    public static InputStream getImageViewInputStream(String net) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(net);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(3000);//设置网络连接超时的时间为3秒
        httpURLConnection.setRequestMethod("GET");        //设置请求方法为GET
        httpURLConnection.setDoInput(true);                //打开输入流
        int responseCode = httpURLConnection.getResponseCode();    // 获取服务器响应值
        if (responseCode == HttpURLConnection.HTTP_OK) {        //正常连接
            inputStream = httpURLConnection.getInputStream();        //获取输入流
        }
        return inputStream;
    }
}
