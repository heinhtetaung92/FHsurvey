package com.algo.hha.fhsurvey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.adapter.ProjectListAdapter;
import com.algo.hha.fhsurvey.api.APIConfig;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.db.ProjectDataORM;
import com.algo.hha.fhsurvey.model.ProjectData;
import com.algo.hha.fhsurvey.utility.Config;
import com.algo.hha.fhsurvey.utility.Connection;
import com.pnikosis.materialishprogress.ProgressWheel;

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
    Toolbar mToolbar;

    ProgressWheel progress;
    View progress_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        toolbarTitle.setText("FHSurvey");
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        project_listview = (ListView) findViewById(R.id.project_listview);
        project_listview.setOnItemClickListener(this);

        progress = (ProgressWheel) findViewById(R.id.projectlist_progress_wheel);
        progress_background = findViewById(R.id.projectlist_progress_wheel_background);

        showProgressOrNot(true);

        List<ProjectData> datalist = ProjectDataORM.getProjectDatalist(this);
        ProjectListAdapter adp = new ProjectListAdapter(this, datalist);
        project_listview.setAdapter(adp);

        if(datalist.size() > 0){
            showProgressOrNot(false);
        }

        if(Connection.isOnline(this)) {
            SharedPreferences sPref = getSharedPreferences(Config.APP_PREFERENCE, MODE_PRIVATE);

            getDataFromServer(sPref.getString(Config.USERID, ""));
        }else{
            if(datalist.size() <= 0) {
                Toast.makeText(this, "Network is not available!", Toast.LENGTH_SHORT).show();
                showProgressOrNot(false);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ProjectListAdapter adp = (ProjectListAdapter) project_listview.getAdapter();
        ProjectData data = (ProjectData) adp.getItem(position);

        Intent intent = new Intent(MainActivity.this, QuestionFormListActivity.class);
        intent.putExtra("project_name", data.get_projectName());
        intent.putExtra("project_id", data.get_projectID());
        intent.putExtra("project_desc_ee", data.get_projectName_EE());
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    private void getDataFromServer(String user_id){
        RetrofitAPI.getInstance(MainActivity.this).getService().getProjectByUserID(user_id, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                List<ProjectData> datalist = parseJSONToObject(s);
                addDataToListView(datalist);
                ProjectDataORM.insertProjectDataListtoDatabase(MainActivity.this, datalist);
                showProgressOrNot(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, "Cannot reach to server", Toast.LENGTH_SHORT).show();
                showProgressOrNot(false);
            }
        });
    }

    public void logOut()
    {
        SharedPreferences.Editor editor = getSharedPreferences(Config.APP_PREFERENCE, MODE_PRIVATE).edit();
        editor.putString(Config.WAS_LOGIN, null);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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

                if (!obj.isNull("ProjectName_EE"))
                {
                    data.set_projectName_EE(obj.getString("ProjectName_EE"));
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

    private void showProgressOrNot(boolean isShow){
        if(isShow){
            progress.setVisibility(View.VISIBLE);
            progress_background.setVisibility(View.VISIBLE);
        }else{
            progress.setVisibility(View.INVISIBLE);
            progress_background.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item = menu.add(0, 1000, 100, "LogOut");
        MenuItem item1 = menu.add(0, 1001, 101, "Change Route");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1000){
            logOut();
        }else if(item.getItemId() == 1001){
            showDialogForChangingRoute();
        }

        return super.onOptionsItemSelected(item);
    }
    public void showDialogForChangingRoute()
    {
        final SharedPreferences sPref = getSharedPreferences(Config.APP_PREFERENCE, 0);
        MaterialDialog materialdialog = (new com.afollestad.materialdialogs.MaterialDialog.Builder(this)).title("Change route").customView(R.layout.custom_dialog_changeroute, false).positiveText("Change").positiveColorRes(R.color.colorPrimary).negativeText("Cancel").negativeColorRes(R.color.grey_500).autoDismiss(false).callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {

            public void onNegative(MaterialDialog materialdialog1)
            {
                materialdialog1.dismiss();
            }

            public void onPositive(MaterialDialog materialdialog1)
            {
                EditText edittext = (EditText)materialdialog1.findViewById(R.id.customdialog_changeroute);
                android.content.SharedPreferences.Editor editor = sPref.edit();
                editor.putString(Config.SERVER_ROUTE, edittext.getText().toString());
                editor.commit();
                materialdialog1.dismiss();
            }

        }).build();
        materialdialog.show();
        ((EditText)materialdialog.findViewById(R.id.customdialog_changeroute)).setText(sPref.getString(Config.SERVER_ROUTE, APIConfig.BASE_URL));
    }


}
