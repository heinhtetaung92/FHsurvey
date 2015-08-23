package com.algo.hha.fhsurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algo.hha.fhsurvey.R;
import com.algo.hha.fhsurvey.model.UserData;
import com.algo.hha.fhsurvey.utility.ColorUtil;
import com.algo.hha.fhsurvey.utility.RoundDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by heinhtetaung on 7/12/15.
 */
public class AnswerListAdapter extends BaseAdapter {

    List<UserData> datalist = new ArrayList<>();
    private Activity mActivity;
    public boolean isChecked = false;

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
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            holder = new Holder();
            holder.username = (TextView) convertView.findViewById(R.id.list_item_username_textview);
            holder.timestamp = (TextView) convertView.findViewById(R.id.list_item_timestamp_textview);
            holder.roundicon = (TextView) convertView.findViewById(R.id.list_item_round_icon);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.list_item_check_box);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        UserData data = datalist.get(position);

        holder.roundicon.setBackground(RoundDrawable.createUserDrawable(mActivity, ColorUtil.getRandomColor()));
        if(!TextUtils.isEmpty(data.get_username())) {
            holder.roundicon.setText(data.get_username().substring(0, 1).toUpperCase());
        }

        holder.timestamp.setText(data.get_timestamp());
        holder.username.setText(data.get_username());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(data.get_timestamp()));
        holder.timestamp.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR) + "\n" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(data.get_timestamp()));
        holder.timestamp.setText((new StringBuilder()).append(calendar.get(Calendar.DAY_OF_MONTH)).append("-").append(calendar.get(Calendar.MONTH)).append("-").append(calendar.get(Calendar.YEAR)).append("\n").append(calendar.get(Calendar.HOUR_OF_DAY)).append(":").append(calendar.get(Calendar.MINUTE)).toString());
        if (isChecked)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else
        {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setActionMode(boolean flag)
    {
        isChecked = flag;
    }

    public class Holder{
        public TextView username, timestamp;
        public TextView roundicon;
        public ImageView checkBox;
    }
}
