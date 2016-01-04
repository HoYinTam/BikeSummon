package com.example.hosin.bikesummon;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Hosin on 2015/12/25.
 */
public class OrderDialog extends AlertDialog {
    HttpUtil httpUtil=new HttpUtil();
    Context context;
    String city;
    int ID;
    static Handler handler;
    public OrderDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }
    public OrderDialog(Context context){
        super(context);
        this.context=context;
    }
    public OrderDialog(Context context,String city,int ID){
        super(context);
        this.context=context;
        this.city=city;
        this.ID=ID;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_dialog);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==2){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        if(res.getInt("status")==0){
                            OrderDialog.this.dismiss();
                        }else{
                            Toast.makeText(context, "opps!Can't submit your order", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                super.handleMessage(msg);
            }
        };

        final GeoCoder mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NETWORK_ERROR) {
                    //No result
                    Toast.makeText(context, "opps!Can't find your destination", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject param = new JSONObject();
                        param.put("destLatitude", geoCodeResult.getLocation().latitude);
                        param.put("destLongitude", geoCodeResult.getLocation().longitude);
                        param.put("ID", ID);
                        Log.d("json", param.toString());
                        OrderDialog.this.dismiss();
                        mSearch.destroy();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NETWORK_ERROR) {
                    //No result
                    Toast.makeText(context, "opps!Can't find your destination", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject param = new JSONObject();
                        param.put("destLatitude", reverseGeoCodeResult.getLocation().latitude);
                        param.put("destLongitude", reverseGeoCodeResult.getLocation().longitude);
                        param.put("ID", ID);
                        Log.d("json", param.toString());
                        OrderDialog.this.dismiss();
                        mSearch.destroy();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        ((Button)findViewById(R.id.order_confirm)).setOnClickListener(new View.OnClickListener() {
            //TODO:onclickListener
            @Override
            public void onClick(View v) {
                Log.d("location", city);
                try {
                    final JSONObject param = new JSONObject();
                    //param.put("destLatitude", geoCodeResult.getLocation().latitude);
                    //param.put("destLongitude", geoCodeResult.getLocation().longitude);
                    param.put("city", city);
                    param.put("address", ((EditText) OrderDialog.this.findViewById(R.id.destination)).getText().toString());
                    param.put("ID", ID);
                    param.put("event",2);
                    Log.d("json", param.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result = httpUtil.doPost("/event", param).toString();
                                Log.d("json", result);
                                Message message = new Message();
                                message.what = 2;
                                message.obj = result;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    OrderDialog.this.dismiss();
                    mSearch.destroy();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //mSearch.geocode(new GeoCodeOption().city(city).address(((EditText) OrderDialog.this.findViewById(R.id.destination)).getText().toString()));
            }
        });
        ((Button)findViewById(R.id.order_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDialog.this.dismiss();
                mSearch.destroy();
            }
        });
    }


}
