package com.example.q.cs496_5;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Tab2Fragment extends Fragment {

    //줄거
    String response , responseData;

    ListView payListView;
    AGroupAdapter aGroupAdapter;


    public Tab2Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Tab2Fragment newInstance(Bundle args) {
        Tab2Fragment fragment = new Tab2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            payListView.setAdapter(aGroupAdapter);
            ListViewExampleClickListener listViewExampleClickListener = new ListViewExampleClickListener();
            payListView.setOnItemClickListener(listViewExampleClickListener);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView  = inflater.inflate(R.layout.fragment_tab2, container, false);
        payListView = (ListView)rootView.findViewById(R.id.payListView);


        // 서버에 대화방 요청

        new RoomListTask().execute("http://143.248.48.69:8080/api/getroom/"+((MainActivity)MainActivity.mContext).PN,"");

        return rootView;
    }
    private class RoomListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {
            URL murl;
            InputStream is = null;

            aGroupAdapter = new AGroupAdapter(getActivity());

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

                //initiate strings to hold response data
                String inputLine;
                responseData = "";
                //read the InputStream with the BufferedReader line by line and add each line to responseData
                while ( ( inputLine = in.readLine() ) != null ){
                    responseData += inputLine;
                }
                try {
                    JSONObject jRes = new JSONObject(responseData);
                    Log.e("GOT RES",jRes.toString());
                    JSONArray paylist = jRes.getJSONArray("pay");
                    for( int i=0; i<paylist.length(); i++){
                        JSONObject one = (JSONObject) paylist.get(i);
                        String date, hostname, hostpn, title, account, bank, due, period;
                        JSONArray member;
                        Integer n, price;
                        date = one.getString("date").split("T")[0];
                        hostname = one.getString("hostname");
                        hostpn = one.getString("hostphonenumber");
                        title = one.getString("title");
                        account = one.getString("account");
                        bank = one.getString("bank");
                        due = one.getString("due").split("T")[0];
                       // period = one.getString("period");
                        member = one.getJSONArray("member");
                        price = one.getInt("price");
                        n = one.getInt("n");

                        aGroupAdapter.addItem(title,hostname,date,due,price,n,one.toString());
                    }
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
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

    public class ListViewExampleClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView ginfo = (TextView)view.findViewById(R.id.mSecret);
            String str = ginfo.getText().toString();
            Log.e("ITEMCLICK",str);
            Intent intent = new Intent(getActivity(), AGroup.class);
            intent.putExtra("GroupInfo",str);
            startActivity(intent);


        }
    }
}
