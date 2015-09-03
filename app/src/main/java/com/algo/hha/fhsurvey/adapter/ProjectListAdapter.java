package com.algo.hha.fhsurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.algo.hha.fhsurvey.R;
import com.algo.hha.fhsurvey.model.ProjectData;
import com.algo.hha.fhsurvey.utility.ColorUtil;
import com.algo.hha.fhsurvey.utility.RoundDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heinhtetaung on 7/2/15.
 */
public class ProjectListAdapter extends BaseAdapter {

    private List<ProjectData> datalist = new ArrayList<>();
    private Activity mActivity;

    public ProjectListAdapter(Context context, List<ProjectData> dl){
        this.datalist = dl;
        mActivity = (Activity) context;
    }

    public void add(List<ProjectData> dl){
        datalist.clear();
        datalist.addAll(dl);
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.custom_project_list, parent, false);

            holder = new Holder();
            holder.project_name = (TextView) convertView.findViewById(R.id.custom_project_list_name);
            holder.project_description = (TextView) convertView.findViewById(R.id.custom_project_list_description);
            holder.project_expiredate = (TextView) convertView.findViewById(R.id.custom_project_list_expiredate);
            holder.roundicon = (TextView) convertView.findViewById(R.id.projectlist_roundicon);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        ProjectData data = datalist.get(position);

        holder.roundicon.setBackground(RoundDrawable.createUserDrawable(mActivity, ColorUtil.getRandomColor()));
        holder.roundicon.setText(data.get_projectName().substring(0, 1));
        holder.project_name.setText(data.get_projectName());
        holder.project_description.setText(data.get_description());
        holder.project_expiredate.setText(data.get_expireDate().substring(0, 10));

        return convertView;
    }

    public class Holder{
        public TextView project_name, project_description, project_expiredate;
        public TextView roundicon;
    }

}
