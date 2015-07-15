package com.algo.hha.fhsurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.algo.hha.fhsurvey.R;
import com.algo.hha.fhsurvey.model.AnswerData;
import com.algo.hha.fhsurvey.model.UserData;
import com.algo.hha.fhsurvey.utility.ColorUtil;
import com.algo.hha.fhsurvey.utility.RoundDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by heinhtetaung on 7/12/15.
 */
public class AnswerListAdapter extends BaseAdapter {

    List<UserData> datalist = new ArrayList<>();
    private Activity mActivity;

    public AnswerListAdapter(Context context, List<UserData> dl){
        this.datalist = dl;
        mActivity = (Activity) context;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_viewanswer, parent, false);

            holder = new Holder();
            holder.username = (TextView) convertView.findViewById(R.id.viewanswerlist_username);
            holder.timestamp = (TextView) convertView.findViewById(R.id.viewanswerlist_timestamp);;
            holder.roundicon = (TextView) convertView.findViewById(R.id.viewanswerlist_roundicon);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        UserData data = datalist.get(position);

        holder.roundicon.setBackground(RoundDrawable.createUserDrawable(mActivity, ColorUtil.getRandomColor()));
        holder.roundicon.setText(data.get_username().substring(0, 1));
        holder.timestamp.setText(data.get_timestamp());
        holder.username.setText(data.get_username());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(data.get_timestamp()));
        holder.timestamp.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR) + "\n" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        return convertView;
    }

    public class Holder{
        public TextView username, timestamp;
        public TextView roundicon;
    }
}
