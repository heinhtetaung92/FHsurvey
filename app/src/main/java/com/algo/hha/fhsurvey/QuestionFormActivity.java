package com.algo.hha.fhsurvey;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.configuration.AnswerType;
import com.algo.hha.fhsurvey.db.QuestionFormDataORM;
import com.algo.hha.fhsurvey.model.QuestionFormData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/***
 * This is to show the add new answer for the survey
 * This will show questions
 */
public class QuestionFormActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ScrollView question_view;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_form);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

        List<List<QuestionFormData>> datalist = new ArrayList<>();
        question_view = (ScrollView) findViewById(R.id.question_form_scrollview);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String formId = bundle.getString("form_id");
            if(formId != null){
                //getDataFromServer(formId);
                List<List<QuestionFormData>> dl = getDataFromDatabase(formId);
                if(dl.size() > 0){
                    addDataToScrollView(dl);
                }
            }

            String formdescription = bundle.getString("form_description");
            if(formdescription != null){
                toolbarTitle.setText(formdescription);
            }else{
                toolbarTitle.setText("FHSurvey");
            }

        }else{
            toolbarTitle.setText("FHSurvey");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(QuestionFormActivity.this, "CLicked", Toast.LENGTH_SHORT).show();

    }

    private List<List<QuestionFormData>> getDataFromDatabase(String form_id){

        List<QuestionFormData> dbdatalist = QuestionFormDataORM.getQuestionFormDatalist(QuestionFormActivity.this, form_id);

        if(dbdatalist == null)
            return null;

        List<List<QuestionFormData>> maindatalist = new ArrayList<>();

        String tempQuestionID = null;
        List<QuestionFormData> datalist = null;

        for(int i=0;i<dbdatalist.size();i++) {

            QuestionFormData data = dbdatalist.get(i);

            //check new Answer Group Id or not
            if (tempQuestionID == null) {
                Log.i("Question ID", "Temp is null");
                datalist = new ArrayList<>();
                datalist.add(data);
                tempQuestionID = data.get_QuestionID();
            } else if (tempQuestionID.equals(data.get_QuestionID())) {//same answer group id, add to current list
                Log.i("Question ID", "They are equal");
                datalist.add(data);

                if(i == dbdatalist.size() - 2){
                    List<QuestionFormData> templist = datalist;
                    maindatalist.add(templist);
                }

            } else {//new answer group id, add old list to main data list and create new list to add
                Log.i("Question ID", "They are not equal");
                List<QuestionFormData> templist = datalist;
                maindatalist.add(templist);
                datalist = new ArrayList<>();
                datalist.add(data);
                tempQuestionID = data.get_QuestionID();
            }

        }

        return maindatalist;
    }

    /*private void getDataFromServer(String form_id){
        RetrofitAPI.getInstance().getService().getFormDataByFormID(form_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                List<List<QuestionFormData>> datalist = parseJSONToObject(s);
                addDataToScrollView(datalist);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(QuestionFormActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    /*private List<List<QuestionFormData>> parseJSONToObject(String s){
        try {
            JSONArray arr = new JSONArray(s);
            List<List<QuestionFormData>> maindatalist = new ArrayList<>();
            List<QuestionFormData> datalist = new ArrayList<>();
            String tempQuestionID = null;

            for(int i=0;i<arr.length();i++) {

                QuestionFormData data = new QuestionFormData();
                JSONObject obj = arr.getJSONObject(i);

                if (!obj.isNull("ProjectID")) {
                    data.set_ProjectID(obj.getString("ProjectID"));
                }

                if (!obj.isNull("ProjectName")) {
                    data.set_ProjectName(obj.getString("ProjectName"));
                }

                if (!obj.isNull("FormID")) {
                    data.set_FormID(obj.getString("FormID"));
                }

                if (!obj.isNull("FormDescription")) {
                    data.set_FormDescription(obj.getString("FormDescription"));
                }

                if (!obj.isNull("FormIndex")) {
                    data.set_FormIndex(obj.getString("FormIndex"));
                }

                if (!obj.isNull("QuestionGroupID")) {
                    data.set_QuestionGroupID(obj.getString("QuestionGroupID"));
                }

                if (!obj.isNull("QuestionGroupIndex")) {
                    data.set_QuestionGroupIndex(obj.getString("QuestionGroupIndex"));
                }

                if (!obj.isNull("QuestionGroupDescription")) {
                    data.set_QuestionGroupDescription(obj.getString("QuestionGroupDescription"));
                }

                if (!obj.isNull("QuestionID")) {
                    data.set_QuestionID(obj.getString("QuestionID"));
                }

                if (!obj.isNull("QuestionIndex")) {
                    data.set_QuestionIndex(obj.getString("QuestionIndex"));
                }

                if (!obj.isNull("Condition")) {
                    data.set_Condition(obj.getString("Condition"));
                }

                if (!obj.isNull("QuestionShortCode")) {
                    data.set_QuestionShortCode(obj.getString("QuestionShortCode"));
                }

                if (!obj.isNull("QuestionDescription")) {
                    data.set_QuestionDescription(obj.getString("QuestionDescription"));
                }

                if (!obj.isNull("QuestionInstruction")) {
                    data.set_QuestionInstruction(obj.getString("QuestionInstruction"));
                }

                if (!obj.isNull("AnswerTypeID")) {
                    data.set_AnswerTypeID(obj.getString("AnswerTypeID"));
                }

                if (!obj.isNull("AnswerTypeDescription")) {
                    data.set_AnswerTypeDescription(obj.getString("AnswerTypeDescription"));
                }

                if (!obj.isNull("AnswerID")) {
                    data.set_AnswerID(obj.getString("AnswerID"));
                }

                if (!obj.isNull("AnswerDescription")) {
                    data.set_AnswerDescription(obj.getString("AnswerDescription"));
                }

                if (!obj.isNull("AnswerIndex")) {
                    data.set_AnswerIndex(obj.getString("AnswerIndex"));
                }

                if (!obj.isNull("SkippedTo")) {
                    data.set_SkippedTo(obj.getString("SkippedTo"));
                }

                if (!obj.isNull("AnswerColumnID")) {
                    data.set_AnswerColumnID(obj.getString("AnswerColumnID"));
                }

                if (!obj.isNull("ColumnDescription")) {
                    data.set_ColumnDescription(obj.getString("ColumnDescription"));
                }

                if (!obj.isNull("AnswerColumnIndex")) {
                    data.set_AnswerColumnIndex(obj.getString("AnswerColumnIndex"));
                }


                Log.i("Question ID", tempQuestionID + "=" + data.get_QuestionID());
                //check new Answer Group Id or not
                if(tempQuestionID == null){
                    Log.i("Question ID", "Temp is null");
                    datalist = new ArrayList<>();
                    datalist.add(data);
                    tempQuestionID = data.get_QuestionID();
                }
                else if(tempQuestionID.equals(data.get_QuestionID())){//same answer group id, add to current list
                    Log.i("Question ID", "They are equal");
                    datalist.add(data);
                }
                else{//new answer group id, add old list to main data list and create new list to add
                    Log.i("Question ID", "They are not equal");
                    List<QuestionFormData> templist = datalist;
                    maindatalist.add(templist);
                    datalist = new ArrayList<>();
                    datalist.add(data);
                    tempQuestionID = data.get_QuestionID();
                }

            }
            return maindatalist;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }*/



    private void addDataToScrollView(List<List<QuestionFormData>> dl){
        if(question_view == null) return;

        question_view.removeAllViews();


        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        ScrollView.LayoutParams param = new ScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(param);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for(int k=0;k<dl.size();k++){
            View childView = matchUIwithItemType(k, dl);
            if(childView != null) {
                linearLayout.addView(childView);
                linearLayout.addView(createDividerView());
            }

        }

        //linearLayout.addView(createSaveButton());

        question_view.addView(linearLayout);

    }

    /***
     * create save button in bottom of the list
     * @return
     * "save" button
     */
    private View createSaveButton(){
        Button btnSave = new Button(this);
        btnSave.setText("Save");
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnSave.setLayoutParams(param);
        btnSave.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

        return btnSave;
    }

    private View matchUIwithItemType(int position, List<List<QuestionFormData>> dl){
        List<QuestionFormData> itemlist = dl.get(position);
        if(itemlist.size() <= 0){
            return null;
        }else{
            String answerTypeDesc = itemlist.get(0).get_AnswerTypeDescription();

            if(itemlist.get(0).get_ColumnDescription() != null){
                return createTableWithValue(position, dl);
            }
            else if(answerTypeDesc.equals(AnswerType.SINGLE_CHOICE)){//its type is radio button
                return createSingleChoiceAnswer(position, dl);
            }else if(answerTypeDesc.equals(AnswerType.TEXT)){//its type is Text type
                return createTextInputAnswer(position, dl);
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
    private View createSingleChoiceAnswer(int position, List<List<QuestionFormData>> dl){
        List<QuestionFormData> itemlist = dl.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(16, 16, 16, 16);
        linearLayout.setLayoutParams(param);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //create radio group for all radio buttons
        RadioGroup rd_group = new RadioGroup(this);
        LinearLayout.LayoutParams rd_group_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rd_group_param.setMargins(8, 8, 8, 8);
        rd_group.setLayoutParams(rd_group_param);

        //this loop create radio button per data count
        for(int i=0;i<itemlist.size();i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(itemlist.get(i).get_AnswerDescription());
            rd_group.addView(radioButton);

            radioButton.setFocusable(false);
            radioButton.setEnabled(false);
            //checked first item(default)
            /*if(i == 0)
                radioButton.setChecked(true);*/
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

    private View createTextInputAnswer(final int position, List<List<QuestionFormData>> dl){

        List<QuestionFormData> itemlist = dl.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(16, 16, 16, 16);
        linearLayout.setLayoutParams(param);

        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //create title(Question) for edittext
        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //create edittext as requirement
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        editText.setSingleLine();
        editText.setLayoutParams(editText_param);
        editText.setEms(10);
        editText.setFocusable(false);
        linearLayout.addView(editText);

        return linearLayout;
    }

    private View createDividerView(){
        View view = new View(QuestionFormActivity.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        param.setMargins(0, 16, 0, 16);
        view.setLayoutParams(param);
        view.setBackgroundColor(getResources().getColor(R.color.grey_500));

        return view;
    }

    private View createTableWithValue(final int position, List<List<QuestionFormData>> dl){

        List<QuestionFormData> itemlist = dl.get(position);

        //Main layout we will see as table layout
        LinearLayout mainlayout = new LinearLayout(QuestionFormActivity.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(16, 16, 16, 16);
        mainlayout.setLayoutParams(param);
        mainlayout.setOrientation(LinearLayout.VERTICAL);

        //get column count from list
        int columnCount = getColumnCountFromDataList(itemlist);

        LinearLayout titleLayout = new LinearLayout(QuestionFormActivity.this);
        LinearLayout.LayoutParams titleparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setMinimumHeight(56);
        titleLayout.setLayoutParams(titleparam);
        for(int i=0;i<columnCount+1;i++){

            TextView textView = new TextView(QuestionFormActivity.this);
            LinearLayout.LayoutParams titletextparam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            titletextparam.weight = 1;
            textView.setLayoutParams(titletextparam);
            textView.setPadding( 2, 2, 2, 2);
            if(i == 0){
                textView.setText("");
            }else{
                textView.setText(itemlist.get(i-1).get_ColumnDescription());
            }

            textView.setBackgroundResource(R.drawable.background_tabletextview);
            titleLayout.addView(textView);

        }

        mainlayout.addView(titleLayout);


        for(int i=0;i<itemlist.size();i+=columnCount){

            //create layout for value not title
            LinearLayout valueLayout = new LinearLayout(QuestionFormActivity.this);
            LinearLayout.LayoutParams valuelayoutparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            valueLayout.setOrientation(LinearLayout.HORIZONTAL);
            valueLayout.setMinimumHeight(56);
            valueLayout.setLayoutParams(valuelayoutparam);

            //add textview for answer description that shows in frist column
            TextView textView = new TextView(QuestionFormActivity.this);
            LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            valuetextparam.weight = 1;
            textView.setLayoutParams(valuetextparam);
            textView.setText(itemlist.get(i).get_AnswerDescription());
            textView.setPadding( 2, 2, 2, 2);
            textView.setBackgroundResource(R.drawable.background_tabletextview);
            valueLayout.addView(textView);

            //call loop er column Count and create EditText
            for(int j=i;j<columnCount + i;j++){

                View et_view = getEditTextView();
                if(et_view != null){
                    valueLayout.addView(et_view);
                }

            }

            mainlayout.addView(valueLayout);

        }

        return mainlayout;
    }

    private View getEditTextView(){

        EditText editText = new EditText(QuestionFormActivity.this);
        LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        valuetextparam.weight = 1;
        editText.setLayoutParams(valuetextparam);
        editText.setSingleLine();
        editText.setBackgroundResource(R.drawable.background_tabletextview);

        return editText;
    }

    private int getColumnCountFromDataList(List<QuestionFormData> dl){

        List<String> collist = new ArrayList<>();
        for(int i=0;i<dl.size();i++){
            if(collist.contains(dl.get(i).get_AnswerColumnIndex())){
                return collist.size();
            }else{
                collist.add(dl.get(i).get_AnswerColumnIndex());
            }
        }

        return collist.size();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

}
