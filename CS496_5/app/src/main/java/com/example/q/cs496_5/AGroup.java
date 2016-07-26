package com.example.q.cs496_5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class AGroup extends AppCompatActivity {

    TextView vTitle, vDate, vDue, vMembers, vPrice, vAccount,vHavetoPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agroup);
        vTitle = (TextView)findViewById(R.id.mTitle);
        vDate = (TextView)findViewById(R.id.mDate);
        vDue = (TextView)findViewById(R.id.mDue);
        // vMembers
        vPrice = (TextView)findViewById(R.id.mPrice);
        vAccount = (TextView)findViewById(R.id.mBank);
        vHavetoPay = (TextView)findViewById(R.id.mHavetoPay);

        Intent intent = getIntent();
        String temp = intent.getStringExtra("GroupInfo");
        try {
            JSONObject groupinfo = new JSONObject(temp);
            String title, date, due, members,bank, account;
            Integer  price, n, havetopay;
            title = groupinfo.getString("title");
            date = groupinfo.getString("date");
            due = groupinfo.getString("due");
            //members = groupinfo.getString("members");
            price = groupinfo.getInt("price");
            bank = groupinfo.getString("bank");
            account = groupinfo.getString("account");
            n = groupinfo.getInt("n");
            havetopay = price;

            vTitle.setText(title);
            vDate.setText(date);
            vDue.setText(due);
            vPrice.setText(price);
            vAccount.setText(bank + "/" + account);
            vHavetoPay.setText(havetopay);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}