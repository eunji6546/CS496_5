package com.example.q.cs496_5;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q on 2016-07-25.
 */
public class FriendsAdapter extends BaseAdapter{
    private Context mContext = null;
    public static ArrayList<OneFriend> mListData = null;

    public FriendsAdapter(Context context){
        super();
        this.mContext = context;
        this.mListData = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int i) {
        return mListData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        FriendViewHolder holder;
        if (convertView == null){
            holder = new FriendViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.one_contact,null);


            holder.mName = (TextView) convertView.findViewById(R.id.mName);
            holder.mPhone = (TextView) convertView.findViewById(R.id.mPhone);
            holder.mPlus = (TextView) convertView.findViewById(R.id.mPlus);
            holder.mMinus = (TextView) convertView.findViewById(R.id.mMinus);
            convertView.setTag(holder);

        }else {
            holder = (FriendViewHolder) convertView.getTag();
        }

        Log.e("SET","ADAPTER");
        OneFriend mData = mListData.get(i);

        holder.mName.setText(mData.mName);
        Log.e("NAME",holder.mName.getText().toString());
        holder.mPhone.setText(mData.mPhone);
        Log.e("NAME",mData.mPhone);
        holder.mPlus.setText(mData.mPlus);
        Log.e("NAME",mData.mPlus);
        holder.mMinus.setText(mData.mMinus);
        Log.e("NAME",mData.mMinus);

        return convertView;
    }
    public static void addItem(String name, String number, String plus, String minus){
        //assume that photo is url

        OneFriend addInfo;
        addInfo = new OneFriend();

        addInfo.mName = name;
        addInfo.mPhone = number;
        addInfo.mPlus = plus;
        addInfo.mMinus = minus;
        mListData.add(addInfo);
    }
}
