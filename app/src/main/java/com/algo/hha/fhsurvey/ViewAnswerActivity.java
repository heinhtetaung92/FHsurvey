package com.algo.hha.fhsurvey;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.algo.hha.fhsurvey.configuration.AnswerType;
import com.algo.hha.fhsurvey.db.AnswerDataORM;
import com.algo.hha.fhsurvey.model.AnswerData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;


public class ViewAnswerActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    ScrollView answer_scrollview;
    Toolbar mToolbar;

    List<EditText> editTextList;
    List<RadioButton> radioList;
    List<CheckBox> checkboxList;

    List<List<AnswerData>>  datalist;

    InputFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answer);

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

        editTextList = new ArrayList<>();
        radioList = new ArrayList<>();
        checkboxList = new ArrayList<>();

        datalist = new ArrayList<>();
        answer_scrollview = (ScrollView) findViewById(R.id.viewanswer_scrollview);

        filter = new InputFilter() {
            public CharSequence filter(CharSequence charsequence, int i, int j, Spanned spanned, int k, int l) {
                for (; i < j; i++) {
                    if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*").matcher(String.valueOf(charsequence.charAt(i))).matches()) {
                        return "";
                    }
                }

                return null;
            }
        };

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String timestamp = bundle.getString("timestamp");
            String form_id = bundle.getString("form_id");
            if(timestamp != null && form_id != null){
                //getDataFromServer(formId);
                datalist = getDataFromDatabase(form_id, timestamp);
                if(datalist.size() > 0){
                    addDataToScrollView(datalist);
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



    private List<List<AnswerData>> getDataFromDatabase(String form_id, String timestamp){

        List<AnswerData> dbdatalist = AnswerDataORM.getAnswerDatalist(ViewAnswerActivity.this, form_id, timestamp);

        if(dbdatalist == null)
            return null;

        List<List<AnswerData>> maindatalist = new ArrayList<>();

        String tempQuestionID = null;
        List<AnswerData> datalist = null;

        for(int i=0;i<dbdatalist.size();i++) {

            AnswerData data = dbdatalist.get(i);

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
                    List<AnswerData> templist = datalist;
                    maindatalist.add(templist);
                }

            } else {//new answer group id, add old list to main data list and create new list to add
                Log.i("Question ID", "They are not equal");
                List<AnswerData> templist = datalist;
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
                List<List<AnswerData>> datalist = parseJSONToObject(s);
                addDataToScrollView(datalist);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(QuestionFormActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    /*private List<List<AnswerData>> parseJSONToObject(String s){
        try {
            JSONArray arr = new JSONArray(s);
            List<List<AnswerData>> maindatalist = new ArrayList<>();
            List<AnswerData> datalist = new ArrayList<>();
            String tempQuestionID = null;

            for(int i=0;i<arr.length();i++) {

                AnswerData data = new AnswerData();
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
                    List<AnswerData> templist = datalist;
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



    private void addDataToScrollView(List<List<AnswerData>> dl){
        if(answer_scrollview == null) return;

        answer_scrollview.removeAllViews();


        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        ScrollView.LayoutParams param = new ScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(param);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        String tempQuestionGroupIndex = "-1";
        for(int k=0;k<dl.size();k++){
            if(!tempQuestionGroupIndex.equals(dl.get(k).get(0).get_QuestionGroupIndex())){
                linearLayout.addView(createGroupTitleTextView(dl.get(k).get(0)));
                tempQuestionGroupIndex = dl.get(k).get(0).get_QuestionGroupIndex();
            }
            View childView = matchUIwithItemType(k, dl);
            if(childView != null) {
                linearLayout.addView(childView);
                linearLayout.addView(createDividerView());
            }

        }

        //linearLayout.addView(createSaveButton());

        answer_scrollview.addView(linearLayout);

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

    private View createGroupTitleTextView(AnswerData data){
        //add group title textview to layout
        TextView tv_answer_grouptitle = new TextView(this);
        LinearLayout.LayoutParams grouptitle_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        grouptitle_param.setMargins(16, 16, 16, 16);
        tv_answer_grouptitle.setLayoutParams(grouptitle_param);

        tv_answer_grouptitle.setText(data.get_QuestionGroupDescription());
        tv_answer_grouptitle.setTextSize(18);
        tv_answer_grouptitle.setTextColor(getResources().getColor(android.R.color.white));
        tv_answer_grouptitle.setPadding(16, 16, 16, 16);
        tv_answer_grouptitle.setBackgroundColor(getResources().getColor(R.color.pink_500));
        tv_answer_grouptitle.setTypeface(null, Typeface.BOLD);

        return tv_answer_grouptitle;
    }

    private View matchUIwithItemType(int position, List<List<AnswerData>> dl){
        List<AnswerData> itemlist = dl.get(position);
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
            }else if(answerTypeDesc.equals(AnswerType.DATE)){
                return createDateTextInputAnswer(position, dl);
            }else if (answerTypeDesc.equals(AnswerType.MULTI_CHOICE)){
                return createMultiChoiceAnswer(position, dl);
            }else if(answerTypeDesc.equals(AnswerType.NUMBER)){
                return createNumberInputAnswer(position, dl);
            }
        }
        return null;
    }

    /***
     * this method create EditText with title
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */

    private View createNumberInputAnswer(final int position, List<List<AnswerData>> dl){

        final List<AnswerData> itemlist = dl.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
        linearLayout.setLayoutParams(param);

        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        /*//add group title textview to layout
        TextView tv_answer_grouptitle = new TextView(this);
        LinearLayout.LayoutParams grouptitle_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        grouptitle_param.setMargins(16, 16, 16, 16);
        tv_answer_grouptitle.setLayoutParams(grouptitle_param);

        tv_answer_grouptitle.setText(itemlist.get(0).get_QuestionGroupDescription());
        tv_answer_grouptitle.setTextSize(18);
        tv_answer_grouptitle.setTextColor(getResources().getColor(android.R.color.white));
        tv_answer_grouptitle.setPadding(16, 16, 16, 16);
        tv_answer_grouptitle.setBackgroundColor(getResources().getColor(R.color.pink_500));
        tv_answer_grouptitle.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_grouptitle);*/

        //create title(Question) for edittext
        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        if(itemlist.get(0).get_QuestionShortCode() !=  null) {
            if (TextUtils.isEmpty(itemlist.get(0).get_QuestionShortCode())) {
                tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
            } else {
                tv_answer_title.setText(itemlist.get(0).get_QuestionShortCode() + ". " + itemlist.get(0).get_QuestionDescription());
            }
        }else{
            tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
        }

        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //add instruction view to layout
        if(itemlist.get(0).get_QuestionInstruction() != null) {
            TextView tv_answer_instruction = new TextView(this);
            LinearLayout.LayoutParams instruction_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            instruction_param.setMargins(16, 16, 16, 16);
            tv_answer_instruction.setLayoutParams(title_param);

            tv_answer_instruction.setText(itemlist.get(0).get_QuestionInstruction());
            tv_answer_instruction.setTextSize(18);
            linearLayout.addView(tv_answer_instruction);
        }

        //create edittext as requirement
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        editText.setSingleLine();
        editText.setLayoutParams(editText_param);
        editText.setEms(10);
        editText.setTag(itemlist.get(0));
        editText.setEnabled(false);
        //editText.setFocusable(false);
        editText.setOnFocusChangeListener(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(itemlist.get(0).get_IS_ACTIVE() != null) {
            if (itemlist.get(0).get_IS_ACTIVE().equals("true")){
                editText.setText(itemlist.get(0).get_VALUE());
            }
        }

        if (itemlist.get(0).get_Condition() != null)
        {
            String validatevalue = "";
            String s = itemlist.get(0).get_Condition();
            if (s.contains("Required"))
            {
                String as[] = s.split("/");
                validatevalue = s;
                if (as.length > 1)
                {
                    validatevalue = as[0];
                }
            }
            try
            {
                String[] list = validatevalue.split(":");
                if (list[0].equals("Yes"))
                {
                    editText.setFilters(new InputFilter[] {
                            filter, new android.text.InputFilter.LengthFilter(Integer.parseInt(list[2]))
                    });
                }
            }
            // Misplaced declaration of an exception variable
            catch (Exception ex) { }
        }

        editTextList.add(editText);
        linearLayout.addView(editText);

        return linearLayout;
    }

    /***
     * this method create a view with radio button data from server
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */
    private View createMultiChoiceAnswer(int position, List<List<AnswerData>> dl){
        List<AnswerData> itemlist = dl.get(position);



        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
        linearLayout.setLayoutParams(param);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        /*//add group title textview to layout
        TextView tv_answer_grouptitle = new TextView(this);
        LinearLayout.LayoutParams grouptitle_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        grouptitle_param.setMargins(16, 16, 16, 16);
        tv_answer_grouptitle.setLayoutParams(grouptitle_param);

        tv_answer_grouptitle.setText(itemlist.get(0).get_QuestionGroupDescription());
        tv_answer_grouptitle.setTextSize(18);
        tv_answer_grouptitle.setTextColor(getResources().getColor(android.R.color.white));
        tv_answer_grouptitle.setPadding(16, 16, 16, 16);
        tv_answer_grouptitle.setBackgroundColor(getResources().getColor(R.color.pink_500));
        tv_answer_grouptitle.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_grouptitle);*/

        //add title textview to layout
        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        if(itemlist.get(0).get_QuestionShortCode() !=  null) {
            if (TextUtils.isEmpty(itemlist.get(0).get_QuestionShortCode())) {
                tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
            } else {
                tv_answer_title.setText(itemlist.get(0).get_QuestionShortCode() + ". " + itemlist.get(0).get_QuestionDescription());
            }
        }else{
            tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
        }

        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //add instruction view to layout
        if(itemlist.get(0).get_QuestionInstruction() != null) {
            TextView tv_answer_instruction = new TextView(this);
            LinearLayout.LayoutParams instruction_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            instruction_param.setMargins(16, 16, 16, 16);
            tv_answer_instruction.setLayoutParams(title_param);

            tv_answer_instruction.setText(itemlist.get(0).get_QuestionInstruction());
            tv_answer_instruction.setTextSize(18);
            linearLayout.addView(tv_answer_instruction);
        }

        /*//create radio group for all radio buttons
        RadioGroup rd_group = new RadioGroup(this);
        LinearLayout.LayoutParams rd_group_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rd_group_param.setMargins(16, 16, 16, 16);
        rd_group.setLayoutParams(rd_group_param);*/

        //this loop create radio button per data count
        for(int i=0;i<itemlist.size();i++){
            CheckBox checkbox = new CheckBox(this);
            checkbox.setTag(itemlist.get(i));
            checkbox.setText(itemlist.get(i).get_AnswerDescription());
            //rd_group.addView(radioButton);
            checkbox.setEnabled(false);
            linearLayout.addView(checkbox);

            checkbox.setOnCheckedChangeListener(this);
            //checked first item(default)
            if(itemlist.get(i).get_IS_ACTIVE() != null) {
                if (itemlist.get(i).get_IS_ACTIVE().equals("true")){
                    checkbox.setChecked(true);
                }
            }
            checkboxList.add(checkbox);
        }


        return linearLayout;
    }

    /***
     * this method create a view with radio button data from server
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */
    private View createSingleChoiceAnswer(int position, List<List<AnswerData>> dl){
        List<AnswerData> itemlist = dl.get(position);

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

        tv_answer_title.setText(itemlist.get(0).get_QuestionShortCode() + ". " + itemlist.get(0).get_QuestionDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //add instruction view to layout
        if(itemlist.get(0).get_QuestionInstruction() != null) {
            TextView tv_answer_instruction = new TextView(this);
            LinearLayout.LayoutParams instruction_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            instruction_param.setMargins(16, 16, 16, 16);
            tv_answer_instruction.setLayoutParams(title_param);

            tv_answer_instruction.setText(itemlist.get(0).get_QuestionInstruction());
            tv_answer_instruction.setTextSize(18);
            linearLayout.addView(tv_answer_instruction);
        }

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
            radioButton.setEnabled(false);
            radioButton.setTag(itemlist.get(i));

            //checked first item(default)
            if(itemlist.get(i).get_IS_ACTIVE() != null) {
                if (itemlist.get(i).get_IS_ACTIVE().equals("true")){
                    radioButton.setChecked(true);
                }
            }
            radioList.add(radioButton);
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

    private View createTextInputAnswer(final int position, List<List<AnswerData>> dl){

        List<AnswerData> itemlist = dl.get(position);

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

        tv_answer_title.setText(itemlist.get(0).get_QuestionShortCode() + ". " + itemlist.get(0).get_QuestionDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //add instruction view to layout
        if(itemlist.get(0).get_QuestionInstruction() != null) {
            TextView tv_answer_instruction = new TextView(this);
            LinearLayout.LayoutParams instruction_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            instruction_param.setMargins(16, 16, 16, 16);
            tv_answer_instruction.setLayoutParams(title_param);

            tv_answer_instruction.setText(itemlist.get(0).get_QuestionInstruction());
            tv_answer_instruction.setTextSize(18);
            linearLayout.addView(tv_answer_instruction);
        }

        //create edittext as requirement
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        editText.setSingleLine();
        editText.setLayoutParams(editText_param);
        editText.setEms(10);
        editText.setEnabled(false);
        //editText.setFocusable(false);
        editText.setTag(itemlist.get(0));

        if(itemlist.get(0).get_IS_ACTIVE() != null) {
            if (itemlist.get(0).get_IS_ACTIVE().equals("true")){
                editText.setText(itemlist.get(0).get_VALUE());
            }
        }

        editTextList.add(editText);
        linearLayout.addView(editText);

        return linearLayout;
    }

    /***
     * this method create EditText with title
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */

    private View createDateTextInputAnswer(final int position, List<List<AnswerData>> dl){

        final List<AnswerData> itemlist = dl.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
        linearLayout.setLayoutParams(param);

        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        /*//add group title textview to layout
        TextView tv_answer_grouptitle = new TextView(this);
        LinearLayout.LayoutParams grouptitle_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        grouptitle_param.setMargins(16, 16, 16, 16);
        tv_answer_grouptitle.setLayoutParams(grouptitle_param);

        tv_answer_grouptitle.setText(itemlist.get(0).get_QuestionGroupDescription());
        tv_answer_grouptitle.setTextSize(18);
        tv_answer_grouptitle.setTextColor(getResources().getColor(android.R.color.white));
        tv_answer_grouptitle.setPadding(16, 16, 16, 16);
        tv_answer_grouptitle.setBackgroundColor(getResources().getColor(R.color.pink_500));
        tv_answer_grouptitle.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_grouptitle);*/

        //create title(Question) for edittext
        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        if(itemlist.get(0).get_QuestionShortCode() !=  null) {
            if (TextUtils.isEmpty(itemlist.get(0).get_QuestionShortCode())) {
                tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
            } else {
                tv_answer_title.setText(itemlist.get(0).get_QuestionShortCode() + ". " + itemlist.get(0).get_QuestionDescription());
            }
        }else{
            tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
        }

        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //add instruction view to layout
        if(itemlist.get(0).get_QuestionInstruction() != null) {
            TextView tv_answer_instruction = new TextView(this);
            LinearLayout.LayoutParams instruction_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            instruction_param.setMargins(16, 16, 16, 16);
            tv_answer_instruction.setLayoutParams(title_param);

            tv_answer_instruction.setText(itemlist.get(0).get_QuestionInstruction());
            tv_answer_instruction.setTextSize(18);
            linearLayout.addView(tv_answer_instruction);
        }

        //create edittext as requirement
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(8, 8, 8, 8);
        editText.setSingleLine();
        editText.setLayoutParams(editText_param);
        editText.setEms(10);
        editText.setTag(itemlist.get(0));

        if(itemlist.get(0).get_IS_ACTIVE() != null) {
            if (itemlist.get(0).get_IS_ACTIVE().equals("true")){
                editText.setText(itemlist.get(0).get_VALUE());
            }
        }

        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                showDatePickerDialog();
                editText.setId(R.id.my_edit_text_1);
            }

        });
        editTextList.add(editText);
        linearLayout.addView(editText);

        return linearLayout;
    }

    private void showDatePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
        EditText et = (EditText)findViewById(R.id.my_edit_text_1);
        et.setText((new StringBuilder()).append(i3).append("/").append(i2).append("/").append(i).toString());
        AnswerData answerdata = (AnswerData)et.getTag();
        for(int j=0;j<datalist.size();j++){
            List list = datalist.get(j);
            if(!list.contains(answerdata)){
                continue;
            }

            int k = list.indexOf(answerdata);
            list.remove(k);
            answerdata.set_IS_ACTIVE("true");
            answerdata.set_VALUE(et.getText().toString());
            list.add(k, answerdata);
            datalist.remove(j);
            datalist.add(j, list);

            break;
        }

    }

    private View createDividerView(){
        View view = new View(ViewAnswerActivity.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        param.setMargins(0, 16, 0, 16);
        view.setLayoutParams(param);
        view.setBackgroundColor(getResources().getColor(R.color.grey_500));

        return view;
    }

    private View createTableWithValue(final int position, List<List<AnswerData>> dl){

        List<AnswerData> itemlist = dl.get(position);

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams mainparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainparam.setMargins(16, 16, 16, 16);
        linearLayout.setLayoutParams(mainparam);
        //set orientation
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        //add title textview to layout
        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        tv_answer_title.setText(itemlist.get(0).get_QuestionShortCode() + ". " + itemlist.get(0).get_QuestionDescription());
        tv_answer_title.setTextSize(18);
        tv_answer_title.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(tv_answer_title);

        //add instruction view to layout
        if(itemlist.get(0).get_QuestionInstruction() != null) {
            TextView tv_answer_instruction = new TextView(this);
            LinearLayout.LayoutParams instruction_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            instruction_param.setMargins(16, 16, 16, 16);
            tv_answer_instruction.setLayoutParams(title_param);

            tv_answer_instruction.setText(itemlist.get(0).get_QuestionInstruction());
            tv_answer_instruction.setTextSize(18);
            linearLayout.addView(tv_answer_instruction);
        }


        //Main layout we will see as table layout
        LinearLayout mainlayout = new LinearLayout(ViewAnswerActivity.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(16, 16, 16, 16);
        mainlayout.setLayoutParams(param);
        mainlayout.setOrientation(LinearLayout.VERTICAL);

        //get column count from list
        int columnCount = getColumnCountFromDataList(itemlist);

        LinearLayout titleLayout = new LinearLayout(ViewAnswerActivity.this);
        HorizontalScrollView.LayoutParams titleparam = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setMinimumHeight(56);
        titleLayout.setLayoutParams(titleparam);
        for(int i=0;i<columnCount+1;i++){

            TextView textView = new TextView(ViewAnswerActivity.this);
            LinearLayout.LayoutParams titletextparam;
            textView.setPadding( 8, 8, 8, 8);

            if(i == 0){
                textView.setText("");
                titletextparam = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
            }else{
                textView.setText(itemlist.get(i-1).get_ColumnDescription());
                titletextparam = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            textView.setLayoutParams(titletextparam);
            textView.setBackgroundResource(R.drawable.background_tabletextview);
            titleLayout.addView(textView);

        }

        mainlayout.addView(titleLayout);


        for(int i=0;i<itemlist.size();i+=columnCount){

            //create layout for value not title
            LinearLayout valueLayout = new LinearLayout(ViewAnswerActivity.this);
            LinearLayout.LayoutParams valuelayoutparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            valueLayout.setOrientation(LinearLayout.HORIZONTAL);
            valueLayout.setMinimumHeight(80);
            valueLayout.setLayoutParams(valuelayoutparam);

            //add textview for answer description that shows in frist column
            TextView textView = new TextView(ViewAnswerActivity.this);
            LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(valuetextparam);
            textView.setText(itemlist.get(i).get_AnswerDescription());
            textView.setPadding( 8, 8, 8, 8);
            textView.setBackgroundResource(R.drawable.background_tabletextview);
            valueLayout.addView(textView);

            //call loop er column Count and create EditText
            for(int j=i;j<columnCount + i;j++){

                EditText et_view = getEditTextView(itemlist.get(j));
                if(et_view != null){
                    valueLayout.addView(et_view);
                    editTextList.add(et_view);
                }

            }

            mainlayout.addView(valueLayout);

        }

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(ViewAnswerActivity.this);
        LinearLayout.LayoutParams horizontalScrollViewparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        horizontalScrollViewparam.setMargins(16,16,16,16);
        horizontalScrollView.setLayoutParams(horizontalScrollViewparam);

        horizontalScrollView.addView(mainlayout);
        linearLayout.addView(horizontalScrollView);

        return linearLayout;
    }

    private EditText getEditTextView(AnswerData data){

        EditText editText = new EditText(ViewAnswerActivity.this);
        LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(valuetextparam);
        editText.setSingleLine();
        editText.setEnabled(false);
        //editText.setFocusable(false);
        editText.setBackgroundResource(R.drawable.background_tabletextview);

        if(data.get_IS_ACTIVE() != null) {
            if (data.get_IS_ACTIVE().equals("true")){
                editText.setText(data.get_VALUE());
            }
        }

        return editText;
    }

    private int getColumnCountFromDataList(List<AnswerData> dl){

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 3330, 0, "EDIT").setShowAsAction(2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        if (menuitem.getItemId() == 3330)
        {
            if (menuitem.getTitle().equals("EDIT"))
            {
                setEditMode();
                menuitem.setTitle("DONE");
            } else
            {
                setDoneMode();
                menuitem.setTitle("EDIT");
                saveToDb();
            }
            return true;
        } else
        {
            return super.onOptionsItemSelected(menuitem);
        }
    }

    public void setEditMode(){
        for(EditText et: editTextList){
            et.setFocusable(true);
            et.setEnabled(true);
        }

        for(RadioButton rb : radioList){
            rb.setFocusable(true);
            rb.setEnabled(true);
        }

        for(CheckBox cb : checkboxList){
            cb.setFocusable(true);
            cb.setEnabled(true);
        }
    }

    public void setDoneMode(){
        for(EditText et: editTextList){
            et.setEnabled(false);
            //et.setFocusable(false);
        }

        for(RadioButton rb : radioList){
            rb.setEnabled(false);
            //rb.setFocusable(false);
        }

        for(CheckBox cb : checkboxList){
            cb.setEnabled(false);
            //cb.setFocusable(false);
        }
    }

    public void saveToDb()
    {

        for(EditText editText: editTextList){
            AnswerData answerData = (AnswerData) editText.getTag();

            for(int l=0;l<datalist.size();l++){
                if(datalist.get(l).contains(answerData)){

                    List<AnswerData> list = datalist.get(l);

                    int k = list.indexOf(answerData);
                    list.remove(k);
                    answerData.set_IS_ACTIVE("true");
                    answerData.set_VALUE(editText.getText().toString());
                    list.add(k, answerData);
                    datalist.remove(l);
                    datalist.add(l, list);
                }
            }

        }

        for(RadioButton radioButton: radioList){
            AnswerData answerData = (AnswerData) radioButton.getTag();

            for(int l=0;l<datalist.size();l++){
                if(datalist.get(l).contains(answerData)){

                    List<AnswerData> list = datalist.get(l);


                    Log.i(answerData.get_VALUE() + " " + String.valueOf(radioButton.isChecked()), answerData.get_AnswerDescription());
                    int k = list.indexOf(answerData);
                    list.remove(k);
                    answerData.set_IS_ACTIVE(String.valueOf(radioButton.isChecked()));
                    answerData.set_VALUE(String.valueOf(radioButton.isChecked()));
                    list.add(k, answerData);
                    datalist.remove(l);
                    datalist.add(l, list);
                }
            }
        }

        for(CheckBox checkBox: checkboxList){
            AnswerData answerData = (AnswerData) checkBox.getTag();

            for(int l=0;l<datalist.size();l++){
                if(datalist.get(l).contains(answerData)){

                    List<AnswerData> list = datalist.get(l);

                    int k = list.indexOf(answerData);
                    list.remove(k);
                    answerData.set_IS_ACTIVE(String.valueOf(checkBox.isChecked()));
                    answerData.set_VALUE(String.valueOf(checkBox.isChecked()));
                    list.add(k, answerData);
                    datalist.remove(l);
                    datalist.add(l, list);
                }
            }
        }

        AnswerDataORM.insertAnswerFormDataListtoDatabase(this, datalist);
        Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    private void hideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        answer_scrollview.removeAllViews();
    }
}
