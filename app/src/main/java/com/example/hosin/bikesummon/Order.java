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
    public int orderID;
    public  int userID;
    public  int driverID;
    public  Double depLatitude;
    public  Double depLongitude;
    public Double destLatitude;
    public Double destLongitude;
    public String finishTime;
    public String destName;
    private HttpUtil httpUtil=new HttpUtil();
    public static Handler handler;

    public Order(int orderID){
        this.orderID=orderID;

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==11) {
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        Log.d("json", res.toString());

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
                        JSONObject res=new JSONObject(result);
                        if(res.getInt("status")==0){
                            setUserID(res.getInt("userID"));
                            setDepLatitude(res.getDouble("depLatitude"));
                            setDepLongitude(res.getDouble("depLongitude"));
                            setDestLatitude(res.getDouble("destLatitude"));
                            setDestLongitude(res.getDouble("destLongitude"));
                            setFinishTime(res.getString("finishTime"));
                            setDestName(res.getString("destName"));
                            driverID=res.getInt("driverID");
                            Log.d("drawMap", String.valueOf(res.getDouble("destLatitude")));
                        }
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

    public void setDepLatitude(double depLatitude) {
        Order.this.depLatitude = depLatitude;
    }

    public void setDepLongitude(double depLongitude) {
        Order.this.depLongitude = depLongitude;
    }

    public void setDestLatitude(double destLatitude) {
        Order.this.destLatitude = destLatitude;
    }

    public void setDestLongitude(double destLongitude) {
        Order.this.destLongitude = destLongitude;
    }

    public void setDestName(String destName) {
        Log.d("set",destName);
        Order.this.destName = destName;
    }

    public void setDriverID(int driverID) {
        Order.this.driverID = driverID;
    }

    public void setFinishTime(String finishTime) {
        Order.this.finishTime = finishTime;
    }

    public void setOrderID(int orderID) {
        Order.this.orderID = orderID;
    }

    public void setUserID(int userID) {
        Order.this.userID = userID;
    }

    public double getDepLatitude() {
        while(depLatitude==null);

        return  Order.this.depLatitude.doubleValue();
    }
    public  double getDepLongitude() {
        while(depLongitude==null);

        return  Order.this.depLongitude;
    }
    public  double getDestLatitude() {
        while(destLatitude==null);

        return Order.this.destLatitude;
    }

    public  double getDestLongitude() {
        while(destLongitude==null);
        Log.d("drawMap", String.valueOf(destLongitude));
        return Order.this.destLongitude;
    }



    public String getFinishTime() {
        while (finishTime==null);
        return Order.this.finishTime;
    }

    public  int getUserID() {
        return Order.this.userID;
    }



    public  String getDestName() {
        while (destName==null);
        return Order.this.destName;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getDriverID() {
        return driverID;
    }



}
