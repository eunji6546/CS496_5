package com.example.q.cs496_5;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.zip.Inflater;


public class Tab1Fragment extends Fragment {


    public JSONArray contactList;
    String URL = "http://143.248.48.69:";
    String PORT = "8080";

    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 102;

    ListView friendslistview;
    FriendsAdapter friendsAdapter;


    public Tab1Fragment() {
        // Required empty public constructor
    }

    //I removed static !!!
    // TODO: Rename and change types and number of parameters
    public static Tab1Fragment newInstance(Bundle args) {
        Tab1Fragment fragment = new Tab1Fragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView  = inflater.inflate(R.layout.fragment_tab1, container, false);
        friendslistview = (ListView)rootView.findViewById(R.id.friendlistview);
        rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FriendsTask().execute(URL+PORT+"/api/match_phonenumber",contactList.toString());
            }
        });
        //first 주소록 리스트 가져옴


        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.Data.CONTACT_ID
        };

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor contactCursor = getActivity().getContentResolver().query(uri,projection,null,selectionArgs, sortOrder);
        //Cursor contactCursor = managedQuery(uri,null,null,selectionArgs, sortOrder);

        JSONObject object;


        object = new JSONObject();
        contactList = new JSONArray();
        JSONObject oneContact;

        int permissionContact = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);

        if (permissionContact != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        int permissionContact2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS);

        if (permissionContact2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }


        if (contactCursor.moveToFirst()) {
            do {
                try {
                    oneContact = new JSONObject();
                    oneContact.put("name", contactCursor.getString(1));
                    Log.e("NAME",contactCursor.getString(1));
                    oneContact.put("phonenumber", contactCursor.getString(0));
                  /*  ContentResolver cr = getActivity().getContentResolver();
                    int contactId_idx = contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                    Long contactid = contactCursor.getLong(3);

                   *//* Uri puri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, puri);
                    oneContact.put("photo", puri);
                    contactList.put(oneContact);*/

                    contactList.put(oneContact);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (contactCursor.moveToNext());

        }


        Log.e("##",contactList.toString());
        new FriendsTask().execute(URL+PORT+"/api/match_phonenumber/"+((MainActivity)MainActivity.mContext).PN,contactList.toString());

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            friendslistview.setAdapter(friendsAdapter);
            ListViewExampleClickListener listViewExampleClickListener = new ListViewExampleClickListener();
            friendslistview.setOnItemClickListener(listViewExampleClickListener);
        }
    };


     class FriendsTask extends AsyncTask<String, Void, String> {
        String Res,responseData;

        @Override
        protected String doInBackground(final String... params) {
            java.net.URL murl;
            String response = null;
            Log.e("HttpConnectionThread", "I'm in");
            friendsAdapter = new FriendsAdapter(getActivity());
            try {

                murl = new URL(params[0]);
                Log.e("UU",murl.toString());
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
                OutputStream os = conn.getOutputStream();

                JSONArray jarray ;
                try {
                    jarray = new JSONArray(params[1]);
                    Log.e("Writing",jarray.toString());
                    os.write(jarray.toString().getBytes("UTF-8"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                os.flush();
                os.close();
                response = conn.getResponseMessage();
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
                   JSONArray jRes = new JSONArray(responseData);
                    /////////////////////////////////////
                    ((MyActivity)MyActivity.mContext).ContactList = jRes;

                    ////////////////////////////////////
                    Log.e("RESPON",jRes.toString());

                    for (int i = 0; i < jRes.length(); i++) {
                        JSONObject one = (JSONObject) jRes.get(i);
                        String name, phone, plus, minus;
                        name = one.getString("name");
                        phone = one.getString("phonenumber");
                         plus = one.getString("plus");
                         minus = one.getString("minus");

                        friendsAdapter.addItem(name, phone, plus,minus);
                    }
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {

            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ON","POSTEXECUTE");
           // friendslistview.setAdapter(friendsAdapter);
           /* ListViewExampleClickListener listViewExampleClickListener = new ListViewExampleClickListener();
            friendslistview.setOnItemClickListener(listViewExampleClickListener);*/

        }
    }

    public class ListViewExampleClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}


