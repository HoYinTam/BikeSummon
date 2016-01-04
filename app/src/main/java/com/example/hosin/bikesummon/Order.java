package com.example.hosin.bikesummon;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Hosin on 2016/1/4.
 */
public class Order {
    private int orderID;
    private int userID;
    private int driverID;
    private double depLatitude;
    private double depLongitude;
    private double destLatitude;
    private double destLongitude;
    private String finishTime;
    private String destName;
    private HttpUtil httpUtil=new HttpUtil();
    private static Handler handler;

    public Order(int orderID){
        this.orderID=orderID;

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==11) {
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        Log.d("json", res.toString());
                        if(res.getInt("status")==0){
                            userID=res.getInt("userID");
                            depLatitude=res.getDouble("depLatitude");
                            depLongitude=res.getDouble("depLongitude");
                            destLatitude=res.getDouble("destLatitude");
                            destLongitude=res.getDouble("destLongitude");
                            finishTime=res.getString("finishTime");
                            destName=res.getString("destName");
                            //driverID=res.getInt("driverID");
                            Log.d("order",destName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };

        try {
            final JSONObject param = new JSONObject();
            param.put("orderID",orderID);
            param.put("event",11);
            Log.d("json", param.toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = httpUtil.doPost("/event", param).toString();
                        Log.d("json", result);
                        Message message = new Message();
                        message.what = 11;
                        message.obj = result;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public double getDepLatitude() {
        return depLatitude;
    }

    public double getDepLongitude() {
        return depLongitude;
    }

    public double getDestLatitude() {
        return destLatitude;
    }

    public double getDestLongitude() {
        return destLongitude;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public int getUserID() {
        return userID;
    }

    public String getDestName() {
        if(destName==null){
            Log.d("null","null");
        }
        return destName;
    }

    public int getDriverID() {
        return driverID;
    }


}
