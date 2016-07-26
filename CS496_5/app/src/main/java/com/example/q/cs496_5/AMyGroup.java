package com.example.q.cs496_5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* @@@@@@@@@@@@@@@@@@@@@@@@
 In Case I am a Host !!
 */
public class AMyGroup extends AppCompatActivity {
    TextView vTitle, vDate, vDue, vMembers, vPrice, vAccount,vRest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amy_group);
        vTitle = (TextView)findViewById(R.id.mTitle);
        vDate = (TextView)findViewById(R.id.mDate);
        vDue = (TextView)findViewById(R.id.mDue);
        // vMembers
        vPrice = (TextView)findViewById(R.id.mPrice);
        vAccount = (TextView)findViewById(R.id.mBank);
        vRest = (TextView)findViewById(R.id.mRestReceive);


        Intent intent = getIntent();
        String temp = intent.getStringExtra("GropInfo");
        try {
            JSONObject groupinfo = new JSONObject(temp);
            String title, date, due, bank, account;
            JSONArray  members;
            Integer n, price, rest;
            title = groupinfo.getString("title");
            date = groupinfo.getString("date");
            due = groupinfo.getString("due");
            members = groupinfo.getJSONArray("member");
            price = groupinfo.getInt("price");
            bank = groupinfo.getString("bank");
            account = groupinfo.getString("account");
            n = groupinfo.getInt("n");
            rest = price/n*members.length();

            vTitle.setText(title);
            vDate.setText(date);
            vDue.setText(due);
            vPrice.setText(price);
            vAccount.setText(bank + "/" + account);
            vRest.setText(rest);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
