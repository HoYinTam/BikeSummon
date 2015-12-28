package com.example.hosin.bikesummon;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Hosin on 2015/12/25.
 */
public class OrderDialog extends AlertDialog {
    public OrderDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public OrderDialog(Context context){
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_dialog);


        ((Button)findViewById(R.id.order_confirm)).setOnClickListener(new View.OnClickListener() {
            //TODO:onclickListener
            @Override
            public void onClick(View v) {
                OrderDialog.this.dismiss();
            }
        });
        ((Button)findViewById(R.id.order_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDialog.this.dismiss();
            }
        });
    }
}
