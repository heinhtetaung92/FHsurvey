package com.algo.hha.fhsurvey;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.adapter.AnswerListAdapter;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.db.AnswerDataORM;
import com.algo.hha.fhsurvey.model.ProjectFormData;
import com.algo.hha.fhsurvey.model.UserData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;


public class CSVListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView listView;
    Toolbar mToolbar;
    File[] datalist;

    String form_description = "", proj_name = "";

    List<Integer> checkedPositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csvlist);
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

        listView = (ListView) findViewById(R.id.csv_listview);
        /*listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener());
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                return true;
            }
        });*/

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("form_description") != null){
            form_description = bundle.getString("form_description");
            proj_name = bundle.getString("project_name");

            toolbarTitle.setText(form_description + "(" + proj_name + ")");

            datalist = sdCardHandler(form_description, proj_name);
            List<String> dl = new ArrayList<>();

            for(int i=0;i<datalist.length;i++){
                dl.add(datalist[i].getName());
            }

            if(datalist == null){
                //TODO it's null
            }else {
                ArrayAdapter<String> adp = new ArrayAdapter<String>(CSVListActivity.this, R.layout.custom_multimode_textview, dl);
                listView.setAdapter(adp);
            }

            //uploadFilesToServer();

        }



    }


    private File[] sdCardHandler(String form_description, String proj_name) {
        // SD Card path
        File root = android.os.Environment.getExternalStorageDirectory();
        File mainDirect = new File(root.getAbsolutePath() + "/FHSurvey/" + form_description + "(" + proj_name + ")");

        //File directory = QuestionFormListActivity.this.getDir("FHSurvey", Context.MODE_PRIVATE);

        // If Directory not exist then create
        if (mainDirect.exists()){
            return mainDirect.listFiles();
        }

        return null;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        MaterialDialog dialog = new MaterialDialog.Builder(CSVListActivity.this)
                .title("Upload to server")
                .content("Are you sure to upload \"" + datalist[position].getName() + "\"")
                .positiveText("Upload")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        uploadFilesToServer(datalist[position], position);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .build();
        dialog.show();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        MaterialDialog dialog = new MaterialDialog.Builder(CSVListActivity.this)
                .title("Delete this file?")
                .content("Are you sure?")
                .positiveText("YES")
                .negativeText("Cancel")
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        datalist[position].delete();
                        datalist = sdCardHandler(form_description, proj_name);
                        ArrayAdapter adp = (ArrayAdapter) listView.getAdapter();
                        adp.remove(adp.getItem(position));
                        adp.notifyDataSetChanged();

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

    private class MultiChoiceModeListener implements AbsListView.MultiChoiceModeListener{

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if(checked){
                checkedPositions.add(position);
            }else{
                int temppos = checkedPositions.indexOf(position);
                checkedPositions.remove(temppos);
            }
            Log.i("Listview check change", String.valueOf(checked));
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {



            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


    public void uploadFilesToServer(final File file, final int position){

        /*if(checkedPositions.size() <= 0){
            Toast.makeText(CSVListActivity.this, "Please choose at least one item to upload!", Toast.LENGTH_SHORT).show();
            return;
        }*/

        /*Map<String, TypedFile> files = new HashMap<String, TypedFile>();
        for(int j=0;j<datalist.length;j++){
            files.put("fileUpload" + j, new TypedFile("multipart/form-data", datalist[j]));
        }*/

        TypedFile typedFile = new TypedFile("multipart/form-data", datalist[0]);

        RetrofitAPI.getInstance().getService().uploadSingleFileToServer(typedFile, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(CSVListActivity.this, "Success", Toast.LENGTH_SHORT).show();

                file.delete();
                datalist = sdCardHandler(form_description, proj_name);
                ArrayAdapter adp = (ArrayAdapter) listView.getAdapter();
                adp.remove(adp.getItem(position));
                adp.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(CSVListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
