package com.algo.hha.fhsurvey;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import com.algo.hha.fhsurvey.utility.Config;
import com.algo.hha.fhsurvey.utility.InputFilterMinMax;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;


public class AnswerActivity extends ActionBarActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    LinearLayout mainLayout;
    TextView btn_next, btn_prev;
    Toolbar mToolbar;
    int position = 0;

    List<List<AnswerData>> answerlist;
    long answertimestamp;
    String username;
    TextView toolbarTitle;
    List<EditText> allEds = new ArrayList<EditText>();
    List<Integer> position_track;

    InputFilter filter;

    String form_id = "", proj_id = "";
    boolean doubleBackToExitPressedOnce = false;

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


        Bundle bundle = getIntent().getExtras();
        position_track = new ArrayList<>();
        if(bundle != null) {
            form_id = bundle.getString("form_id");
            proj_id = bundle.getString("proj_id");
        }

        showDialogToAddUserName();

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

    }
/*
    public CharSequence filter(CharSequence charsequence, int i, int j, Spanned spanned, int k, int l)
    {
        for (; i < j; i++)
        {
            if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*").matcher(String.valueOf(charsequence.charAt(i))).matches())
            {
                return "";
            }
        }

        return null;
    }*/

    private List createAnswerListFromQuestionForm(List list)
    {
        ArrayList arraylist = new ArrayList();
        SharedPreferences sharedpreferences = getSharedPreferences(Config.APP_PREFERENCE, 0);
        for (int i = 0; i < list.size(); i++)
        {
            AnswerData answerdata = new AnswerData();
            QuestionFormData questionformdata = (QuestionFormData)list.get(i);
            answerdata.set_ProjectID(questionformdata.get_ProjectID());
            answerdata.set_ProjectName(questionformdata.get_ProjectName());
            answerdata.set_FormID(questionformdata.get_FormID());
            answerdata.set_FormDescription(questionformdata.get_FormDescription());
            answerdata.set_FormIndex(questionformdata.get_FormIndex());
            answerdata.set_QuestionGroupID(questionformdata.get_QuestionGroupID());
            answerdata.set_QuestionGroupIndex(questionformdata.get_QuestionGroupIndex());
            answerdata.set_QuestionGroupDescription(questionformdata.get_QuestionGroupDescription());
            answerdata.set_QuestionID(questionformdata.get_QuestionID());
            answerdata.set_QuestionIndex(questionformdata.get_QuestionIndex());
            answerdata.set_Condition(questionformdata.get_Condition());
            answerdata.set_QuestionShortCode(questionformdata.get_QuestionShortCode());
            answerdata.set_QuestionDescription(questionformdata.get_QuestionDescription());
            answerdata.set_QuestionInstruction(questionformdata.get_QuestionInstruction());
            answerdata.set_AnswerTypeID(questionformdata.get_AnswerTypeID());
            answerdata.set_AnswerTypeDescription(questionformdata.get_AnswerTypeDescription());
            answerdata.set_AnswerID(questionformdata.get_AnswerID());
            answerdata.set_AnswerDescription(questionformdata.get_AnswerDescription());
            answerdata.set_AnswerIndex(questionformdata.get_AnswerIndex());
            answerdata.set_SkippedTo(questionformdata.get_SkippedTo());
            answerdata.set_AnswerColumnID(questionformdata.get_AnswerColumnID());
            answerdata.set_ColumnDescription(questionformdata.get_ColumnDescription());
            answerdata.set_AnswerColumnIndex(questionformdata.get_AnswerColumnIndex());

            answerdata.set_userid(sharedpreferences.getString(Config.USERID, ""));
            answerdata.set_timestamp(String.valueOf(answertimestamp));
            answerdata.set_answerer(username);

            arraylist.add(answerdata);
        }

        return arraylist;
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
        position_track.add(position);
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
     * this method create a view with radio button data from server
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */
    private View createSingleChoiceAnswer(int position, List<List<AnswerData>> dl){
        List<AnswerData> itemlist = dl.get(position);

        if (( itemlist.get(0)).get_Condition().contains("Required"))
        {
            setEnabled(containAnswer(itemlist));
        }else{
            setEnabled(true);
        }

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
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
     * this method create a view with radio button data from server
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */
    private View createMultiChoiceAnswer(int position, List<List<AnswerData>> dl){
        List<AnswerData> itemlist = dl.get(position);

        if (((AnswerData) itemlist.get(0)).get_Condition().contains("Required"))
        {
            setEnabled(containAnswer(itemlist));
        }

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
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
            linearLayout.addView(checkbox);

            checkbox.setOnCheckedChangeListener(this);
            //checked first item(default)
            if(itemlist.get(i).get_IS_ACTIVE() != null) {
                if (itemlist.get(i).get_IS_ACTIVE().equals("true")){
                    checkbox.setChecked(true);
                }
            }
        }


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

        final List<AnswerData> itemlist = dl.get(position);


        if ( itemlist.get(0).get_Condition().contains("Required"))
        {
            setEnabled(containAnswer(itemlist));
        }else{
            setEnabled(true);
        }
        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
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
        editText.setOnFocusChangeListener(this);

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
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable)
            {
            }

            public void beforeTextChanged(CharSequence charsequence, int j, int k, int l)
            {
            }

            public void onTextChanged(CharSequence charsequence, int j, int k, int l)
            {
                if (( itemlist.get(0)).get_Condition().contains("Required"))
                {
                    if (TextUtils.isEmpty(charsequence))
                    {
                        setEnabled(false);
                        return;
                    } else
                    {
                        setEnabled(true);
                        return;
                    }
                } else
                {
                    setEnabled(true);
                    return;
                }
            }

        });

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

    private View createNumberInputAnswer(final int position, List<List<AnswerData>> dl){

        final List<AnswerData> itemlist = dl.get(position);


        if ( itemlist.get(0).get_Condition().contains("Required"))
        {
            setEnabled(containAnswer(itemlist));
        }else{
            setEnabled(true);
        }
        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
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
                    editText.setFilters(new InputFilter[]{
                            new InputFilterMinMax(AnswerActivity.this, list[1], list[2])
                    });
                    /*editText.setFilters(new InputFilter[] {
                            filter, new android.text.InputFilter.LengthFilter(Integer.parseInt(list[2]))
                    });*/
                }
            }
            // Misplaced declaration of an exception variable
            catch (Exception ex) { }
        }

        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable)
            {
            }

            public void beforeTextChanged(CharSequence charsequence, int j, int k, int l)
            {
            }

            public void onTextChanged(CharSequence charsequence, int j, int k, int l)
            {
                if (( itemlist.get(0)).get_Condition().contains("Required"))
                {
                    if (TextUtils.isEmpty(charsequence))
                    {
                        setEnabled(false);
                        return;
                    } else
                    {
                        setEnabled(true);
                        return;
                    }
                } else
                {
                    setEnabled(true);
                    return;
                }
            }

        });

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

        if (( itemlist.get(0)).get_Condition().contains("Required"))
        {
            boolean isEnabled = isTableContainRequired(itemlist);
            setEnabled(!isEnabled);
        }else{
            setEnabled(true);
        }

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
                titletextparam = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
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
            LinearLayout valueLayout = new LinearLayout(AnswerActivity.this);
            LinearLayout.LayoutParams valuelayoutparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            valueLayout.setOrientation(LinearLayout.HORIZONTAL);
            valueLayout.setMinimumHeight(80);
            valueLayout.setLayoutParams(valuelayoutparam);

            //add textview for answer description that shows in frist column
            TextView textView = new TextView(AnswerActivity.this);
            LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(200 , ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(valuetextparam);
            textView.setText(itemlist.get(i).get_AnswerDescription());
            textView.setPadding( 8, 8, 8, 8);
            textView.setBackgroundResource(R.drawable.background_tabletextview);
            valueLayout.addView(textView);

            if(itemlist.get(i).get_AnswerTypeDescription().equals(AnswerType.MULTI_CHOICE_MULTI)){
                for(int j=i;j<columnCount+i; j++){
                    View cb_view = getCheckBoxForTable(itemlist.get(j), itemlist);
                    if(cb_view != null){
                        valueLayout.addView(cb_view);
                    }
                }
            }
            else if(itemlist.get(i).get_AnswerTypeDescription().equals(AnswerType.SINGLE_CHOICE_MULTI)){

                RadioGroup rd_group = new RadioGroup(this);
                LinearLayout.LayoutParams rd_group_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rd_group.setOrientation(LinearLayout.HORIZONTAL);
                rd_group.setLayoutParams(rd_group_param);

                for (int j = i; j < columnCount + i; j++) {

                    View rb_view = getRadioButtonForTable(itemlist.get(j), itemlist);
                    if (rb_view != null) {
                        rd_group.addView(rb_view);
                    }

                }
                valueLayout.addView(rd_group);
            }
            //create Edittext with TEXT Input type and add to table layout
            else if(itemlist.get(i).get_AnswerTypeDescription().equals(AnswerType.TEXT_MULTI)){
                for(int j=i;j<columnCount+i; j++){
                    View editTextView = getEditTextView(itemlist.get(j), itemlist);
                    if(editTextView != null){
                        valueLayout.addView(editTextView);
                    }
                }
            }
            //create Edittext with NUMBER Input type and add to table layout
            else if(itemlist.get(i).get_AnswerTypeDescription().equals(AnswerType.NUMBER_MULTI)){
                for(int j=i;j<columnCount+i; j++){
                    View editTextView = getNumberEditText(itemlist.get(j), itemlist);
                    if(editTextView != null){
                        valueLayout.addView(editTextView);
                    }
                }
            }
            //create Edittext with DATE Input type and add to table layout
            else if(itemlist.get(i).get_AnswerTypeDescription().equals(AnswerType.NUMBER_MULTI)){

                for(int j=i;j<columnCount+i; j++){
                    View editTextView = getDateEditText(itemlist.get(j), itemlist);
                    if(editTextView != null){
                        valueLayout.addView(editTextView);
                    }
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



    private View getEditTextView(AnswerData data, final List datalist){

        final EditText editText = new EditText(AnswerActivity.this);
        LinearLayout.LayoutParams valuetextparam = new LinearLayout.LayoutParams(200 , ViewGroup.LayoutParams.MATCH_PARENT);
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

        if (data.get_Condition() != null)
        {
            String condition = data.get_Condition();

            if (condition.contains("Required"))
            {
                String as[] = condition.split("/");

                if (as.length > 1)
                {
                    condition = as[0];
                }
            }
            try
            {
                String[] validation = condition.split(":");
                if (validation[0].equals("Yes"))
                {
                    editText.setFilters(new InputFilter[] {
                            filter, new android.text.InputFilter.LengthFilter(Integer.parseInt(validation[2]))
                    });
                }
            }
            // Misplaced declaration of an exception variable
            catch (Exception ex) { }
        }
        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable)
            {
                updateTableEditTextValue();
                boolean flag;
                if (!isTableContainRequired(datalist))
                {
                    flag = true;
                } else
                {
                    flag = false;
                }
                setEnabled(flag);
            }

            public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }
        });

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

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.next_button:

                hideKeyboard();
                updateTableEditTextValue();
                allEds.clear();
                if(btn_next.getText().toString().equals("NEXT")) {

                    if (answerlist == null) return;

                    if(position >= answerlist.size()){
                        return;
                    }

                    List<AnswerData> list = answerlist.get(position);

                    String skippedTo = "";
                    for(int l=0;l<list.size();l++){
                        if(list.get(l).get_IS_ACTIVE() != null){
                            if(list.get(l).get_IS_ACTIVE().equals("true")){
                                if(list.get(l).get_SkippedTo() != null){
                                    skippedTo = list.get(l).get_SkippedTo();
                                    Log.i("Skipped To Value", list.get(l).get_SkippedTo());
                                }
                            }
                        }
                    }

                    position++;
                    if(position < answerlist.size())
                    for(;position < answerlist.size(); position++){

                        if(skippedTo.equals("")){
                            break;
                        }

                        if(skippedTo.equals(answerlist.get(position).get(0).get_QuestionShortCode())){
                            break;
                        }
                    }


                    setEnabled(false);

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

                position_track.remove(position_track.size() - 1);
                if(position_track.size() > 0) {
                    position = position_track.get(position_track.size() - 1);

                    if (position < answerlist.size()) {
                        mainLayout.removeAllViews();
                        mainLayout.addView(matchUIwithItemType(position, answerlist));
                        position_track.remove(position_track.size()-1);
                    }
                }

                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        AnswerData data = (AnswerData) buttonView.getTag();

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

                if ((templist.get(0)).get_Condition().contains("Required"))
                {
                    setEnabled(containAnswer(templist));
                }

                break;

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
                        }if (AnswerDataORM.isAnswererAlreadyIn(AnswerActivity.this, un, form_id, proj_id))
                        {
                            Toast.makeText(AnswerActivity.this, "Name is already used!", Toast.LENGTH_SHORT).show();
                            return;
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

        EditText et_ask_username = (EditText) dialog.findViewById(R.id.customdialog_ask_username);
        et_ask_username.setFilters(new InputFilter[] {
                new InputFilter() {


                    public CharSequence filter(CharSequence charsequence, int i, int j, Spanned spanned, int k, int l)
                    {
                        if (charsequence.equals(""))
                        {
                            return charsequence;
                        }
                        if (charsequence.toString().matches("[a-zA-Z ]+"))
                        {
                            return charsequence;
                        } else
                        {
                            return "";
                        }
                    }


                }
        });

        dialog.show();


    }



    private void startAnswerActivity(){
        Bundle bundle = getIntent().getExtras();
        position_track = new ArrayList<>();
        if(bundle != null){
            form_id = bundle.getString("form_id");
            proj_id = bundle.getString("proj_id");
            if(form_id != null){
                //getDataFromServer(formId);
                answerlist = getDataFromDatabase(form_id);

                if(answerlist.size() > 0){

                    if(matchUIwithItemType(position, answerlist) != null)
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

    /***
     * this method create EditText with title
     * @param position
     * list's position to get data
     * @return view
     * to use as listview's item
     */

    private View createDateTextInputAnswer(final int position, List<List<AnswerData>> dl){

        final List<AnswerData> itemlist = dl.get(position);

        if ( itemlist.get(0).get_Condition().contains("Required"))
        {
            setEnabled(containAnswer(itemlist));
        }else{
            setEnabled(true);
        }

        //crate linearlayout as main layout
        LinearLayout linearLayout = new LinearLayout(this);
        //set layout param of abslistview
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(8, 8, 8, 8);
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
        editText.setOnFocusChangeListener(this);

        if(itemlist.get(0).get_IS_ACTIVE() != null) {
            if (itemlist.get(0).get_IS_ACTIVE().equals("true")){
                editText.setText(itemlist.get(0).get_VALUE());
            }
        }

        editText.setFocusable(false);
        editText.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                showDatePickerDialog();
                editText.setId(R.id.my_edit_text_1);
            }

        });

        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable)
            {
            }

            public void beforeTextChanged(CharSequence charsequence, int j, int k, int l)
            {
            }

            public void onTextChanged(CharSequence charsequence, int j, int k, int l)
            {
                if (( itemlist.get(0)).get_Condition().contains("Required"))
                {
                    if (TextUtils.isEmpty(charsequence))
                    {
                        setEnabled(false);
                        return;
                    } else
                    {
                        setEnabled(true);
                        return;
                    }
                } else
                {
                    setEnabled(true);
                    return;
                }
            }

        });


        linearLayout.addView(editText);

        return linearLayout;
    }

    public void setEnabled(boolean flag)
    {
        btn_next.setEnabled(flag);
        if(flag){
            btn_next.setTextColor(getResources().getColor(R.color.pink_500));
        }else{
            btn_next.setTextColor(getResources().getColor(R.color.grey_500));
        }
    }

    public boolean containAnswer(List<AnswerData> dl)
    {
        boolean flag = false;
        for (int i=0; i<dl.size(); i++)
        {
            AnswerData answerdata = dl.get(i);
            if (answerdata.get_IS_ACTIVE() == null)
            {
                continue;
            }

            if (answerdata.get_IS_ACTIVE().equals("true"))
            {
                flag = true;
            }
        }

        return flag;
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
        for(int j=0;j<answerlist.size();j++){
            List list = answerlist.get(j);
            if(!list.contains(answerdata)){
                continue;
            }

            int k = list.indexOf(answerdata);
            list.remove(k);
            answerdata.set_IS_ACTIVE("true");
            answerdata.set_VALUE(et.getText().toString());
            list.add(k, answerdata);
            answerlist.remove(j);
            answerlist.add(j, list);

            break;
        }

    }

    private boolean isTableContainRequired(List<AnswerData> list)
    {
        boolean isRequired = false;
        int i = 0;
        while (i < list.size())
        {
            if (list.get(i).get_Condition().contains("Required"))
            {
                if (list.get(i).get_IS_ACTIVE() == null)
                {
                    isRequired = true;
                } else
                if (list.get(i).get_IS_ACTIVE().equals("false"))
                {
                    isRequired = true;
                } else
                {
                    if (list.get(i).get_IS_ACTIVE().equals("true"))
                    {
                        if (TextUtils.isEmpty(list.get(i).get_VALUE()))
                        {
                            isRequired = true;
                        }
                    }
                }
            }
            i++;
        }
        return isRequired;
    }

    public void updateTableEditTextValue()
    {
        int i = 0;
        label0:
        do
        {
            if (i < allEds.size())
            {
                EditText edittext = allEds.get(i);
                AnswerData answerdata = (AnswerData)edittext.getTag();
                int j = 0;
                do
                {
                    label1:
                    {
                        if (j < answerlist.size())
                        {
                            List list = (List)answerlist.get(j);
                            if (!list.contains(answerdata))
                            {
                                break label1;
                            }
                            int k = list.indexOf(answerdata);
                            list.remove(k);
                            answerdata.set_IS_ACTIVE("true");
                            answerdata.set_VALUE(edittext.getText().toString());
                            list.add(k, answerdata);
                            answerlist.remove(j);
                            answerlist.add(j, list);
                            allEds.get(i).setTag(answerdata);
                        }
                        i++;
                        continue label0;
                    }
                    j++;
                } while (true);
            }
            return;
        } while (true);
    }



    public EditText getNumberEditText(AnswerData item, List<AnswerData> itemlist){
        //create edittext as requirement
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
        editText_param.setMargins(8, 8, 8, 8);
        editText.setSingleLine();
        editText.setLayoutParams(editText_param);
        editText.setEms(10);
        editText.setTag(item);
        editText.setOnFocusChangeListener(this);
        editText.setBackgroundResource(R.drawable.background_tabletextview);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(item.get_IS_ACTIVE() != null) {
            if (item.get_IS_ACTIVE().equals("true")){
                editText.setText(item.get_VALUE());
            }
        }

        if (item.get_Condition() != null)
        {
            String validatevalue = "";
            String s = item.get_Condition();
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
                    editText.setFilters(new InputFilter[]{
                            new InputFilterMinMax(AnswerActivity.this, Integer.parseInt(list[1]), Integer.parseInt(list[2]))
                    });
                    /*editText.setFilters(new InputFilter[] {
                            filter, new InputFilter.LengthFilter(Integer.parseInt(list[2]))
                    });*/
                }
            }
            // Misplaced declaration of an exception variable
            catch (Exception ex) { }
        }

        return editText;

    }

    public EditText getDateEditText(AnswerData item, final List<AnswerData> itemlist){
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
        editText_param.setMargins(8, 8, 8, 8);
        editText.setSingleLine();
        editText.setLayoutParams(editText_param);
        editText.setEms(10);
        editText.setTag(item);
        editText.setBackgroundResource(R.drawable.background_tabletextview);
        editText.setOnFocusChangeListener(this);

        if(item.get_IS_ACTIVE() != null) {
            if (item.get_IS_ACTIVE().equals("true")){
                editText.setText(item.get_VALUE());
            }
        }

        editText.setFocusable(false);
        editText.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                showDatePickerDialog();
                editText.setId(R.id.my_edit_text_1);
            }

        });



        return editText;
    }

    public EditText getTextEditText(List<AnswerData> itemlist){
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams editText_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText_param.setMargins(8, 8, 8, 8);
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
        return editText;
    }

    public View getRadioButtonForTable(AnswerData item, List<AnswerData> itemlist){


            RadioButton radioButton = new RadioButton(this);
            LinearLayout.LayoutParams valueradiobuttonparam = new LinearLayout.LayoutParams(200 , ViewGroup.LayoutParams.MATCH_PARENT);
            radioButton.setTag(item);
            radioButton.setText(item.get_ColumnDescription());
            radioButton.setBackgroundResource(R.drawable.background_tabletextview);
            radioButton.setLayoutParams(valueradiobuttonparam);

            radioButton.setOnCheckedChangeListener(this);

            if(item.get_IS_ACTIVE() != null) {
                if (item.get_IS_ACTIVE().equals("true")){
                    radioButton.setChecked(true);
                }else{
                    radioButton.setChecked(false);
                }
            }


        return radioButton;
    }

    public View getCheckBoxForTable(AnswerData item, List<AnswerData> itemlist){

            CheckBox checkbox = new CheckBox(this);
            LinearLayout.LayoutParams valueradiobuttonparam = new LinearLayout.LayoutParams(200 , ViewGroup.LayoutParams.MATCH_PARENT);
            checkbox.setTag(item);
            checkbox.setText(item.get_ColumnDescription());
            checkbox.setLayoutParams(valueradiobuttonparam);
            checkbox.setBackgroundResource(R.drawable.background_tabletextview);

            checkbox.setOnCheckedChangeListener(this);
            //checked first item(default)
            if(item.get_IS_ACTIVE() != null) {
                if (item.get_IS_ACTIVE().equals("true")){
                    checkbox.setChecked(true);
                }else{
                    checkbox.setChecked(false);
                }
            }


        return checkbox;
    }

}
