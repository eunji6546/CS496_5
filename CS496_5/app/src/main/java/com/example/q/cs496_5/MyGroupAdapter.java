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
public class MyGroupAdapter extends BaseAdapter {
    private Context mContext = null;
    public static ArrayList<OneMyGroup> mListData = null;

    public MyGroupAdapter(Context context){
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
        MyGroupViewHolder holder;
        if (convertView == null){
            holder = new MyGroupViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.one_agrouplayout,null);

            holder.mTitle = (TextView)convertView.findViewById(R.id.mTitle);
            holder.mPrice = (TextView)convertView.findViewById(R.id.mHost);
            holder.mFrom = (TextView)convertView.findViewById(R.id.mFrom);
            holder.mTo = (TextView)convertView.findViewById(R.id.mTo);
            holder.mRest = (TextView)convertView.findViewById(R.id.mHavetoPay);
            holder.mGroupInfo = (TextView)convertView.findViewById(R.id.mSecret);
            convertView.setTag(holder);
        }else {
            holder = (MyGroupViewHolder) convertView.getTag();
        }
        OneMyGroup mData = mListData.get(i);

        holder.mTitle.setText(mData.mTitle);
        holder.mPrice.setText(mData.mPrice);
        holder.mFrom.setText(mData.mFrom);
        holder.mTo.setText(mData.mTo);
        holder.mRest.setText(String.valueOf(mData.mRest));
        holder.mGroupInfo.setText(mData.mGroupinfo);

        return convertView;
    }
    public static void addItem(String title,Integer restPeople, String from, String to, Integer price, Integer n,String groupinfo){
        //assume that photo is url

        OneMyGroup addInfo;
        addInfo = new OneMyGroup();

        addInfo.mTitle = title;
        addInfo.mPrice = price;
        addInfo.mFrom = from;
        addInfo.mTo = to;
        addInfo.mRest = (double)price / (double)n *restPeople;
        addInfo.mN = n;
        addInfo.mGroupinfo = groupinfo;

        mListData.add(addInfo);

    }
}
