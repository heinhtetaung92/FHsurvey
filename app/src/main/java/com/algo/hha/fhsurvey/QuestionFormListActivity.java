package com.algo.hha.fhsurvey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.adapter.ProjectFormListAdapter;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.db.ProjectFormDataORM;
import com.algo.hha.fhsurvey.db.QuestionFormDataORM;
import com.algo.hha.fhsurvey.model.ProjectFormData;
import com.algo.hha.fhsurvey.model.QuestionFormData;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class QuestionFormListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView form_list;
    Toolbar mToolbar;

    ProgressWheel progress;
    View progress_background;

    ProjectFormData selectedData;

    MaterialDialog dialog = null;

    ProgressDialog download_progress_dialog = null;
    int download_progress_increaseValue = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_form_list);
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

        form_list = (ListView) findViewById(R.id.question_form_data_listview);

        progress = (ProgressWheel) findViewById(R.id.formlist_progress_wheel);
        progress_background = findViewById(R.id.formlist_progress_wheel_background);

        showProgressOrNot(true);

        //hardcode data for project list
        List<ProjectFormData> datalist = ProjectFormDataORM.getProjectFormDatalist(this);
        ProjectFormListAdapter adp = new ProjectFormListAdapter(this, datalist);
        form_list.setAdapter(adp);

        if (datalist.size() > 0) {
            showProgressOrNot(false);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String proj_id = bundle.getString("project_id");
            String proj_name = bundle.getString("project_name");

            if (proj_name != null) {
                toolbarTitle.setText(proj_name);
            } else {
                toolbarTitle.setText("FHSurvey");
            }

            if (proj_id != null) {
                getDataFromServer(proj_id);
            } else {
                showProgressOrNot(false);
            }
        } else {
            toolbarTitle.setText("FHSurvey");
            showProgressOrNot(false);
        }

        form_list.setOnItemClickListener(this);
    }

    private void getDataFromServer(String proj_id) {
        RetrofitAPI.getInstance().getService().getFormListByProjectID(proj_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                List<ProjectFormData> datalist = parseJSONToObject(s);
                addDataToListView(datalist);

                ProjectFormDataORM.insertProjectDataListtoDatabase(QuestionFormListActivity.this, datalist);
                showProgressOrNot(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(QuestionFormListActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
                showProgressOrNot(false);
            }
        });
    }

    private List<ProjectFormData> parseJSONToObject(String s) {
        try {
            /*JSONArray arr = new JSONArray(s);

            for(int i=0;i<arr.length();i++){*/
            List<ProjectFormData> datalist = new ArrayList<>();
            ProjectFormData data = new ProjectFormData();
            JSONObject obj = new JSONObject(s);

            if (!obj.isNull("FormID")) {
                data.set_formID(obj.getString("FormID"));
            }

            if (!obj.isNull("ProjectID")) {
                data.set_projectID(obj.getString("ProjectID"));
            }

            if (!obj.isNull("FormDescription")) {
                data.set_formDescription(obj.getString("FormDescription"));
            }

            if (!obj.isNull("FormIndex")) {
                data.set_formIndex(obj.getString("FormIndex"));
            }

            if (!obj.isNull("Status")) {
                data.set_status(obj.getString("Status"));
            }

            datalist.add(data);
/*
            }*/

            return datalist;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addDataToListView(List<ProjectFormData> dl) {
        if (form_list == null) return;

        ProjectFormListAdapter adp = (ProjectFormListAdapter) form_list.getAdapter();
        adp.add(dl);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProjectFormListAdapter adp = (ProjectFormListAdapter) form_list.getAdapter();
        selectedData = (ProjectFormData) adp.getItem(position);

        //show choice list for form list
        showChoiceDialog(selectedData);
    }


    private void showChoiceDialog(ProjectFormData data) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(data.get_formDescription());
        builder.customView(R.layout.custom_dialog_formlistchoice);
        builder.autoDismiss(false);
        builder.backgroundColor(android.R.color.white);
        builder.backgroundColorRes(android.R.color.white);
        dialog = builder.build();
        dialog.show();

        TextView tv_addanswer = (TextView) dialog.findViewById(R.id.custom_dialog_add_answer);
        TextView tv_viewquestion = (TextView) dialog.findViewById(R.id.custom_dialog_view_questions);
        TextView tv_viewanswers = (TextView) dialog.findViewById(R.id.custom_dialog_view_answers);
        TextView tv_downloadquestion = (TextView) dialog.findViewById(R.id.custom_dialog_download_form);
        TextView tv_uploadanswers = (TextView) dialog.findViewById(R.id.custom_dialog_upload_answers);

        List<QuestionFormData> dl = QuestionFormDataORM.getQuestionFormDatalist(QuestionFormListActivity.this, data.get_formID());

        if(dl.size() <= 0){
            tv_addanswer.setEnabled(false);
            tv_viewquestion.setEnabled(false);
            tv_viewanswers.setEnabled(false);
            tv_downloadquestion.setEnabled(true);
            tv_uploadanswers.setEnabled(false);
        }else{
            tv_addanswer.setEnabled(true);
            tv_viewquestion.setEnabled(true);
            tv_viewanswers.setEnabled(true);
            tv_downloadquestion.setEnabled(true);
            tv_uploadanswers.setEnabled(true);
        }

        tv_addanswer.setOnClickListener(this);
        tv_viewquestion.setOnClickListener(this);
        tv_viewanswers.setOnClickListener(this);
        tv_downloadquestion.setOnClickListener(this);
        tv_uploadanswers.setOnClickListener(this);

    }

    private void showProgressOrNot(boolean isShow) {
        if (isShow) {
            progress.setVisibility(View.VISIBLE);
            progress_background.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.INVISIBLE);
            progress_background.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.custom_dialog_add_answer:
                Toast.makeText(QuestionFormListActivity.this, "Add Answer", Toast.LENGTH_SHORT).show();

                if(selectedData == null)
                    return;
                Intent answerintent = new Intent(QuestionFormListActivity.this, AnswerActivity.class);
                answerintent.putExtra("form_id", selectedData.get_formID());
                answerintent.putExtra("form_description", selectedData.get_formDescription());
                startActivity(answerintent);
                Toast.makeText(QuestionFormListActivity.this, "View Questions", Toast.LENGTH_SHORT).show();

                if(dialog != null || dialog.isShowing()){
                    dialog.dismiss();
                }

                break;

            case R.id.custom_dialog_view_questions:
                if(selectedData == null)
                    return;
                Intent questionformintent = new Intent(QuestionFormListActivity.this, QuestionFormActivity.class);
                questionformintent.putExtra("form_id", selectedData.get_formID());
                questionformintent.putExtra("form_description", selectedData.get_formDescription());
                startActivity(questionformintent);
                Toast.makeText(QuestionFormListActivity.this, "View Questions", Toast.LENGTH_SHORT).show();

                if(dialog != null || dialog.isShowing()){
                    dialog.dismiss();
                }

                break;

            case R.id.custom_dialog_view_answers:
                Toast.makeText(QuestionFormListActivity.this, "View Answers", Toast.LENGTH_SHORT).show();

                Intent answerlistintent = new Intent(QuestionFormListActivity.this, AnswerListActivity.class);
                answerlistintent.putExtra("form_id", selectedData.get_formID());
                answerlistintent.putExtra("form_description", selectedData.get_formDescription());
                startActivity(answerlistintent);

                break;

            case R.id.custom_dialog_download_form:
                Toast.makeText(QuestionFormListActivity.this, "Download Form", Toast.LENGTH_SHORT).show();


                if(dialog != null || dialog.isShowing()){
                    dialog.dismiss();
                }

                if(selectedData == null)
                    return;

                downloadQuestionForm(selectedData.get_formID());

                break;

            case R.id.custom_dialog_upload_answers:
                Toast.makeText(QuestionFormListActivity.this, "Upload Answers", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void downloadQuestionForm(String form_id){

        download_progress_dialog = new ProgressDialog(QuestionFormListActivity.this);
        download_progress_dialog.setCancelable(false);
        download_progress_dialog.setTitle("Downloading...");
        download_progress_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        download_progress_dialog.setMax(100);
        download_progress_dialog.show();

        new Thread(new Runnable() {
            public void run() {
                while (download_progress_increaseValue < 100) {
                    download_progress_increaseValue += 3;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            download_progress_dialog.setProgress(download_progress_increaseValue);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        RetrofitAPI.getInstance().getService().getFormDataByFormID(form_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {


                List<QuestionFormData> datalist = parseJSONToQuestionFormObject(s);
                if(download_progress_dialog != null){
                    download_progress_dialog.setProgress(95);
                }

                int result = QuestionFormDataORM.insertQuestionFormDataListtoDatabase(QuestionFormListActivity.this, datalist);

                //addDataToScrollView(datalist);

                //TODO to add loading progress with timeline
                if(download_progress_dialog != null){
                    download_progress_dialog.setProgress(100);
                    download_progress_dialog.dismiss();
                }

                Toast.makeText(QuestionFormListActivity.this, "Success "+ String.valueOf(result), Toast.LENGTH_SHORT).show();

                if(selectedData != null)
                    showChoiceDialog(selectedData);

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(QuestionFormListActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
                if(download_progress_dialog != null){
                    download_progress_dialog.dismiss();
                }
            }
        });

    }

    private List<QuestionFormData> parseJSONToQuestionFormObject(String s){
        try {
            JSONArray arr = new JSONArray(s);
            List<QuestionFormData> datalist = new ArrayList<>();
            int increaseValue = 80/arr.length();

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

                datalist.add(data);

                if(download_progress_dialog != null){
                    download_progress_dialog.setProgress(increaseValue);
                }

            }
            return datalist;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

}
