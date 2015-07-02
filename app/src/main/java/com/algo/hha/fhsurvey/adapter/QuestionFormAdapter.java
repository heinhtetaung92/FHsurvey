package com.algo.hha.fhsurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.algo.hha.fhsurvey.configuration.AnswerType;
import com.algo.hha.fhsurvey.model.QuestionFormData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heinhtetaung on 7/1/15.
 */
public class QuestionFormAdapter extends BaseAdapter implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private List<List<QuestionFormData>> datalist = new ArrayList<>();
    private Activity mActivity;

    public QuestionFormAdapter(Context context, List<List<QuestionFormData>> dl){
        mActivity = (Activity) context;
        datalist = dl;
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

    public void add(List<List<QuestionFormData>> dl){
        if(dl != null){
            datalist.clear();
            datalist.addAll(dl);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View createView = matchUIwithItemType(position);
        if(createView == null)
            createView = new View(mActivity);

        return createView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        int itemposition = (int) group.getTag();
        List<QuestionFormData> answerlist = datalist.get(itemposition);

        RadioButton rb = (RadioButton) group.getChildAt(checkedId);
        if(rb == null) return;
        Toast.makeText(mActivity, String.valueOf(checkedId) + "     " + String.valueOf(rb.getText()) + "      " + answerlist.get(checkedId), Toast.LENGTH_SHORT).show();
    }

    private View matchUIwithItemType(int position){
        List<QuestionFormData> itemlist = datalist.get(position);
        if(itemlist.size() <= 0){
            return null;
        }else{
            String answerTypeDesc = itemlist.get(0).get_AnswerTypeDescription();
            if(answerTypeDesc.equals(AnswerType.SINGLE_CHOICE)){//its type is radio button
                return createSingleChoiceAnswer(position);
            }else if(answerTypeDesc.equals(AnswerType.TEXT)){//its type is Text type
                return createTextInputAnswer(position);
            }
        }
        return null;
    }

    /***
     * this method create a view with radio button data from server
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */
    private View createSingleChoiceAnswer(int position){
        List<QuestionFormData> itemlist = datalist.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(mActivity);
        //set layout param of abslistview
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(param);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv_answer_title = new TextView(mActivity);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        tv_answer_title.setLayoutParams(title_param);

        tv_answer_title.setText(itemlist.get(0).get_AnswerDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //create radio group for all radio buttons
        RadioGroup rd_group = new RadioGroup(mActivity);
        LinearLayout.LayoutParams rd_group_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rd_group_param.setMargins(8, 8, 8, 8);
        rd_group.setLayoutParams(rd_group_param);

        //this loop create radio button per data count
        for(int i=0;i<itemlist.size();i++){
            RadioButton radioButton = new RadioButton(mActivity);
            radioButton.setText(itemlist.get(i).get_AnswerDescription());
            rd_group.addView(radioButton);

            //checked first item(default)
            if(i == 0)
                radioButton.setChecked(true);
        }
        linearLayout.addView(rd_group);

        return linearLayout;
    }

    /***
     * this method create EditText with title
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */
    private View createTextInputAnswer(int position){

        List<QuestionFormData> itemlist = datalist.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(mActivity);
        //set layout param of abslistview
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(param);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //create title(Question) for edittext
        TextView tv_answer_title = new TextView(mActivity);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        tv_answer_title.setLayoutParams(title_param);

        tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //create edittext as requirement
        EditText editText = new EditText(mActivity);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        editText.setLayoutParams(editText_param);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setSingleLine();
        linearLayout.addView(editText);

        return linearLayout;
    }

}
