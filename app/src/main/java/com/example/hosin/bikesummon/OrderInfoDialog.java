package com.example.hosin.bikesummon;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Hosin on 2016/1/4.
 */
public class OrderInfoDialog extends AlertDialog{
    HttpUtil httpUtil=new HttpUtil();
    private Context context;
    private Order order;
    private int driverID;
    private TextView email;
    private TextView username;
    private TextView name;
    private TextView tel;
    private TextView destination;
    private Button accept;
    private Button cancel;
    static Handler handler;

    Marker st;
    Marker en;

    protected OrderInfoDialog(Context context,Order order,int driverID) {
        super(context);
        this.context=context;
        this.order=order;
        this.driverID=driverID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_info_dialog);
        email=(TextView)findViewById(R.id.order_info_email);
        username=(TextView)findViewById(R.id.order_info_nickname);
        name=(TextView)findViewById(R.id.order_info_name);
        tel=(TextView)findViewById(R.id.order_info_tel);
        destination=(TextView)findViewById(R.id.info_destination);


        accept=(Button)findViewById(R.id.driver_accept);
        cancel=(Button)findViewById(R.id.driver_cancel);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==3){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        Log.d("json", res.toString());
                        if (res.getInt("status") == 0) {

                            if(st!=null) st.remove();
                            if(en!=null) en.remove();

                            TextureMapView mapView=((DriverActivity)context).getMapView();
                            LatLng stLoc =new LatLng(order.getDepLatitude(),order.getDepLongitude());
                            BitmapDescriptor stBitmap= BitmapDescriptorFactory.fromResource(R.mipmap.icon_st);
                            OverlayOptions stOpt=new MarkerOptions().position(stLoc).icon(stBitmap).zIndex(9).draggable(true);
                            st= (Marker) mapView.getMap().addOverlay(stOpt);
                            ((DriverActivity) context).setSt(st);

                            LatLng endLoc =new LatLng(order.getDestLatitude(),order.getDestLongitude());
                            Log.d("drawMap", String.valueOf(order.getDestLatitude()));
                            BitmapDescriptor endBitmap= BitmapDescriptorFactory.fromResource(R.mipmap.icon_en);
                            OverlayOptions endOpt=new MarkerOptions().position(endLoc).icon(endBitmap).zIndex(9).draggable(true);
                            en= (Marker) mapView.getMap().addOverlay(endOpt);
                            ((DriverActivity) context).setEn(en);

                            Toolbar toolbar=((DriverActivity)context).getToolbar();
                            toolbar.setTitle("Serving");
                            ((DriverActivity) context).invalidateOptionsMenu();

                            OrderInfoDialog.this.dismiss();
                        }else{
                            Toast.makeText(context, "opps!Can't get order", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(msg.what==6){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        Log.d("orderInfo", res.toString());
                        if (res.getInt("status") == 0) {
                            Toast.makeText(context,res.getString("username"),Toast.LENGTH_SHORT).show();
                            email.setText(res.getString("email"));
                            username.setText(res.getString("username"));
                            name.setText(res.getString("name"));
                            tel.setText(res.getString("tel"));
                            destination.setText(order.getDestName());
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };

        try {
            final JSONObject param = new JSONObject();
            param.put("ID", order.getUserID());
            param.put("event",6);
            param.put("type", "customer");;
            Log.d("json", param.toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = httpUtil.doPost("/event", param).toString();
                        Log.d("json", result);
                        Message message = new Message();
                        message.what = 6;
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

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final JSONObject param = new JSONObject();
                    //param.put("destLatitude", geoCodeResult.getLocation().latitude);
                    //param.put("destLongitude", geoCodeResult.getLocation().longitude);
                    param.put("ID", driverID);
                    param.put("event", 3);
                    param.put("orderID", order.getOrderID());
                    Log.d("json", param.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result = httpUtil.doPost("/event", param).toString();
                                Log.d("json", result);
                                Message message = new Message();
                                message.what = 3;
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
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderInfoDialog.this.dismiss();
            }
        });

    }

}
