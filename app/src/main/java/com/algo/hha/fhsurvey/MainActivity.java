package com.algo.hha.fhsurvey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.algo.hha.fhsurvey.adapter.ProjectListAdapter;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.model.ProjectData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ListView project_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        project_listview = (ListView) findViewById(R.id.project_listview);
        project_listview.setOnItemClickListener(this);

        List<ProjectData> datalist = new ArrayList<>();
        ProjectListAdapter adp = new ProjectListAdapter(this, datalist);
        project_listview.setAdapter(adp);

        getDataFromServer();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ProjectListAdapter adp = (ProjectListAdapter) project_listview.getAdapter();
        ProjectData data = (ProjectData) adp.getItem(position);

        Intent intent = new Intent(MainActivity.this, QuestionFormListActivity.class);
        intent.putExtra("project_id", data.get_projectID());
        startActivity(intent);
    }


    private void getDataFromServer(){
        RetrofitAPI.getInstance().getService().getProjectsByURL(new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                List<ProjectData> datalist = parseJSONToObject(s);
                addDataToListView(datalist);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<ProjectData> parseJSONToObject(String s){
        try {
            JSONArray arr = new JSONArray(s);
            List<ProjectData> datalist = new ArrayList<>();
            for(int i=0;i<arr.length();i++){
                ProjectData data = new ProjectData();
                JSONObject obj = arr.getJSONObject(i);

                if(!obj.isNull("ProjectID")){
                    data.set_projectID(obj.getString("ProjectID"));
                }

                if(!obj.isNull("ProjectName")){
                    data.set_projectName(obj.getString("ProjectName"));
                }

                if(!obj.isNull("Description")){
                    data.set_description(obj.getString("Description"));
                }

                if(!obj.isNull("ProjectStatus")){
                    data.set_projectStatus(obj.getString("ProjectStatus"));
                }

                if(!obj.isNull("StartDate")){
                    data.set_startDate(obj.getString("StartDate"));
                }

                if(!obj.isNull("CompleteDate")){
                    data.set_completeDate(obj.getString("CompleteDate"));
                }

                if(!obj.isNull("ExpireDate")){
                    data.set_expireDate(obj.getString("ExpireDate"));
                }

                if(!obj.isNull("Status")){
                    data.set_status(obj.getString("Status"));
                }
                datalist.add(data);

            }

            return datalist;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addDataToListView(List<ProjectData> dl){
        if(project_listview == null) return;

        ProjectListAdapter adp = (ProjectListAdapter) project_listview.getAdapter();
        adp.add(dl);
    }


    /*    "ProjectID": "8b0cf458-a595-4a3e-996f-fc1be699520f",
            "ProjectName": "Field survey WIN’S",
            "Description": "Test Project",
            "ProjectStatus": 1,
            "StartDate": "2015-06-15T00:00:00",
            "CompleteDate": "2015-12-15T00:00:00",
            "ExpireDate": "2015-07-01T04:51:58.6535667-05:00",
            "Status": 1
    */

}
