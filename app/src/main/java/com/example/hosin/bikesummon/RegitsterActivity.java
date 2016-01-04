package com.example.hosin.bikesummon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ant.liao.GifView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegitsterActivity extends AppCompatActivity {


    private  Boolean flag=true;
    private View mProgressView;
    private View mRegisterView;

    private EditText email;
    private EditText phone;
    private EditText checkPassword;
    private EditText password;
    private EditText nickname;
    private EditText realName;
    private Button register;
    private Button more;
    private RadioGroup whois;
    private RadioGroup sex;
    private RadioGroup relation;
    private Toolbar toolbar;

    private UserRegisterTask mAuthTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitster);
        mRegisterView=findViewById(R.id.registerLayout);

        mProgressView = findViewById(R.id.register_progress);

        toolbar = (Toolbar) findViewById(R.id.basetoolbar);
        toolbar.setTitle("Register");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*mProgressView.setGifImage(R.drawable.loading);
        mProgressView.setGifImageType(GifView.GifImageType.COVER);
        WindowManager wm = this.getWindowManager();
        mProgressView.setShowDimension(wm.getDefaultDisplay().getWidth(),80);*/

        checkPassword=(EditText)findViewById(R.id.registerPassword_check);
        email=(EditText)findViewById(R.id.registerEmail);
        phone=(EditText)findViewById(R.id.registerPhone);
        password=(EditText)findViewById(R.id.registerPassword);
        nickname=(EditText)findViewById(R.id.nickname);
        realName=(EditText)findViewById(R.id.realName);
        sex=(RadioGroup)findViewById(R.id.sexGroup);
        relation=(RadioGroup)findViewById(R.id.relationGroup);

        whois =(RadioGroup) findViewById(R.id.radioGroup);
        whois.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.registerCustom){
                    realName.setVisibility(View.GONE);
                    if(flag)sex.setVisibility(View.GONE);
                }else{
                    realName.setVisibility(View.VISIBLE);
                    sex.setVisibility(View.VISIBLE);
                }
            }
        });


        more=(Button)findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    relation.setVisibility(View.VISIBLE);
                    sex.setVisibility(View.VISIBLE);
                    flag=false;
                }else{
                    relation.setVisibility(View.GONE);
                    sex.setVisibility(View.GONE);
                    flag=true;
                }
            }
        });

        register=(Button)findViewById(R.id.register);
        register.setVisibility(View.GONE);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_toobar, menu);
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
            AlertDialog aboutDialog=new  AlertDialog.Builder(RegitsterActivity.this).setTitle("About").setMessage("Engineer is working hard!").setNegativeButton("OK",null).create();
            aboutDialog.show();
            return true;
        }else if(id==R.id.action_finish){
            //TODO:upload new profile
            attemptRegister();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        email.setError(null);
        password.setError(null);
        nickname.setError(null);
        phone.setError(null);
        checkPassword.setError(null);


        // Store values at the time of the login attempt.
        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();
        String mNickName = nickname.getText().toString();
        String mPhone = phone.getText().toString();
        String mCheckPassword = checkPassword.getText().toString();
        int mSex=sex.getCheckedRadioButtonId();

        boolean cancel = false;
        View focusView = null;
        boolean isFirstRequest=true;

        //Check for a correct password
        if(TextUtils.isEmpty(mCheckPassword)){
            focusView=checkPassword;
            checkPassword.setError(getString(R.string.error_field_required));
            cancel=true;
        }else if (!mCheckPassword.equals(mPassword)){
            checkPassword.setError("password is not correct!");
            focusView=checkPassword;
            cancel=true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            email.setError(getString(R.string.error_field_required));
            if (isFirstRequest){
                focusView = email;
                isFirstRequest=false;
            }
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            email.setError(getString(R.string.error_invalid_email));
            if (isFirstRequest){
                focusView = email;
                isFirstRequest=false;
            }
            cancel = true;
        }


        //Check for a valid phone
        if(TextUtils.isEmpty(mPhone)){
            phone.setError(getString(R.string.error_field_required));
            focusView = phone;
            cancel = true;
        }else if(!isPhoneValid(mPhone)){
            phone.setError("invalid phone number");
            focusView=phone;
            cancel=true;
        }


        //Check for a valid nickname
        if(TextUtils.isEmpty(mNickName)){
            nickname.setError(getString(R.string.error_field_required));
            focusView=nickname;
            cancel=true;
        }else if(!isNicknameValid(mNickName)){
            nickname.setError("invalid nickname");
            focusView=nickname;
            cancel=true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if(whois.getCheckedRadioButtonId()==R.id.registerCustom){
                mAuthTask=new UserRegisterTask(mEmail,mPhone,mNickName,mPassword,true);
            }else{
                mAuthTask=new UserRegisterTask(mEmail,mPhone,mNickName,mPassword,mSex,false);
            }

            mAuthTask.execute((Void) null);
        }
    }

    private boolean isNicknameValid(String sNickName) {
        boolean flag = false;

        Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");
        Matcher matcher = pattern.matcher(sNickName);

        if (!matcher.matches()){
            nickname.setError("only support letters, numbers & underline !");
        }else if (sNickName.length() < 4){
            nickname.setError("length must greater than 4");
        }else{
            flag = true;
        }

        return flag;
    }

    private boolean isPhoneValid(String sPhone) {
        String format = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";

        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(sPhone);

        if(! matcher.matches()){
            phone.setError("invalid mobile number !");
        }

        return matcher.matches();
    }
    private boolean isEmailValid(String sEmail) {
        Boolean flag = false;
        String format = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{2,}[.]\\p{Lower}{2,}";

        if (sEmail.matches(format)){
            flag = true;
        }else{
            email.setError("invalid email address !");
        }

        return flag;
    }

    private boolean isPasswordValid(String sPassword) {
        Boolean flag = false;
        Pattern pattern = Pattern.compile("([a-z]|[0-9]|[A-Z]){8,}");
        Matcher matcher = pattern.matcher(sPassword);

        if (sPassword.length()<7){
            password.setError("length must be greater than 7");
        } else if(!matcher.matches()){
            password.setError("must contains number and lower case letters !");
        }else {
            flag = true;
        }

        return flag;
    }

    public class UserRegisterTask extends AsyncTask<Void,Void,Integer>{

        private Boolean isCustom;
        private String mEmail;
        private String mNickname;
        private String mPassword;
        private String mPhone;
        private int mSex;
        private final String url="http://hellobike.sinaapp.com/register";

        UserRegisterTask(String mEmail,String mPhone,String mNickname,String mPassword,Boolean isCustom){
            this.mEmail = mEmail;
            this.mPhone = mPhone;
            this.mNickname = mNickname;
            this.mPassword = mPassword;
            this.isCustom=isCustom;
        }
        UserRegisterTask(String mEmail,String mPhone,String mNickname,String mPassword,int mSex,Boolean isCustom){
            this.mEmail = mEmail;
            this.mPhone = mPhone;
            this.mNickname = mNickname;
            this.mPassword = mPassword;
            this.isCustom=isCustom;
            this.mSex=mSex;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                // Simulate network access.
                //Thread.sleep(2000);

                //upload data
                JSONObject jsonObject = new JSONObject();
                //jsonObject.put("action","register");
                jsonObject.put("tel", mPhone);
                jsonObject.put("username", mNickname);
                jsonObject.put("email", mEmail);
                //TODO: encrypt the password
                jsonObject.put("password", mPassword);
                if (isCustom) {
                    jsonObject.put("type", "customer");
                } else {
                    jsonObject.put("type", "driver");
                    jsonObject.put("sex",(mSex==R.id.male)?"M":"F");
                }
                Log.d("json", jsonObject.toString());
                //out.writeBytes(jsonObject.toString());
                //Log.d("sina", "out");

                HttpPost post=new HttpPost(url);
                post.addHeader("Content-Type", "application/json");
                HttpEntity entity=null;
                StringEntity se=new StringEntity(jsonObject.toString(), HTTP.UTF_8);
                post.setEntity(se);
                Log.d("sina", "send finished");

                //get data from server
                HttpClient client=new DefaultHttpClient();
                HttpResponse response=client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    Log.d("sina", response.toString());

                    String result = EntityUtils.toString(response.getEntity());
                    Log.d("sina", "Responese:" + result.toString());

                    JSONObject res = new JSONObject(result);
                    if(res.getInt("status")==0){
                        return isCustom? res.getInt("userID"):res.getInt("driverID");
                    }
                    else{
                        Log.e("conn",res.getString("message"));
                        return -1;
                    }
                }else{
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        protected void onPostExecute(final Integer i) {
            if(i>0){
                Intent intent;
                if(isCustom){
                    intent=new Intent(RegitsterActivity.this,CustomerActivity.class);
                }else {
                    intent=new Intent(RegitsterActivity.this,DriverActivity.class);
                }
                intent.putExtra("userID",i);
                startActivity(intent);
            }else{
                    mAuthTask=null;
                    showProgress(false);
                    Toast.makeText(RegitsterActivity.this,"Opps!Register fail",Toast.LENGTH_SHORT).show();
                    password.setText("");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask=null;
            super.onCancelled();
        }
    }
}
