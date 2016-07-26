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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Tab3Fragment extends Fragment {
    String response , responseData;

    ListView receiveListView;
    MyGroupAdapter myGroupAdapter;

    public Tab3Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Tab3Fragment newInstance(Bundle args ) {
        Tab3Fragment fragment = new Tab3Fragment();
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
            receiveListView.setAdapter(myGroupAdapter);
            ListViewExampleClickListener listViewExampleClickListener = new ListViewExampleClickListener();
            receiveListView.setOnItemClickListener(listViewExampleClickListener);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab3, container, false);
        rootView.findViewById(R.id.createBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gogo();
            }
        });

        receiveListView = (ListView)rootView.findViewById(R.id.recListView);
        new RoomListTask().execute("http://143.248.48.69:8080/api/getroom/"+((MainActivity)MainActivity.mContext).PN,"");

        return rootView;
    }

    private class RoomListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {
            URL murl;
            InputStream is = null;

            myGroupAdapter = new MyGroupAdapter(getActivity());

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
                    JSONArray reclist = jRes.getJSONArray("receive");
                    for( int i=0; i<reclist.length(); i++){
                        JSONObject one = (JSONObject) reclist.get(i);
                        String date, hostname, hostpn, title, account, bank, due, period;
                        JSONArray member;
                        Integer n, price, restPeople;
                        date = one.getString("date");
                        hostname = one.getString("hostname");
                        hostpn = one.getString("hostphonenumber");
                        title = one.getString("title");
                        account = one.getString("account");
                        bank = one.getString("bank");
                        due = one.getString("due");
                        period = one.getString("period");
                        member = one.getJSONArray("member");
                        price = one.getInt("price");
                        n = one.getInt("n");
                        restPeople = member.length();

                        myGroupAdapter.addItem(title,restPeople,date,due,price,n,one.toString());
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

    public void gogo() {
        Intent intent = new Intent(getActivity(),MakeNewGroup.class);
        startActivity(intent);
    }

    public class ListViewExampleClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//Intent 넘길 때 넘겨야 할 그룹 인포 스트링 뽑아내는 작업;
            String str = "";
            Intent intent = new Intent(getActivity(), AMyGroup.class);
            intent.putExtra("GroupInfo",str);
            startActivity(intent);

        }
    }
}
