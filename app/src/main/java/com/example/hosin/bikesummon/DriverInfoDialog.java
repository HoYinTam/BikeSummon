package com.example.hosin.bikesummon;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.TextureMapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Hosin on 2016/1/3.
 */
public class DriverInfoDialog extends AlertDialog {

    HttpUtil httpUtil=new HttpUtil();
    private Context context;
    private int  ID;
    private int orderID;
    private int type; //0: from map 1:from order
    static Handler handler;

    private TextView email;
    private TextView username;
    private TextView name;
    private TextView tel;
    private RatingBar rating;
    private RadioButton male;
    private RadioButton female;
    private RadioButton single;
    private RadioButton couple;
    private RadioButton secret;

    private Button ok;
    private Button finish;

    public DriverInfoDialog(Context context,int ID,int type) {
        super(context);
        this.context=context;
        this.ID=ID;
        this.type=type;
    }

    public DriverInfoDialog(Context context,int ID,int orderID,int type) {
        super(context);
        this.context=context;
        this.ID=ID;
        this.type=type;
        this.orderID=orderID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_info_dialog);

        email=(TextView)findViewById(R.id.info_email);
        username=(TextView)findViewById(R.id.info_nickname);
        name=(TextView)findViewById(R.id.info_name);
        tel=(TextView)findViewById(R.id.info_tel);
        rating=(RatingBar)findViewById(R.id.info_rating);
        male=(RadioButton)findViewById(R.id.info_male);
        female=(RadioButton)findViewById(R.id.info_female);
        single=(RadioButton)findViewById(R.id.info_single);
        couple=(RadioButton)findViewById(R.id.info_couple);
        secret=(RadioButton)findViewById(R.id.info_secret);
        ok= (Button) findViewById(R.id.info_ok);
        finish=(Button)findViewById(R.id.info_finish);

        if(type==1){
            finish.setVisibility(View.VISIBLE);
            ok.setText("cancel");
        }else{
            finish.setVisibility(View.GONE);
            ok.setText("ok");
        }


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==6){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        Log.d("driverInfo",res.toString());
                        if(res.getInt("status")==0){
                                email.setText(res.getString("email"));
                                username.setText(res.getString("username"));
                                name.setText(res.getString("name"));
                                tel.setText(res.getString("tel"));
                                rating.setRating((float)res.getDouble("rating"));
                                if(res.getString("sex").equals("M")) {
                                    male.setChecked(true);
                                    female.setChecked(false);
                                }else if(res.getString("sex").equals("F")){
                                    male.setChecked(false);
                                    female.setChecked(true);
                                }else{
                                    male.setChecked(false);
                                    female.setChecked(false);
                                }
                                if(res.getString("relationStatus").equals("S")){
                                    single.setChecked(true);
                                    couple.setChecked(false);
                                    secret.setChecked(false);
                                }else if(res.getString("relationStatus").equals("N")){
                                    couple.setChecked(true);
                                    single.setChecked(false);
                                    secret.setChecked(false);
                                }else{
                                    secret.setChecked(true);
                                    single.setChecked(false);
                                    couple.setChecked(false);
                                }

                        }else{
                            Toast.makeText(context, "opps!Can't get Infomation", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else if(msg.what==5){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        Log.d("json", res.toString());
                        if (res.getInt("status") == 0) {
                                Toast.makeText(context, "order has been finished!", Toast.LENGTH_SHORT);

                            /*View view= LayoutInflater.from(context).inflate(R.layout.fragment_home, null);
                                TextureMapView mapView=(TextureMapView) view.findViewById(R.id.textureMapView);
                                Log.d("mapView",mapView.toString());*/
                            ((CustomerActivity)context).getEn().remove();
                            ((CustomerActivity)context).getSt().remove();

                                DriverInfoDialog.this.dismiss();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverInfoDialog.this.dismiss();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final JSONObject param = new JSONObject();
                    //param.put("destLatitude", geoCodeResult.getLocation().latitude);
                    //param.put("destLongitude", geoCodeResult.getLocation().longitude);
                    param.put("orderID", orderID);
                    param.put("event", 5);
                    param.put("rating", rating.getRating());
                    Log.d("json", param.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result = httpUtil.doPost("/event", param).toString();
                                Log.d("json", result);
                                Message message = new Message();
                                message.what = 5;
                                message.obj = result;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            final JSONObject param = new JSONObject();
            //param.put("destLatitude", geoCodeResult.getLocation().latitude);
            //param.put("destLongitude", geoCodeResult.getLocation().longitude);
            param.put("ID", ID);
            param.put("event",6);
            param.put("type", "driver");
            Log.d("driverInfo", param.toString());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
