package com.example.q.cs496_5;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends FragmentActivity {
    public static CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create(); //로그인 응답을 처리할 콜백 관리자 생성

      /*  // Add code to print out the key hash
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


                    /*Thread thread1 = new Thread(){
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
                    thread2.start();*/



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
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
