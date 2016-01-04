package com.example.hosin.bikesummon;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Hosin on 2016/1/4.
 */
public class OrderInfoDialog extends AlertDialog implements HttpUtil.CallBack{
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
        email=(TextView)findViewById(R.id.info_email);
        username=(TextView)findViewById(R.id.info_nickname);
        name=(TextView)findViewById(R.id.info_name);
        tel=(TextView)findViewById(R.id.info_tel);
        destination=(TextView)findViewById(R.id.destination);
        destination.setText(order.getDestName());

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
                            OrderInfoDialog.this.dismiss();
                            //TODO:update listview
                        }else{
                            Toast.makeText(context, "opps!Can't get order", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };

        final JSONObject param = new JSONObject();
        try {
            param.put("ID", order.getUserID());
            param.put("event",6);
            param.put("type", "customer");
            Log.d("json", param.toString());
            httpUtil.doPostAsyn("/event",param,this);
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void onRequestComplete(String result) {
        try {
            JSONObject res = new JSONObject(result);
            Log.d("json", res.toString());
            if (res.getInt("status") == 0) {
                email.setText(res.getString("email"));
                username.setText(res.getString("username"));
                name.setText(res.getString("name"));
                tel.setText(res.getString("tel"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
