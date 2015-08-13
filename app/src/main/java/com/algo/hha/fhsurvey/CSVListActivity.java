package com.algo.hha.fhsurvey;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.algo.hha.fhsurvey.adapter.CheckableArrayAdapter;
import com.algo.hha.fhsurvey.api.RetrofitAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class CSVListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView listView;
    Toolbar mToolbar;
    File[] datalist;

    String form_description = "", proj_name = "", form_id = "", proj_id = "";

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
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle("FHSurvey");
                int k = listView.getCheckedItemCount();
                if (k == 1)
                {
                    mode.setSubtitle((new StringBuilder()).append(k).append(" Item Selected").toString());
                } else
                {
                    mode.setSubtitle((new StringBuilder()).append(k).append(" Items Selected").toString());
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
            public boolean onCreateActionMode(ActionMode actionmode, Menu menu) {
                CheckableArrayAdapter adp = (CheckableArrayAdapter)listView.getAdapter();
                adp.setActionMode(true);
                adp.notifyDataSetChanged();
                menu.add(0, 1110, 1, "DELETE").setShowAsAction(1);
                menu.add(0, 1111, 0, "UPLOAD").setShowAsAction(1);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                switch (item.getItemId()){
                    case 1110:

                        MaterialDialog.Builder builder = new MaterialDialog.Builder(CSVListActivity.this);
                                builder.title("Are you sure?");

                        int j = listView.getCheckedItemCount();
                        StringBuilder stringbuilder = (new StringBuilder()).append("You are about to delete ").append(j);
                        if (j == 1)
                        {
                            stringbuilder.append(" file");
                        } else
                        {
                            stringbuilder.append(" files");
                        }
                        builder.content(stringbuilder.toString());

                        builder.positiveText("YES").negativeText("Cancel").autoDismiss(false).callback(new MaterialDialog.ButtonCallback() {

                            public void onNegative(MaterialDialog materialdialog) {
                                materialdialog.dismiss();
                            }

                            public void onPositive(MaterialDialog materialdialog) {
                                materialdialog.dismiss();
                                materialdialog = createLoadingDialog();
                                materialdialog.show();
                                int i;
                                for (Iterator iterator = checkedPositions.iterator(); iterator.hasNext(); datalist[i].delete()) {
                                    i = ((Integer) iterator.next()).intValue();
                                }

                                datalist = sdCardHandler(form_description, proj_name, form_id, proj_id);
                                List<String> dl = new ArrayList();
                                for (int j = 0; j < datalist.length; j++) {
                                    dl.add(datalist[j].getName());
                                }

                                if (datalist != null) {
                                    CheckableArrayAdapter adp = new CheckableArrayAdapter(CSVListActivity.this, dl);
                                    listView.setAdapter(adp);
                                }
                                materialdialog.dismiss();
                                Toast.makeText(CSVListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                checkedPositions.clear();
                                mode.finish();
                            }
                        });

                        builder.show();

                        break;

                    case 1111:
                        break;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                CheckableArrayAdapter adp = (CheckableArrayAdapter)listView.getAdapter();
                adp.setActionMode(false);
                adp.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(this);
        //listView.setOnItemLongClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("form_description") != null){
            form_description = bundle.getString("form_description");
            proj_name = bundle.getString("project_name");
            form_id = bundle.getString("form_id");
            proj_id = bundle.getString("proj_id");

            toolbarTitle.setText(form_description + "(" + proj_name + ")");

            datalist = sdCardHandler(form_description, proj_name, form_id, proj_id);
            List<String> dl = new ArrayList<>();


            if(datalist == null){
                //TODO it's null
            }else {
                for(int i=0;i<datalist.length;i++){
                    dl.add(datalist[i].getName());
                }

                CheckableArrayAdapter adp = new CheckableArrayAdapter(CSVListActivity.this, dl);
                listView.setAdapter(adp);
            }

            //uploadFilesToServer();

        }



    }

    public MaterialDialog createLoadingDialog()
    {
        return (new com.afollestad.materialdialogs.MaterialDialog.Builder(this)).content("Loading...").build();
    }


    private File[] sdCardHandler(String form_description, String proj_name, String form_id, String proj_id) {
        // SD Card path
        File root = android.os.Environment.getExternalStorageDirectory();
        File mainDirect = new File(root.getAbsolutePath() + "/FHSurvey/" + form_id + "(" + proj_id + ")");

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
                .negativeText("DELETE")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        uploadFilesToServer(datalist[position], position);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        datalist[position].delete();
                        datalist = sdCardHandler(form_description, proj_name, form_id, proj_id);
                        ArrayAdapter adp = (ArrayAdapter) listView.getAdapter();
                        adp.remove(adp.getItem(position));
                        adp.notifyDataSetChanged();

                        dialog.dismiss();
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
                        datalist = sdCardHandler(form_description, proj_name, form_id, proj_id);
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

        RetrofitAPI.getInstance(CSVListActivity.this).getService().uploadSingleFileToServer(typedFile, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Toast.makeText(CSVListActivity.this, "Success", Toast.LENGTH_SHORT).show();

                file.delete();
                datalist = sdCardHandler(form_description, proj_name, form_id, proj_id);
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
