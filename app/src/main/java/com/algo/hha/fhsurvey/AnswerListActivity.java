package com.algo.hha.fhsurvey;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.adapter.AnswerListAdapter;
import com.algo.hha.fhsurvey.db.AnswerDataORM;
import com.algo.hha.fhsurvey.model.QuestionFormData;
import com.algo.hha.fhsurvey.model.UserData;

import java.util.ArrayList;
import java.util.List;


public class AnswerListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView listView;
    Toolbar mToolbar;
    String form_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);

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

        listView = (ListView) findViewById(R.id.answerlist_listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            form_id = bundle.getString("form_id");
            if(form_id != null){
                //getDataFromServer(formId);
                List<UserData> datalist = AnswerDataORM.getAnswerTimeStampList(this, form_id);
                AnswerListAdapter adp = new AnswerListAdapter(this, datalist);
                listView.setAdapter(adp);
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

        AnswerListAdapter adp = (AnswerListAdapter) listView.getAdapter();
        UserData data = (UserData) adp.getItem(position);

        Intent answerintent = new Intent(AnswerListActivity.this, ViewAnswerActivity.class);
        answerintent.putExtra("form_id", form_id);
        answerintent.putExtra("timestamp", data.get_timestamp());
        startActivity(answerintent);
        Toast.makeText(AnswerListActivity.this, "View Answer", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        MaterialDialog dialog = new MaterialDialog.Builder(AnswerListActivity.this)
                .title("Delete this answer?")
                .content("Are you sure?")
                .positiveText("YES")
                .negativeText("Cancel")
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        AnswerListAdapter adp = (AnswerListAdapter) listView.getAdapter();
                        UserData data = (UserData) adp.getItem(position);

                        AnswerDataORM.deleteDataFromTable(AnswerListActivity.this, form_id, data.get_timestamp());

                        List<UserData> datalist = AnswerDataORM.getAnswerTimeStampList(AnswerListActivity.this, form_id);
                        adp = new AnswerListAdapter(AnswerListActivity.this, datalist);
                        listView.setAdapter(adp);

                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                        dialog.dismiss();
                    }
                }).build();
        dialog.show();

        return true;
    }
}
