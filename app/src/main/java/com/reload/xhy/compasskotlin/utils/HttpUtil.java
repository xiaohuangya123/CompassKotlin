package com.reload.xhy.compasskotlin.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.reload.xhy.compasskotlin.base.MyApplication;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    public static boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager) MyApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null){return false;}
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo !=null ){
            return networkInfo.isAvailable();
        }
        return false;
    }

    public static void sendOkhttpRequest(String url, Callback callback){
        Log.d("Rxjava","222222222222");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    public static String sendOkhttpRequest(String url) throws Exception{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return  response.body().string();
    }


}
