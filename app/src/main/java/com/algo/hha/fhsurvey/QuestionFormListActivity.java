package com.algo.hha.fhsurvey;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.algo.hha.fhsurvey.adapter.ProjectFormListAdapter;
import com.algo.hha.fhsurvey.adapter.QuestionFormAdapter;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.model.ProjectFormData;
import com.algo.hha.fhsurvey.model.QuestionFormData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class QuestionFormListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ListView form_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_form_list);

        form_list = (ListView) findViewById(R.id.question_form_data_listview);

        //hardcode data for project list
        List<ProjectFormData> datalist = new ArrayList<>();
        ProjectFormListAdapter adp = new ProjectFormListAdapter(this, datalist);
        form_list.setAdapter(adp);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String proj_id = bundle.getString("project_id");
            if(proj_id != null){
                getDataFromServer(proj_id);
            }
        }

        form_list.setOnItemClickListener(this);
    }

    private void getDataFromServer(String proj_id){
        RetrofitAPI.getInstance().getService().getFormListByProjectID(proj_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                List<ProjectFormData> datalist = parseJSONToObject(s);
                addDataToListView(datalist);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(QuestionFormListActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<ProjectFormData> parseJSONToObject(String s){
        try {
            /*JSONArray arr = new JSONArray(s);

            for(int i=0;i<arr.length();i++){*/
                List<ProjectFormData> datalist = new ArrayList<>();
                ProjectFormData data = new ProjectFormData();
                JSONObject obj = new JSONObject(s);

                if(!obj.isNull("FormID")){
                    data.set_formID(obj.getString("FormID"));
                }

                if(!obj.isNull("ProjectID")){
                    data.set_projectID(obj.getString("ProjectID"));
                }

                if(!obj.isNull("FormDescription")){
                    data.set_formDescription(obj.getString("FormDescription"));
                }

                if(!obj.isNull("FormIndex")){
                    data.set_formIndex(obj.getString("FormIndex"));
                }

                if(!obj.isNull("Status")){
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

    private void addDataToListView(List<ProjectFormData> dl){
        if(form_list == null) return;

        ProjectFormListAdapter adp = (ProjectFormListAdapter) form_list.getAdapter();
        adp.add(dl);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProjectFormListAdapter adp = (ProjectFormListAdapter) form_list.getAdapter();
        ProjectFormData data = (ProjectFormData) adp.getItem(position);

        Intent intent = new Intent(QuestionFormListActivity.this, QuestionFormActivity.class);
        intent.putExtra("form_id", data.get_formID());
        startActivity(intent);
    }

}
