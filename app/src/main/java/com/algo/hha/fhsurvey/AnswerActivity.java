package com.algo.hha.fhsurvey;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.configuration.AnswerType;
import com.algo.hha.fhsurvey.db.AnswerDataORM;
import com.algo.hha.fhsurvey.db.QuestionFormDataORM;
import com.algo.hha.fhsurvey.model.AnswerData;
import com.algo.hha.fhsurvey.model.QuestionFormData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AnswerActivity extends ActionBarActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    LinearLayout mainLayout;
    TextView btn_next, btn_prev;
    Toolbar mToolbar;
    int position = 0;

    List<List<AnswerData>> answerlist;
    long answertimestamp;
    String username;
    TextView toolbarTitle;
    List<EditText> allEds = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        answertimestamp = Calendar.getInstance().getTimeInMillis();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
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

        mainLayout = (LinearLayout) findViewById(R.id.answer_form_linearview);
        btn_next = (TextView) findViewById(R.id.next_button);
        btn_prev = (TextView) findViewById(R.id.prev_button);

        btn_next.setOnClickListener(this);
        btn_prev.setOnClickListener(this);

        showDialogToAddUserName();
    }


    private List<AnswerData> createAnswerListFromQuestionForm(List<QuestionFormData> dlist){

        List<AnswerData> datalist = new ArrayList<>();

        for(int k=0;k<dlist.size();k++){
            AnswerData answerData = new AnswerData();
            QuestionFormData data = dlist.get(k);

            answerData.set_ProjectID(data.get_ProjectID());
            answerData.set_ProjectName(data.get_ProjectName());
            answerData.set_FormID(data.get_FormID());
            answerData.set_FormDescription(data.get_FormDescription());
            answerData.set_FormIndex(data.get_FormIndex());
            answerData.set_QuestionGroupID(data.get_QuestionGroupID());
            answerData.set_QuestionGroupIndex(data.get_QuestionGroupIndex());
            answerData.set_QuestionGroupDescription(data.get_QuestionGroupDescription());
            answerData.set_QuestionID(data.get_QuestionID());
            answerData.set_QuestionIndex(data.get_QuestionIndex());
            answerData.set_Condition(data.get_Condition());
            answerData.set_QuestionShortCode(data.get_QuestionShortCode());
            answerData.set_QuestionDescription(data.get_QuestionDescription());
            answerData.set_QuestionInstruction(data.get_QuestionInstruction());
            answerData.set_AnswerTypeID(data.get_AnswerTypeID());
            answerData.set_AnswerTypeDescription(data.get_AnswerTypeDescription());
            answerData.set_AnswerID(data.get_AnswerID());
            answerData.set_AnswerDescription(data.get_AnswerDescription());
            answerData.set_AnswerIndex(data.get_AnswerIndex());
            answerData.set_SkippedTo(data.get_SkippedTo());
            answerData.set_AnswerColumnID(data.get_AnswerColumnID());
            answerData.set_ColumnDescription(data.get_ColumnDescription());
            answerData.set_AnswerColumnIndex(data.get_AnswerColumnIndex());
            answerData.set_timestamp(String.valueOf(answertimestamp));
            answerData.set_answerer(username);

            datalist.add(answerData);

        }

        return datalist;
    }

    private List<List<AnswerData>> getDataFromDatabase(String form_id){

        List<QuestionFormData> dbdatalist = QuestionFormDataORM.getQuestionFormDatalist(AnswerActivity.this, form_id);

        if(dbdatalist == null)
            return null;

        List<AnswerData> answerdatalist = createAnswerListFromQuestionForm(dbdatalist);

        List<List<AnswerData>> maindatalist = new ArrayList<>();

        String tempQuestionID = null;
        List<AnswerData> datalist = null;

        for(int i=0;i<answerdatalist.size();i++) {

            AnswerData data = answerdatalist.get(i);

            //check new Answer Group Id or not
            if (tempQuestionID == null) {
                Log.i("Question ID", "Temp is null");
                datalist = new ArrayList<>();
                datalist.add(data);
                tempQuestionID = data.get_QuestionID();
            } else if (tempQuestionID.equals(data.get_QuestionID())) {//same answer group id, add to current list
                Log.i("Question ID", "They are equal");
                datalist.add(data);

                if(i == answerdatalist.size() - 2){
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


    private View matchUIwithItemType(int position, List<List<AnswerData>> dl){
        List<AnswerData> itemlist = dl.get(position);
        if(itemlist.size() <= 0){
            return null;
        }else{

            if(position == 0){
                btn_prev.setVisibility(View.INVISIBLE);
            }else{
                btn_prev.setVisibility(View.VISIBLE);
            }

            if(position == dl.size() - 1){
                btn_next.setText("SAVE");
            }else {
                btn_next.setText("NEXT");
            }

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

        //add group title textview to layout
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
        linearLayout.addView(tv_answer_grouptitle);

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

        //create radio group for all radio buttons
        RadioGroup rd_group = new RadioGroup(this);
        LinearLayout.LayoutParams rd_group_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rd_group_param.setMargins(16, 16, 16, 16);
        rd_group.setLayoutParams(rd_group_param);

        //this loop create radio button per data count
        for(int i=0;i<itemlist.size();i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setTag(itemlist.get(i));
            radioButton.setText(itemlist.get(i).get_AnswerDescription());
            rd_group.addView(radioButton);

            radioButton.setOnCheckedChangeListener(this);
            //checked first item(default)
            if(itemlist.get(i).get_IS_ACTIVE() != null) {
                if (itemlist.get(i).get_IS_ACTIVE().equals("true")){
                    radioButton.setChecked(true);
                }
            }
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

        //add group title textview to layout
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
        linearLayout.addView(tv_answer_grouptitle);

        //create title(Question) for edittext
        TextView tv_answer_title = new TextView(this);
        LinearLayout.LayoutParams title_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_param.setMargins(16, 16, 16, 16);
        tv_answer_title.setLayoutParams(title_param);

        tv_answer_title.setText(itemlist.get(0).get_QuestionDescription());
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
        editText.setOnFocusChangeListener(this);

        if(itemlist.get(0).get_IS_ACTIVE() != null) {
            if (itemlist.get(0).get_IS_ACTIVE().equals("true")){
                editText.setText(itemlist.get(0).get_VALUE());
            }
        }


        linearLayout.addView(editText);

        return linearLayout;
    }

    private View createDividerView(){
        View view = new View(AnswerActivity.this);
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

        //add group title textview to layout
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
        linearLayout.addView(tv_answer_grouptitle);

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
        LinearLayout mainlayout = new LinearLayout(AnswerActivity.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(16, 16, 16, 16);
        mainlayout.setLayoutParams(param);
        mainlayout.setOrientation(LinearLayout.VERTICAL);

        //get column count from list
        int columnCount = getColumnCountFromDataList(itemlist);

        LinearLayout titleLayout = new LinearLayout(AnswerActivity.this);
        LinearLayout.LayoutParams titleparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setMinimumHeight(56);
        titleLayout.setLayoutParams(titleparam);
        for(int i=0;i<columnCount+1;i++){

            TextView textView = new TextView(AnswerActivity.this);
            LinearLayout.LayoutParams titletextparam;
            textView.setPadding( 8, 8, 8, 8);
            if(i == 0){
                textView.setText("");
                titletextparam = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT);
            }else{
                textView.setText(itemlist.get(i-1).get_ColumnDescription());
                titletextparam = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            textView.setLayoutParams(titletextparam);

            textView.setBackgroundResource(R.drawable.background_tabletextview);
            titleLayout.addView(textView);

        }

        mainlayout.addView(titleLayout);


        for(int i=0;i<itemlist.size();i+=columnCount){

            //create layout for value not title
            LinearLayout valueLayout = new LinearLayout(AnswerActivity.this);
            LinearLayout.LayoutParams valuelayoutparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            valueLayout.setOrientation(LinearLayout.HORIZONTAL);
            valueLayout.setMinimumHeight(80);
            valueLayout.setLayoutParams(valuelayoutparam);

            //add textview for answer description that shows in frist column
            TextView textView = new TextView(AnswerActivity.this);
            LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(valuetextparam);
            textView.setText(itemlist.get(i).get_AnswerDescription());
            textView.setPadding( 8, 8, 8, 8);
            textView.setBackgroundResource(R.drawable.background_tabletextview);
            valueLayout.addView(textView);

            //call loop er column Count and create EditText
            for(int j=i;j<columnCount + i;j++){

                View et_view = getEditTextView(itemlist.get(j));
                if(et_view != null){
                    valueLayout.addView(et_view);
                }

            }

            mainlayout.addView(valueLayout);

        }

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(AnswerActivity.this);
        LinearLayout.LayoutParams horizontalScrollViewparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        horizontalScrollViewparam.setMargins(16,16,16,16);
        horizontalScrollView.setLayoutParams(horizontalScrollViewparam);

        horizontalScrollView.addView(mainlayout);
        linearLayout.addView(horizontalScrollView);

        return linearLayout;
    }

    private View getEditTextView(AnswerData data){

        EditText editText = new EditText(AnswerActivity.this);
        LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.MATCH_PARENT);
        valuetextparam.weight = 1;
        editText.setLayoutParams(valuetextparam);
        editText.setSingleLine();
        editText.setBackgroundResource(R.drawable.background_tabletextview);
        editText.setTag(data);
        //editText.setOnFocusChangeListener(this);

        if(data.get_IS_ACTIVE() != null) {
            if (data.get_IS_ACTIVE().equals("true")){
                editText.setText(data.get_VALUE());
            }
        }

        allEds.add(editText);

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
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.next_button:

                hideKeyboard();

                if(btn_next.getText().toString().equals("NEXT")) {

                    if (answerlist == null) return;

                    position++;

                    if (position < answerlist.size()) {
                        mainLayout.removeAllViews();
                        mainLayout.addView(matchUIwithItemType(position, answerlist));
                    }

                }else if(btn_next.getText().toString().equals("SAVE")){
                    //TODO save to db and show success

                    MaterialDialog dialog = new MaterialDialog.Builder(AnswerActivity.this)
                            .content("Saving...").build();
                    dialog.show();

                    for(int k=0;k<allEds.size();k++){
                        EditText et = allEds.get(k);
                        AnswerData data = (AnswerData) et.getTag();

                        for (int l = 0; l < answerlist.size(); l++) {
                            List<AnswerData> templist = answerlist.get(l);
                            if (templist.contains(data)) {

                                int pos = templist.indexOf(data);
                                templist.remove(pos);

                                Log.i("Position", String.valueOf(pos));
                                data.set_IS_ACTIVE("true");
                                data.set_VALUE(et.getText().toString());

                                templist.add(pos, data);
                                answerlist.remove(l);

                                answerlist.add(l, templist);
                                break;

                            }
                        }
                    }

                    int count = AnswerDataORM.insertAnswerFormDataListtoDatabase(AnswerActivity.this, answerlist);
                    dialog.dismiss();
                    Toast.makeText(AnswerActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
                    finish();

                }

                break;

            case R.id.prev_button:

                hideKeyboard();

                if(answerlist == null) return;

                position--;

                if(position < answerlist.size()) {
                    mainLayout.removeAllViews();
                    mainLayout.addView(matchUIwithItemType(position, answerlist));
                }

                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        RadioButton rb = (RadioButton) buttonView;

        AnswerData data = (AnswerData) rb.getTag();

        for(int k=0;k<answerlist.size();k++){
            List<AnswerData> templist = answerlist.get(k);
            if(templist.contains(data)){

                int pos = templist.indexOf(data);
                templist.remove(pos);
                if(isChecked){
                    data.set_IS_ACTIVE("true");
                    data.set_VALUE("true");
                }else{
                    data.set_IS_ACTIVE("false");
                    data.set_VALUE("false");
                }
                templist.add(pos, data);
                answerlist.remove(k);


                answerlist.add(k, templist);

            }
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(!hasFocus) {

            EditText et = (EditText) v;
            AnswerData data = (AnswerData) et.getTag();

            for (int k = 0; k < answerlist.size(); k++) {
                List<AnswerData> templist = answerlist.get(k);
                if (templist.contains(data)) {

                    int pos = templist.indexOf(data);
                    templist.remove(pos);

                    Log.i("Position", String.valueOf(pos));
                    data.set_IS_ACTIVE("true");
                    data.set_VALUE(et.getText().toString());

                    templist.add(pos, data);
                    answerlist.remove(k);

                    answerlist.add(k, templist);
                    break;

                }
            }
        }
    }

    private void showDialogToAddUserName(){

        MaterialDialog dialog = new MaterialDialog.Builder(AnswerActivity.this)
                .title("What is your name?")
                .customView(R.layout.customdialog_askusername, false)
                .positiveText("Start")
                .negativeText("Cancel")
                .autoDismiss(false)
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        EditText et_username = (EditText) dialog.findViewById(R.id.customdialog_ask_username);
                        String un = et_username.getText().toString();

                        if (TextUtils.isEmpty(un)) {
                            Toast.makeText(AnswerActivity.this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                        } else {

                            username = un;
                            startAnswerActivity();

                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        AnswerActivity.this.finish();
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();


    }

    private void startAnswerActivity(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String formId = bundle.getString("form_id");
            if(formId != null){
                //getDataFromServer(formId);
                answerlist = getDataFromDatabase(formId);

                if(answerlist.size() > 0){
                    mainLayout.addView(matchUIwithItemType(position, answerlist));
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

}
