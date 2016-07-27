package com.example.q.cs496_5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* @@@@@@@@@@@@@@@@@@@@@@@@
 In Case I am a Host !!
 */
public class AMyGroup extends AppCompatActivity {
    TextView vTitle, vDate, vDue, vPrice, vAccount,vRest;
    ImageView vPhoto;
    ListView vMembers;
    String [] Members;
    String response , responseData;

    public static String roomnumber;
    public String[] userphonenumbers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amy_group);
        vTitle = (TextView)findViewById(R.id.mTitle);
        vDate = (TextView)findViewById(R.id.mDate);
        vDue = (TextView)findViewById(R.id.mDue);

        vPrice = (TextView)findViewById(R.id.mPrice);
        vAccount = (TextView)findViewById(R.id.mBank);
        vRest = (TextView)findViewById(R.id.mRestReceive);

        vMembers = (ListView)findViewById(R.id.unpaidPeopleListView);
        vPhoto = (ImageView)findViewById(R.id.mImage);



        Intent intent = getIntent();
        String temp = intent.getStringExtra("GroupInfo");
        try {
            JSONObject groupinfo = new JSONObject(temp);
            String title, date, due, bank, account,imgstring;
            JSONArray  members;
            Integer n, price, rest;
            title = groupinfo.getString("title");
            date = groupinfo.getString("date").split("T")[0];
            due = groupinfo.getString("due").split("T")[0];
            members = groupinfo.getJSONArray("member");
            price = Integer.parseInt( groupinfo.getString("price"));
            bank = groupinfo.getString("bank");
            account = groupinfo.getString("account");
            n = Integer.parseInt( groupinfo.getString("n"));
            rest = price/n*members.length();
            roomnumber = groupinfo.getString("roomnumber");

            imgstring = groupinfo.getString("image");
            if (imgstring.equals("NONE")){
                Log.e("A GROUP", "I DON T HAVE IMAGE ");
            } else {
                Bitmap bitmap = decodeToBase64(imgstring);
                vPhoto.setImageBitmap(bitmap);
            }

            vTitle.setText(title);
            vDate.setText(date);
            vDue.setText(due);
            vPrice.setText(price.toString());
            vAccount.setText(bank + "/" + account);
            vRest.setText(((Integer)rest).toString());

            Members = new String[members.length()];
            userphonenumbers = new String[members.length()];

            final ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i<members.length(); i++){
                JSONObject one = (JSONObject)members.get(i);
                String name = one.getString("name");
                userphonenumbers[i] = one.getString("phonenumber");
                Members[i]=name;
                list.add(Members[i]);
            }

            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, list);
            vMembers.setAdapter(adapter);

            vMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        final int position, long id) {
                    // Dialog 로 멤버 입금 확인하여 삭제 하려는가 묻고
                    // positive 일 때 서버에 요청 보냄 삭제하라고

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AMyGroup.this);
                    alertDialogBuilder.setMessage("Remove this person\nDid this person paid?")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //보내기 !!
                                    new RemoveTask().execute( "http://143.248.48.69:8080/api/remove_payperson/"+roomnumber+"/"+userphonenumbers[position],"");
                                    finish();
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.setTitle("REMOVE");
                    alert.show();
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(View view) {
    }

    private class RemoveTask extends AsyncTask<String, Void, String> {

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

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    public static Bitmap decodeToBase64(String input){
        byte[] decodeByte = Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
    }
}
