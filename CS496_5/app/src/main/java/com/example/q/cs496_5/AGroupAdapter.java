package com.example.q.cs496_5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q on 2016-07-26.
 */
public class AGroupAdapter extends BaseAdapter {
    private Context mContext = null;
    public static ArrayList<OneAGroup> mListData = null;

    public AGroupAdapter(Context context){
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
        AGroupViewHolder holder;
        if (convertView == null){
            holder = new AGroupViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.one_agrouplayout,null);

            holder.mTitle = (TextView)convertView.findViewById(R.id.mTitle);
            holder.mHost = (TextView)convertView.findViewById(R.id.mHost);
            holder.mFrom = (TextView)convertView.findViewById(R.id.mFrom);
            holder.mTo = (TextView)convertView.findViewById(R.id.mTo);
            holder.mHavetoPay = (TextView)convertView.findViewById(R.id.mHavetoPay);
            holder.mGroupInfo = (TextView)convertView.findViewById(R.id.mSecret);
            convertView.setTag(holder);
        }else {
            holder = (AGroupViewHolder) convertView.getTag();
        }
        OneAGroup mData = mListData.get(i);

        holder.mTitle.setText(mData.mTitle);
        holder.mHost.setText(mData.mHost);
        holder.mFrom.setText(mData.mFrom);
        holder.mTo.setText(mData.mTo);
        holder.mHavetoPay.setText(String.valueOf((int)mData.mHaveToPay));
        holder.mGroupInfo.setText(mData.mGroupinfo);

        return convertView;
    }

    public static void addItem(String title, String host, String from, String to, Integer price, Integer n,String groupinfo){
        //assume that photo is url

        OneAGroup addInfo;
        addInfo = new OneAGroup();

        addInfo.mTitle = title;
        addInfo.mHost = host;
        addInfo.mFrom = from;
        addInfo.mTo = to;
        addInfo.mHaveToPay = (double)price / (double)n ;
        addInfo.mN = n;
        addInfo.mPrice = price;
        addInfo.mGroupinfo = groupinfo;

        mListData.add(addInfo);

    }
}
