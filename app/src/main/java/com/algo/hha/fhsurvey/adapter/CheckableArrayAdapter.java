// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.algo.hha.fhsurvey.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algo.hha.fhsurvey.R;

import java.util.List;

public class CheckableArrayAdapter extends ArrayAdapter
{
    public static class ViewHolder
    {

        public final ImageView checkBoxImageView;
        public final TextView labelTextView;

        public ViewHolder(View view)
        {
            labelTextView = (TextView)view.findViewById(R.id.csv_listitem_label_text);
            checkBoxImageView = (ImageView)view.findViewById(R.id.csv_listitem_checkbox);
        }
    }

    private boolean mActionMode;
    private Activity mContext;
    private List<String> mDataSet;

    public CheckableArrayAdapter(Activity activity, List<String> list)
    {
        super(activity, 0, list);
        mActionMode = false;
        mContext = activity;
        mDataSet = list;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {

        ViewHolder vh;
        if (view == null)
        {
            view = mContext.getLayoutInflater().inflate(R.layout.custom_csvlistview, null, true);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else
        {
            vh = (ViewHolder)view.getTag();
        }
        vh.labelTextView.setText(mDataSet.get(i));
        if (mActionMode)
        {
            vh.checkBoxImageView.setVisibility(View.VISIBLE);
        } else
        {
            vh.checkBoxImageView.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    public void setActionMode(boolean flag)
    {
        mActionMode = flag;
    }

}
