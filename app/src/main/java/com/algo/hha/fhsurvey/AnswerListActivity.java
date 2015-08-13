package com.algo.hha.fhsurvey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.adapter.AnswerListAdapter;
import com.algo.hha.fhsurvey.db.AnswerDataORM;
import com.algo.hha.fhsurvey.model.AnswerData;
import com.algo.hha.fhsurvey.model.UserData;
import com.algo.hha.fhsurvey.utility.Config;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class AnswerListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {


    private static final String CSV_HEADER[] = {
            "UserID", "QuestionID", "AnswerDescription", "AnswerID", "AnswerColumnID", "UploadBy", "Answerer"
    };
    List<Integer> checkedPositions;
    private File desFile;
    String form_desc_ee;
    String form_id;
    String formdescription;
    boolean isAlreadyExist;
    ListView listView;
    Toolbar mToolbar;
    String proj_desc_ee;
    String proj_id;
    String proj_name;

    public AnswerListActivity()
    {
        proj_name = "";
        proj_id = "";
        formdescription = "";
        proj_desc_ee = "";
        form_desc_ee = "";
        checkedPositions = new ArrayList();
        isAlreadyExist = false;
        desFile = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
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
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                                                @Override
                                                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                                                    mode.setTitle("FHSurvey");
                                                    int j = listView.getCheckedItemCount();
                                                    if (j == 1)
                                                    {
                                                        mode.setSubtitle((new StringBuilder()).append(j).append(" Item Selected").toString());
                                                    } else
                                                    {
                                                        mode.setSubtitle((new StringBuilder()).append(j).append(" Items Selected").toString());
                                                    }
                                                    if (checked)
                                                    {
                                                        checkedPositions.add(Integer.valueOf(position));
                                                        return;
                                                    } else
                                                    {
                                                        position = checkedPositions.indexOf(Integer.valueOf(position));
                                                        checkedPositions.remove(position);
                                                        return;
                                                    }
                                                }

                                                @Override
                                                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                                    AnswerListAdapter adp = (AnswerListAdapter)listView.getAdapter();
                                                    adp.setActionMode(true);
                                                    adp.notifyDataSetChanged();
                                                    menu.add(0, 2220, 0, "EXPORT").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                                                    menu.add(0, 2221, 1, "DELETE").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                                                    checkedPositions.clear();
                                                    return true;
                                                }

                                                @Override
                                                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                                                    switch (item.getItemId()){
                                                        case 2220:
                                                            exportSelectedAnswerToCSVFile(mode);
                                                            return true;

                                                        case 2221:

                                                            MaterialDialog dialog = new MaterialDialog.Builder(AnswerListActivity.this)
                                                                    .title("Are you sure?")
                                                                    .content("You are about to delete answers!")
                                                                    .positiveText("OK")
                                                                    .positiveColorRes(R.color.colorPrimary)
                                                                    .negativeText("Cancel")
                                                                    .negativeColorRes(R.color.grey_500)
                                                                    .autoDismiss(false)
                                                                    .callback(new MaterialDialog.ButtonCallback() {
                                                                        @Override
                                                                        public void onPositive(MaterialDialog dialog) {
                                                                            AnswerListAdapter adp = (AnswerListAdapter)listView.getAdapter();
                                                                            for (int i = 0; i < checkedPositions.size(); i++)
                                                                            {
                                                                                UserData userdata = (UserData) adp.getItem(checkedPositions.get(i));
                                                                                AnswerDataORM.deleteDataFromTable(AnswerListActivity.this, form_id, userdata.get_timestamp());
                                                                            }

                                                                            List<UserData> userDataList = AnswerDataORM.getAnswerTimeStampList(AnswerListActivity.this, form_id);
                                                                            adp = new AnswerListAdapter(AnswerListActivity.this,userDataList);
                                                                            listView.setAdapter(adp);
                                                                            mode.finish();
                                                                            dialog.dismiss();
                                                                        }

                                                                        @Override
                                                                        public void onNegative(MaterialDialog dialog) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).build();
                                                            dialog.show();
                                                            dialog.dismiss();

                                                            return true;
                                                    }

                                                    return false;
                                                }

                                                @Override
                                                public void onDestroyActionMode(ActionMode mode) {
                                                    AnswerListAdapter adp = (AnswerListAdapter)listView.getAdapter();
                                                    adp.setActionMode(false);
                                                    adp.notifyDataSetChanged();
                                                }
                                            });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            proj_id = bundle.getString("proj_id");
            proj_name = bundle.getString("proj_name");
            form_id = bundle.getString("form_id");
            proj_desc_ee = bundle.getString("proj_description_ee");
            form_desc_ee = bundle.getString("form_description_ee");

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

    private void addData(UserData userdata, ActionMode actionmode)
    {
        HashMap hashmap = new HashMap();
        List list = AnswerDataORM.getOnlyAnswerDatalist(this, form_id, userdata.get_timestamp());
        SharedPreferences sharedpreferences = getSharedPreferences(Config.APP_PREFERENCE, 0);
        for (int i = 0; i < list.size(); i++)
        {
            hashmap.put(CSV_HEADER[0], ((AnswerData)list.get(i)).get_userid());
            hashmap.put(CSV_HEADER[1], ((AnswerData)list.get(i)).get_QuestionID());
            hashmap.put(CSV_HEADER[2], ((AnswerData)list.get(i)).get_VALUE());
            hashmap.put(CSV_HEADER[3], ((AnswerData)list.get(i)).get_AnswerID());
            hashmap.put(CSV_HEADER[4], ((AnswerData)list.get(i)).get_AnswerColumnID());
            hashmap.put(CSV_HEADER[5], sharedpreferences.getString(Config.USERID, ""));
            hashmap.put(CSV_HEADER[6], ((AnswerData)list.get(i)).get_answerer());
            writeDataOnCSV(userdata, hashmap);
        }

        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        isAlreadyExist = false;
        actionmode.finish();
    }
    private CellProcessor[] getProcessors()
    {
        return (new CellProcessor[] {
                new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()
        });
    }

    private void sdCardHandler(UserData userdata, ActionMode actionmode)
    {
        Object obj = Environment.getExternalStorageDirectory();
        obj = new File((new StringBuilder()).append(((File) (obj)).getAbsolutePath()).append("/FHSurvey").toString());
        if (!((File) (obj)).exists() && !((File) (obj)).mkdir())
        {
            Toast.makeText(this, "Cannot access SDCard!", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File((new StringBuilder()).append(((File) (obj)).getAbsolutePath()).append("/").append(form_id).append("(").append(proj_id).append(")").toString());
        if (!file.exists() && !file.mkdir())
        {
            Toast.makeText(this, "Cannot access SDCard!", Toast.LENGTH_SHORT).show();
            return;
        }
        obj = userdata.get_timestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(((String) (obj))));
        StringBuilder stringbuilder = new StringBuilder();
        StringBuilder stringbuilder1 = new StringBuilder();
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
        {
            obj = "0";
        } else
        {
            obj = "";
        }
        stringbuilder.append(stringbuilder1.append(((String) (obj))).append(calendar.get(Calendar.DAY_OF_MONTH)).toString());
        stringbuilder1 = new StringBuilder();
        if (calendar.get(Calendar.MONTH) < 10)
        {
            obj = "0";
        } else
        {
            obj = "";
        }
        stringbuilder.append(stringbuilder1.append(((String) (obj))).append(calendar.get(Calendar.MONTH)).toString());
        stringbuilder.append(calendar.get(Calendar.YEAR));
        stringbuilder.append(calendar.get(Calendar.HOUR_OF_DAY));
        stringbuilder.append(calendar.get(Calendar.MINUTE));
        stringbuilder.append(calendar.get(Calendar.SECOND));

        Log.i("AnswerList Form Proj description", proj_desc_ee);
        Log.i("AnswerList Form Form description", form_desc_ee);
        desFile = new File((new StringBuilder()).append(file).append("/").append(userdata.get_username().trim().toString()).append("-").append(proj_desc_ee).append("-").append(form_desc_ee).append("-").append(stringbuilder.toString()).append(".csv").toString());
        if (desFile.exists());
        addData(userdata, actionmode);
    }

    private void writeDataOnCSV(UserData userdata, HashMap<String, Object> ANSWER)
    {
        ICsvMapWriter mapWriter = null;
        try {
            mapWriter = new CsvMapWriter(new FileWriter(desFile, true),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getProcessors();

            // write the header
            if (isAlreadyExist)
                mapWriter.writeHeader(CSV_HEADER);


            mapWriter.write(ANSWER, CSV_HEADER, processors);

            //TODO check file is exist or not if exist return true, or return false
            //AnswerDataORM.deleteDataFromTable(AnswerListActivity.this, form_id, userdata.get_timestamp());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (mapWriter != null) {
                try {
                    mapWriter.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportSelectedAnswerToCSVFile(ActionMode actionmode)
    {
        Object obj = AnswerDataORM.getAnswerTimeStampList(this, form_id, proj_id);
        ArrayList arraylist = new ArrayList();
        for (int i = 0; i < checkedPositions.size(); i++)
        {
            arraylist.add(((List) (obj)).get(((Integer)checkedPositions.get(i)).intValue()));
        }

        if (arraylist.size() <= 0)
        {
            Toast.makeText(this, "There is no answer to export!", Toast.LENGTH_SHORT).show();
            return;
        }
        obj = new ProgressDialog(this);
        ((ProgressDialog) (obj)).setCancelable(false);
        ((ProgressDialog) (obj)).setTitle("Exporting Answers");
        ((ProgressDialog) (obj)).setProgressStyle(1);
        ((ProgressDialog) (obj)).setMax(arraylist.size());
        ((ProgressDialog) (obj)).show();
        for (int j = 0; j < arraylist.size(); j++)
        {
            isAlreadyExist = false;
            sdCardHandler((UserData)arraylist.get(j), actionmode);
            ((ProgressDialog) (obj)).setProgress(j + 1);
        }

        ((ProgressDialog) (obj)).dismiss();
    }

}
