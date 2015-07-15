package com.algo.hha.fhsurvey;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.utility.Connection;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    EditText et_email, et_pwd, et_pwd_conf, et_name, et_contactno;
    TextView register_button;

    View progress, progress_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email = (EditText) findViewById(R.id.register_et_email);
        et_pwd = (EditText) findViewById(R.id.register_et_pwd);
        et_pwd_conf = (EditText) findViewById(R.id.register_et_pwd_confirm);
        et_name = (EditText) findViewById(R.id.register_et_name);
        et_contactno = (EditText) findViewById(R.id.register_et_contactno);

        register_button = (TextView) findViewById(R.id.register_button);

        progress = findViewById(R.id.register_progress_wheel);
        progress_background = findViewById(R.id.register_progress_wheel_background);

        register_button.setOnClickListener(this);

    }




    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.register_button:

                if(!Connection.isOnline(RegisterActivity.this)){
                    MaterialDialog dialog = new MaterialDialog.Builder(RegisterActivity.this)
                            .title("Connection is loss")
                            .content("Please check network is on!")
                            .positiveText("OK")
                            .positiveColor(R.color.colorPrimary)
                            .positiveColorRes(R.color.colorPrimary)
                            .build();
                    dialog.show();
                }
                else if(TextUtils.isEmpty(et_email.getText().toString()) ||
                   TextUtils.isEmpty(et_pwd.getText().toString()) ||
                   TextUtils.isEmpty(et_pwd_conf.getText().toString()) ||
                   TextUtils.isEmpty(et_name.getText().toString()) ||
                   TextUtils.isEmpty(et_contactno.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }else{

                    showProgress();
                    registerToServer(et_email.getText().toString(),
                            et_pwd.getText().toString(),
                            et_pwd_conf.getText().toString(),
                            et_name.getText().toString(),
                            et_contactno.getText().toString());
                }

                break;
        }

    }

    private void registerToServer(String email, String pwd, String pwd_conf, String name, String contactno){

        RetrofitAPI.getInstance().getService().registerToServer(name, contactno, email, pwd, pwd_conf,
                new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        finish();
                        dismissProgress();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        /*if (error.getBody() == null) {
                            Toast.makeText(RegisterActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                        } else {

                            String errmsg = error.getBody().toString();

                            Log.i("Error", errmsg);
                            try {
                                JSONObject obj = new JSONObject(errmsg);
                                JSONArray errorarray = obj.getJSONArray("errors");
                                String errormsg = "Connection Error";
                                for(int i=0;i<errorarray.length();i++){
                                    errormsg = errorarray.getString(0);
                                    break;
                                }
                                Toast.makeText(RegisterActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }*/

                        Toast.makeText(RegisterActivity.this, "Error in register!", Toast.LENGTH_SHORT).show();
                        dismissProgress();
                    }
                });

    }

    private void showProgress(){
        progress.setVisibility(View.VISIBLE);
        progress_background.setVisibility(View.VISIBLE);
    }

    private void dismissProgress(){
        progress.setVisibility(View.INVISIBLE);
        progress_background.setVisibility(View.INVISIBLE);
    }


}
