package com.example.q.cs496_5;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends AppCompatActivity {
    EditText Phone, Name, Id, Pw;
    Button button;
    TextView MsgTextView;
    String id, pw, responseData ;

    String URL = "http://143.248.48.69:";
    String PORT = "8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Phone = (EditText) findViewById(R.id.RPhone);
        Name = (EditText) findViewById(R.id.RName);
        Id = (EditText) findViewById(R.id.RId);
        Pw = (EditText) findViewById(R.id.RPw);
        button = (Button) findViewById(R.id.Rbtn);
        MsgTextView = (TextView)findViewById(R.id.RMsg) ;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone, name, id, pw;
                phone = Phone.getText().toString();
                name = Name.getText().toString();
                id = Id.getText().toString();
                pw = Pw.getText().toString();
/*
                {

                    "name" : "박채훈",

                        "userid" : "hi",

                        "password" : "12345",

                        "phonenumber" : "01041843369"

                }*/
                JSONObject jobj = new JSONObject();

                try {
                    jobj.put("name",name);
                    jobj.put("userid",id);
                    jobj.put("password",pw);
                    jobj.put("phonenumber",phone);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (phone == null) {
                    MsgTextView.setText("휴대폰번호를 입력하세요.");
                } else if (name == null) {
                    MsgTextView.setText("이름을 입력하세요.");
                } else if (id == null) {
                    MsgTextView.setText("ID를 입력하세요.");
                } else if (pw == null) {
                    MsgTextView.setText("비밀번호를 입력하세요.");
                } else {
                    MsgTextView.setText("");
                    //서버에 날리기
                    new RegisterTask().execute(URL+PORT+"/api/adduser",jobj.toString());
                }

            }
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(final String... params) {
            java.net.URL murl;
            String response = null;
            Log.e("HttpConnectionThread","I'm in");
            try {
                murl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                // conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.connect();

                conn.getOutputStream();
                OutputStream os =  conn.getOutputStream();

                JSONObject jarray = null;
                try {
                    jarray = new JSONObject(params[1]);
                    os.write(jarray.toString().getBytes("UTF-8"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                os.flush();
                os.close();
                response = conn.getResponseMessage();

            } catch (IOException e) {

            }
            // 레지스터 결과
            try {
                JSONObject jRes = new JSONObject(response);
                if (jRes.has("userid")){
                    //register 성공
                    Toast.makeText(getApplicationContext(), "Now you are registered\n Login Now",Toast.LENGTH_LONG);
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return response;
        }

    }
}
