package com.example.hosin.bikesummon;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,driverHomeFragment.OnFragmentInteractionListener{

    private Toolbar toolbar;
    private int userID;
    private String curFragment;
    private Fragment isFragment;

    private HttpUtil httpUtil=new HttpUtil();
    private SharedPreferences pref;
    public static Handler handler;
    private JSONArray unAcceptOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_driver);
        toolbar = (Toolbar) findViewById(R.id.driver_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        curFragment="Home";
        isFragment=getFragmentManager().findFragmentById(R.id.driverHomeFragment);

        //Get UserID
        Intent intent = getIntent();
        userID = intent.getIntExtra("userID", 1);


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putString("username",res.getString("username"));
                        editor.putString("email",res.getString("email"));
                        editor.putString("sex",res.getString("sex"));
                        if(!res.getString("name").equals("null")){
                            editor.putString("name",res.getString("name"));
                        }
                        editor.putString("relationStatus", res.getString("relationStatus"));
                        if(!res.getString("tel").equals("null")){
                            editor.putString("tel", res.getString("tel"));
                        }
                        editor.commit();
                        ((TextView)findViewById(R.id.nav_email)).setText(res.getString("email"));
                        ((TextView)findViewById(R.id.nav_username)).setText(res.getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(msg.what==8){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        if(res.getInt("status")==0){
                            toolbar.setTitle("Home");
                            invalidateOptionsMenu();
                            HomeFragment fragment=new HomeFragment();
                            FragmentManager fragmentManager=getFragmentManager();
                            FragmentTransaction transaction=fragmentManager.beginTransaction();
                            transaction.replace(isFragment.getId(),fragment);
                            isFragment=fragment;
                            transaction.addToBackStack(null);
                            transaction.commit();
                            curFragment="Home";
                        }else{
                            Toast.makeText(DriverActivity.this, "opps!update failure!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(msg.what==10){
                    try {
                        JSONObject res = new JSONObject(msg.obj.toString());
                        if(res.getInt("status")==0){
                            Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(DriverActivity.this, "opps!logout failure!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };
        //Get user info and store in sharePeference
        pref=getSharedPreferences("userInfo", MODE_PRIVATE);
        try {
            final JSONObject param=new JSONObject();
            param.put("event", 1);
            param.put("type", "driver");
            param.put("ID", userID);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = httpUtil.doPost("/event", param).toString();
                        Message message = new Message();
                        message.what = 1;
                        message.obj = result;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
            builder.setTitle("Exit");
            builder.setMessage("Do you want to Exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO:LOGOUT
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject param=new JSONObject();
                                param.put("event", 10);
                                param.put("ID", userID);
                                String result = httpUtil.doPost("/event", param).toString();
                                Message message = new Message();
                                message.what = 10;
                                message.obj = result;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        Log.d("toolbar", curFragment);
        if (curFragment.equals("Profile")) {
            getMenuInflater().inflate(R.menu.profile_toobar, menu);
        } else if (curFragment.equals("Home")) {
            if(unAcceptOrders!=null&&unAcceptOrders.length()>0){
                getMenuInflater().inflate(R.menu.driver_redpoint, menu);
            }else {
                getMenuInflater().inflate(R.menu.driver, menu);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog aboutDialog = new AlertDialog.Builder(DriverActivity.this).setTitle("About").setMessage("Engineer is working hard!").setNegativeButton("OK", null).create();
            aboutDialog.show();
            return true;
        } else if (id == R.id.action_finish) {
            //TODO:upload new profile
            if (curFragment.equals("Profile")) {

                View view = isFragment.getView();

                EditText ed_phone = (EditText) view.findViewById(R.id.profile_Phone);
                EditText ed_password = (EditText) view.findViewById(R.id.profile_password);
                EditText ed_nickname = (EditText) view.findViewById(R.id.profile_nickname);
                EditText ed_checkpassword = (EditText) view.findViewById(R.id.profile_checkpassword);

                //obtain the updated values
                String nickname = ed_nickname.getText().toString();
                String phone = ed_phone.getText().toString();
                String password = ed_password.getText().toString();
                String checkpassword = ed_checkpassword.getText().toString();


                boolean cancel = false;
                View focusView = null;

                //check nickname
                if (!TextUtils.isEmpty(nickname)) {

                    Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");
                    Matcher matcher = pattern.matcher(nickname);

                    if (matcher.matches()) {
                    } else if (ed_nickname.length() < 4) {
                        ed_nickname.setError("length must greater than 4");
                        focusView = ed_nickname;
                        cancel = true;
                    } else {
                        ed_nickname.setError("only support letters, numbers & underline !");
                        focusView = ed_nickname;
                        cancel = true;
                    }
                }

                //check phone
                if (!TextUtils.isEmpty(phone)) {

                    String format = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";

                    Pattern pattern = Pattern.compile(format);
                    Matcher matcher = pattern.matcher(phone);

                    if (!matcher.matches()) {
                        ed_phone.setError("invalid mobile number !");
                        focusView = ed_phone;
                        cancel = true;
                    }

                }

                //check password
                if (!TextUtils.isEmpty(password)) {

                    Pattern pattern = Pattern.compile("([a-z]|[0-9]|[A-Z]){8,}");
                    Matcher matcher = pattern.matcher(password);

                    if (password.length() < 7) {
                        ed_password.setError("length must be greater than 7");
                        focusView = ed_password;
                        cancel = true;
                    } else if (!matcher.matches()) {
                        ed_password.setError("must contains number and letters !");
                        focusView = ed_password;
                        cancel = true;
                    }
                }

                //check checkpassword
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(checkpassword)) {
                    if (!checkpassword.equals(password)) {
                        focusView = ed_checkpassword;
                        cancel = true;
                    }
                } else if (!TextUtils.isEmpty(password) && TextUtils.isEmpty(checkpassword)) {
                    ed_checkpassword.setError(getString(R.string.error_field_required));
                    focusView = ed_checkpassword;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    try {
                        final JSONObject param = new JSONObject();
                        param.put("event", 8);
                        param.put("type", "customer");
                        param.put("ID", userID);
                        param.put("username", ((EditText) view.findViewById(R.id.profile_nickname)).getText().toString());
                        param.put("email", ((TextView) view.findViewById(R.id.profile_Email)).getText().toString());
                        param.put("userPassword", ((EditText) view.findViewById(R.id.profile_password)).getText().toString());
                        param.put("name", "");
                        param.put("tel", ((EditText) view.findViewById(R.id.profile_Phone)).getText().toString());
                        if (((RadioGroup) view.findViewById(R.id.profile_sexGroup)).getCheckedRadioButtonId() == R.id.profile_male) {
                            param.put("sex", "M");
                        } else if (((RadioGroup) view.findViewById(R.id.profile_sexGroup)).getCheckedRadioButtonId() == R.id.profile_female) {
                            param.put("sex", "F");
                        } else {
                            param.put("sex", "");
                        }
                        if (((RadioGroup) view.findViewById(R.id.profile_relationGroup)).getCheckedRadioButtonId() == R.id.profile_single) {
                            param.put("relationStatus", "S");
                        } else if (((RadioGroup) view.findViewById(R.id.profile_relationGroup)).getCheckedRadioButtonId() == R.id.profile_couple) {
                            param.put("relationStatus", "N");
                        } else {
                            param.put("relationStatus", "G");
                        }
                        Log.d("json", param.toString());
                        ((TextView) findViewById(R.id.nav_username)).setText(param.getString("username"));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String result = httpUtil.doPost("/event", param).toString();
                                    Log.d("json", result);
                                    Message message = new Message();
                                    message.what = 8;
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
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            //TODO: add profile fragment
            toolbar.setTitle("Profile");
            invalidateOptionsMenu();

            if (!curFragment.equals("Profile")) {
                ProfileFragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(isFragment.getId(), fragment);
                isFragment = fragment;
                transaction.addToBackStack(null);
                transaction.commit();
            }
            curFragment = "Profile";

        } else if (id == R.id.nav_orders) {
            //TODO: history orders
            toolbar.setTitle("My orders");
            curFragment = "orders";
            invalidateOptionsMenu();
        } else if (id == R.id.action_settings) {
            //TODO:settings
            toolbar.setTitle("Settings");
            curFragment = "setting";
            invalidateOptionsMenu();
        } else if (id == R.id.nav_home) {
            toolbar.setTitle("Home");

            invalidateOptionsMenu();
            if (!curFragment.equals("Home")) {
                driverHomeFragment fragment = new driverHomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(isFragment.getId(), fragment);
                isFragment = fragment;
                transaction.addToBackStack(null);
                transaction.commit();
            }
            curFragment = "Home";
        } else if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
            builder.setTitle("Logout");
            builder.setMessage("Do you want to logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO:LOGOUT
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject param=new JSONObject();
                                param.put("event", 10);
                                param.put("ID", userID);
                                String result = httpUtil.doPost("/event", param).toString();
                                Message message = new Message();
                                message.what = 10;
                                message.obj = result;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onGetInfo(JSONArray orders) {
        this.unAcceptOrders=orders;
        Log.d("callback","callback");
        invalidateOptionsMenu();
    }

}
