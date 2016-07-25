package com.example.q.cs496_5;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimerTask;

public class MakeNewGroup extends AppCompatActivity {

    String[] listItemsFirstRow = {"item 1", "item 2", "item 3"};
    String[] listPeriodHour = {"3h","6h","9h","12h","1day","2day","3day","4day","5day","6day","7day","1month"};
    String[] FriendList ; //= {"이주희","박정빈"};
    boolean[] CheckList;
    public static TextView DateTextView;
    public static TextView BankTextView;
    public static TextView DueTextView;
    public static TextView PeriodTextView;
    public static TextView MemberTextView;

    public static EditText Title, Price,Account;
    public static Button MakeButton,AddButton;

    public static JSONArray jsonRes ;
    public static JSONArray selectedJsonArray;

    public JSONArray ContactL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_group);
        DateTextView = (TextView)findViewById(R.id.mDate);
        BankTextView = (TextView)findViewById(R.id.bankBtn);
        DueTextView = (TextView)findViewById(R.id.mDue);
        PeriodTextView = (TextView)findViewById(R.id.mPeriod);
        MakeButton = (Button)findViewById(R.id.mkBtn);
        Title = (EditText)findViewById(R.id.mTitle);
        Price = (EditText)findViewById(R.id.editText2);
        Account = (EditText)findViewById(R.id.editText3);
        AddButton = (Button)findViewById(R.id.addBtn);
        MemberTextView = (TextView)findViewById(R.id.members);


        ContactL = ((MyActivity)MyActivity.mContext).ContactList;
        CheckList = new boolean[ContactL.length()];



        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@@@@@@@@@@@@@@
                // 서버 응답은 jsonRes 고 려기서 이름만 뽑은게 FriendList
                //
                //@@@@@@@@@@@@@@

                jsonRes = ContactL;

                FriendList = new String[ContactL.length()];
                for (int j=0; j<jsonRes.length();j++){

                    try {
                        JSONObject one = jsonRes.getJSONObject(j);
                        String name;
                        name = one.getString("name");
                        FriendList[j] = name;
                    } catch (JSONException e) {
                        Log.e("JSONEXXX","IN "+String.valueOf(j));
                        e.printStackTrace();
                    }


                }
                Log.e("FriendList",FriendList.toString());

                for (int i=0; i<FriendList.length;i++){
                    CheckList[i]=false;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(MakeNewGroup.this);
                // builder.setAdapter(new MyAdapter(), null);
                builder.setTitle("Add People").setMultiChoiceItems(FriendList,CheckList, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            CheckList[i]=b;
                        }
                    }
                );
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        selectedJsonArray = new JSONArray();
                            String temp = MemberTextView.getText().toString();
                            String mems = "";
                        for (int i=0; i<CheckList.length;i++){
                            if (CheckList[i]){
                                // if it is checked
                                mems+=FriendList[i];
                                mems+="\n";
                                try {
                                    selectedJsonArray.put(jsonRes.get(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        MemberTextView.setText(mems);
                    }
                } );
                 builder.show();


            }
        });
        MakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( DateTextView.getText()!="Pick Date" && Title.getText()!=null && Price.getText()!=null && Account.getText()!=null && BankTextView.getText()!="Bank" && DueTextView.getText()!="Pick Due Date" && DueTextView.getText()!="Per # Hour"
                        && MemberTextView.getText()!=null){
                    //모든 항목 완성 시
                    JSONObject newGroup = new JSONObject();
                    try {
                        newGroup.put("date",DateTextView.getText());
                        newGroup.put("title",Title.getText());
                        newGroup.put("price",Price.getText());
                        newGroup.put("account",Account.getText());
                        newGroup.put("bank",BankTextView.getText());
                        newGroup.put("due",DueTextView.getText());
                        newGroup.put("period",PeriodTextView.getText());
                        newGroup.put("member",selectedJsonArray);

                       /* new AsyncTask<>(){

                            @Override
                            protected Object doInBackground(Object[] objects) {
                                return null;
                            }
                        }.execute()*/
                        //서버에 날리기
                        new HttpConnectionThread().execute("유알엘 넣기 ",newGroup.toString());

                        // 인텐트 넘어감
                        Intent intent = new Intent(MakeNewGroup.this,AGroup.class);
                        intent.putExtra("GropInfo",newGroup.toString());
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(MakeNewGroup.this,"모두 채우시오",Toast.LENGTH_LONG);
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void showDatePickerDialog2(View v) {
        DialogFragment newFragment = new DatePickerFragment2();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showBankPickerDialog(View v){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       // builder.setAdapter(new MyAdapter(), null);
        builder.setItems(listItemsFirstRow, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BankTextView.setText(listItemsFirstRow[i]);
            }
        });
        builder.setTitle("Title");
        builder.show();
    }

    public void showPeriodPickerDialog(View v){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setAdapter(new MyAdapter(), null);
        builder.setItems(listPeriodHour, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PeriodTextView.setText(listPeriodHour[i]);
            }
        });
        builder.setTitle("Title");
        builder.show();

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
            DateTextView.setText(String.format("%d / %d / %d",year,month+1,day));
        }
    }
    public static class DatePickerFragment2 extends DialogFragment
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
            DueTextView.setText(String.format("%d-%d-%d",year,month+1,day));
        }
    }


}
