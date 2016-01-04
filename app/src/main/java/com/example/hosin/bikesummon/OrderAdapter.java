package com.example.hosin.bikesummon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hosin on 2016/1/4.
 */
public class OrderAdapter extends ArrayAdapter<Order> {
    private int resourceID;
    private int type; //0:customer 1:driver

    public OrderAdapter(Context context, int resource, List<Order> objects,int type) {
        super(context, resource, objects);
        resourceID=resource;
        this.type=type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order=getItem(position);
       // Log.d("order", order.getDestName());
        View view= LayoutInflater.from(getContext()).inflate(resourceID, null);
        TextView announcement=(TextView)view.findViewById(R.id.announce);
        /*
        TextView orderID=(TextView)view.findViewById(R.id.orderID);
        TextView userID=(TextView)view.findViewById(R.id.userID);
        TextView depLatitude=(TextView)view.findViewById(R.id.depLatitude);
        TextView depLongitude=(TextView)view.findViewById(R.id.depLongitude);
        TextView destLatitude=(TextView)view.findViewById(R.id.destLatitude);
        TextView destLongitude=(TextView)view.findViewById(R.id.destLongitude);
        TextView finishTime=(TextView)view.findViewById(R.id.finishTime);*/
        if (type==0){
            announcement.setText("Your order has been accpeted");
        }else if(type==1){
            announcement.setText("To "+order.getDestName());
        }
        return view;
    }
}
