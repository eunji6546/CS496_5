package com.example.q.cs496_5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
    public static CallbackManager callbackManager;

    TextView PhoneTextView,MsgTextView;
    EditText IdEditView, PwEditView;
    Button LoginBtn, RegisterBtn;
    String response = null;
    String id, pw, responseData ;

    String URL = "http://143.248.48.69:";
    String PORT = "8080";

    int REQUEST_CODE_READ_SMS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create(); //로그인 응답을 처리할 콜백 관리자 생성
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


        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
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
                        new InputStreamReader( conn.getInputStream() )
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
                       // MsgTextView.setText("Wrong id or password");
                    } else {
                        //MsgTextView.setText("Login Success");
                        Log.e("AA","ASDASDasd");
                        Intent intent = new Intent(MainActivity.this, MyActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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



        /*// Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.q.cs496_5",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

            Log.e("AA","AAAAAAAAAAaa");
        } catch (NoSuchAlgorithmException e) {

            Log.e("AA","AAAAAAAAAAaa");

        }*/

/*
        LoginManager.getInstance().logOut();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_friends"));



        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Login Succeed~!!", Toast.LENGTH_SHORT).show();
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/taggable_friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(final GraphResponse response) {
                                JSONArray ja = null;
                                try {
                                    ja = response.getJSONObject().getJSONArray("data");
                                    new StoreDBTask().execute(ja.toString(),"http://143.248.47.61:8000/insert/fb");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }

            class StoreDBTask extends AsyncTask<String, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(final String... params) {

                    //유저 정보를 서버에 전달


                    *//*Thread thread1 = new Thread(){
                        public void run() {
                            new HttpConnectionThread().doInBackground(params[1],params[0]);
                        }
                    };
                    thread1.start();

                    Thread thread2 = new Thread(){
                        public void run() {
                            try {
                                JSONArray jarray = sendJSONinfo();
                                new HttpConnectionThread().doInBackground("http://143.248.47.61:8000/insert/pb",jarray.toString());
                                //new HttpConnectionThread().doInBackground("params[0]",jarray.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread2.start();*//*



                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    Intent intent = new Intent(MainActivity.this,MyActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "로그인을 취소 하였습니다!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
            }
        });*/





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
