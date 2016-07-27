package com.example.q.cs496_5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends FragmentActivity {

    public static Object mContext;
    public String PN,username;

    TextView PhoneTextView,MsgTextView;
    EditText IdEditView, PwEditView;
    Button LoginBtn, RegisterBtn;
    String response = null;


    String  id, pw, responseData ;

    String URL = "http://143.248.48.69:";
    String PORT = "8080";

    int REQUEST_CODE_READ_SMS = 100;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    int  MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 102;



    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgTextView.setText("Wrong id or password");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mContext = this;
     /* Intent intent = new Intent(MainActivity.this, GCMTest.class);
        startActivity(intent);*/
        PhoneTextView = (TextView) findViewById(R.id.mPhone);
        IdEditView = (EditText) findViewById(R.id.mId);
        PwEditView = (EditText) findViewById(R.id.mPw);
        RegisterBtn = (Button) findViewById(R.id.registerBtn);
        LoginBtn = (Button) findViewById(R.id.loginBtn);
        MsgTextView = (TextView) findViewById(R.id.msgText);

        // request permission

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_READ_SMS); // define this constant yourself
        } else {
            // you have the permission
        }
        int permissionContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (permissionContact != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        int permissionContact2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);

        if (permissionContact2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }


//        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String mPhoneNumber = tMgr.getLine1Number();
        //@@ 이 부분이 휴대폰 번호 가지고 오는 부분 !!
        //PhoneTextView.setText(mPhoneNumber);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = IdEditView.getText().toString();
                pw = PwEditView.getText().toString();

                if (id == null) {
                    MsgTextView.setText("ID를 입력하세요.");
                } else if (pw == null) {
                    MsgTextView.setText("비밀번호를 입력하세요.");
                } else {
                    MsgTextView.setText("");
                    //서버에 날리기
                     new LoginTask().execute(URL+PORT+"/api/login/"+id+"/"+pw);
                }

            }
        });
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
    }
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {

            //params 0 : 유알엘 주소, 1: {id :  pw : }

            //유저 정보를 서버에 전달
            URL murl;
            InputStream is = null;

            Log.e("HttpConnectionThread", "I'm in");
            try {
                try {

                    murl = new URL(params[0]);
                    Log.e("HH",murl.toString());
                    HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    Log.e("HH","AAAAAA");
                    // conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    Log.e("HH","BBBBBBBBB");

                    conn.connect();
                    Log.e("HH","qqqqqqqqqqqqqCC");
                    response = conn.getResponseMessage();

                    Log.e("HH","kkkkkCCC");
                    is = conn.getInputStream();
                    Log.e("HH","CCCCCCCCCC");
//            // Convert the InputStream into a string
//            String contentAsString = readIt(is, len);
//           // Log.e("@@",contentAsString);
//            return contentAsString;
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(is )
                    );
                    Log.e("HH","DDDDDDDDDDDDDDdC");

                    JSONArray jsonResponse = null;
                    //initiate strings to hold response data
                    String inputLine;
                    responseData = "";
                    //read the InputStream with the BufferedReader line by line and add each line to responseData
                    while ( ( inputLine = in.readLine() ) != null ){
                        responseData += inputLine;
                    }
                    try {

                        JSONObject jRes = new JSONObject(responseData);
                        Log.e("RESPON",jRes.toString());
                        String res =jRes.getString("login");
                        Log.e("AAa",res);
                        if (res.equals("Failed!")) {
                            // fail to log in
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        } else {
                            //MsgTextView.setText("Login Success");
                            PN = jRes.getString("phonenumber");
                            username = jRes.getString("username");
                            Log.e("LOGIN OK",PN);
                            Intent intent = new Intent(MainActivity.this, MyActivity.class);

                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } finally {
                    if (is!=null){
                        is.close();
                    }
                }
            } catch (IOException e) {
                Log.e("HH","AAAAAasdfasdfasdfA");
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }





}
