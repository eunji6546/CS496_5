package com.example.q.cs496_5;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class AGroup extends AppCompatActivity {
    String response , responseData;
    TextView vTitle, vDate, vDue, vPrice, vAccount,vHavetoPay,vHost;
    ImageView vPhoto;
    String hostname, hostphonenumber,roomnumber;
    static TextView vSendDatePick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agroup);

        roomnumber = "-1";

        vTitle = (TextView)findViewById(R.id.mTitle);
        vHost = (TextView)findViewById(R.id.mHost);
        vDate = (TextView)findViewById(R.id.mDate);
        vDue = (TextView)findViewById(R.id.mDue);
        // vMembers
        vPrice = (TextView)findViewById(R.id.mPrice);
        vAccount = (TextView)findViewById(R.id.mBank);
        vHavetoPay = (TextView)findViewById(R.id.mHavetoPay);

        vSendDatePick = (TextView)findViewById(R.id.mSendDate);

        vPhoto = (ImageView)findViewById(R.id.mImage);

        findViewById(R.id.pushBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vSendDatePick.getText().toString().equals("Select Paid Date")){
                    // did not select date;
                    Toast.makeText(AGroup.this, "날짜를 입력하세요",Toast.LENGTH_LONG).show();
                }else{
                    // 서버에 알리기 ! host에게 push 를 보내라고
                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("hostname",hostname);
                        sendData.put("hostphonenumber",hostphonenumber);
                        sendData.put("paiddate",vSendDatePick.getText().toString().split("T")[0]);
                        sendData.put("username",((MainActivity)MainActivity.mContext).username);
                        sendData.put("userphonenumber",((MainActivity)MainActivity.mContext).PN);
                        sendData.put("roomnumber",roomnumber);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new IPaidTask().execute("http://143.248.48.69:8080/api/pay_call",sendData.toString());

                }
            }
        });

        Intent intent = getIntent();
        String temp = intent.getStringExtra("GroupInfo");
        Log.e("AGROUP.class",temp);

        try {
            JSONObject groupinfo = new JSONObject(temp);
            String title, date, due, bank, account, imgstring;
            Integer  price, n, havetopay;
            title = groupinfo.getString("title");
            date = groupinfo.getString("date").split("T")[0];
            due = groupinfo.getString("due").split("T")[0];
            hostname = groupinfo.getString("hostname");
            hostphonenumber = groupinfo.getString("hostphonenumber");
            roomnumber = groupinfo.getString("roomnumber");

            price = Integer.parseInt(groupinfo.getString("price"));
            bank = groupinfo.getString("bank");
            account = groupinfo.getString("account");
            n = Integer.parseInt(groupinfo.getString("n"));
            havetopay = (int)((double)price/n);

            imgstring = groupinfo.getString("image");
            if (imgstring.equals("NONE")){
                Log.e("A GROUP", "I DON T HAVE IMAGE ");
            } else {
                Bitmap bitmap = decodeToBase64(imgstring);
                vPhoto.setImageBitmap(bitmap);
            }


            vTitle.setText(title);
            vHost.setText(hostname);
            vDate.setText(date);
            vDue.setText(due);
            vPrice.setText(price.toString());
            vAccount.setText(bank + " / " + account);
            vHavetoPay.setText(havetopay.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            vSendDatePick.setText(String.format("%d-%d-%d",year,month+1,day));
        }
    }

    private class IPaidTask extends AsyncTask<String, Void, String> {

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

    public static Bitmap decodeToBase64(String input){
        byte[] decodeByte = Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
    }




}
