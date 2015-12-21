package com.example.hosin.bikesummon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegitsterActivity extends AppCompatActivity {

    private View mProgressView;
    private View mRegisterView;

    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText nickname;
    private Button register;
    private RadioGroup whois;

    private UserRegisterTask mAuthTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitster);
        mRegisterView=findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
        whois =(RadioGroup) findViewById(R.id.radioGroup);
        email=(EditText)findViewById(R.id.registerEmail);
        phone=(EditText)findViewById(R.id.registerPhone);
        password=(EditText)findViewById(R.id.registerPassword);
        nickname=(EditText)findViewById(R.id.nickname);
        register=(Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
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

        // Store values at the time of the login attempt.
        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();
        String mNickName = nickname.getText().toString();
        String mPhone = phone.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
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
                mAuthTask=new UserRegisterTask(mEmail,mPhone,mNickName,mPassword,false);
            }

            mAuthTask.execute((Void) null);
        }
    }

    private boolean isNicknameValid(String mNickName) {
        //TODO: More judgement on nickname
            return mNickName.length() > 4;
    }

    private boolean isPhoneValid(String mPhone) {
        //TODO: More judgement on phone
        return mPhone.length()==11;
    }

    private boolean isEmailValid(String email) {
        //TODO: More judgement on Email
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: More judgement on password
        return password.length() > 4;
    }

    public class UserRegisterTask extends AsyncTask<Void,Void,Integer>{

        private Boolean isCustom;
        private String email;
        private String nickname;
        private String password;
        private String phone;
        private final String url="http://hellobike.sinaapp.com/users"; //TODO: wait for url

        UserRegisterTask(String email,String phone,String nickname,String password,Boolean isCustom){
            this.email=email;
            this.phone=phone;
            this.nickname=nickname;
            this.password=password;
            this.isCustom=isCustom;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                // Simulate network access.
                //Thread.sleep(2000);

                //upload data
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action","register");
                jsonObject.put("phone",phone);
                jsonObject.put("nickname",nickname);
                jsonObject.put("email", email);
                //TODO: encrypt the password
                jsonObject.put("passwd", password);
                if (isCustom) {
                    jsonObject.put("type", "custom");
                } else {
                    jsonObject.put("type", "driver");
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

                    //JSONObject res = new JSONObject(result);

                    Log.d("sina", "Responese:" + result.toString());
                    //return res.getInt("userID");
                }
                return 2;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        protected void onPostExecute(final Integer i) {
            if(i>0){
                if(isCustom){
                    Intent intent=new Intent(RegitsterActivity.this,TestActivity.class);
                    intent.putExtra("userID",i);
                    startActivity(intent);
                }else {

                }

            }else{

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask=null;
            super.onCancelled();
        }
    }
}
