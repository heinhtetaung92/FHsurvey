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
import com.algo.hha.fhsurvey.model.ProjectFormData;

import java.util.List;

/**
 * Created by heinhtetaung on 7/2/15.
 */
public class ProjectFormListAdapter extends BaseAdapter {

    private List<ProjectFormData> datalist;
    private Activity mActivity;

    public ProjectFormListAdapter(Context context, List<ProjectFormData> dl){
        datalist = dl;
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

    public void add(List<ProjectFormData> dl){
        if(dl != null) {
            datalist.clear();
            datalist.addAll(dl);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_form_list, parent, false);

            holder = new Holder();
            holder.project_description = (TextView) convertView.findViewById(R.id.custom_form_list_description);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        ProjectFormData data = datalist.get(position);

        holder.project_description.setText(data.get_formDescription());

        return convertView;
    }

    public class Holder{
        public TextView project_description;
    }

}
