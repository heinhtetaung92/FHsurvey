package com.algo.hha.fhsurvey;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.algo.hha.fhsurvey.adapter.QuestionFormAdapter;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
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

    ListView question_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_form);

        question_listview = (ListView) findViewById(R.id.question_form_listview);

        List<List<QuestionFormData>> datalist = new ArrayList<>();

        QuestionFormAdapter adp = new QuestionFormAdapter(this, datalist);
        question_listview.setAdapter(adp);
        question_listview.setOnItemClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String formId = bundle.getString("form_id");
            if(formId != null){
                getDataFromServer(formId);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(QuestionFormActivity.this, "CLicked", Toast.LENGTH_SHORT).show();

    }

    private void getDataFromServer(String form_id){
        RetrofitAPI.getInstance().getService().getFormDataByFormID(form_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                List<List<QuestionFormData>> datalist = parseJSONToObject(s);
                addDataToListView(datalist);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(QuestionFormActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<List<QuestionFormData>> parseJSONToObject(String s){
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

                Log.i("Question DataList Size", String.valueOf(datalist.size()));
                Log.i("Question Main DataList Size", String.valueOf(maindatalist.size()));
                for(int x=0;x<maindatalist.size();x++){
                    Log.i("Question Child Size", String.valueOf(maindatalist.get(x).size()));
                }

            }
            return maindatalist;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



    private void addDataToListView(List<List<QuestionFormData>> dl){
        if(question_listview == null) return;

        QuestionFormAdapter adp = (QuestionFormAdapter) question_listview.getAdapter();
        adp.add(dl);
    }

}
