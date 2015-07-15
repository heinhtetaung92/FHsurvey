package com.algo.hha.fhsurvey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.utility.Config;
import com.algo.hha.fhsurvey.utility.Connection;
import com.pnikosis.materialishprogress.ProgressWheel;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {


    TextView register, usernametitle, passwordtitle;
    Button login;
    EditText username, password;
    ProgressWheel progress;
    View progressbackground;


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        login = (Button) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.login_register);
        //forgotpassword = (TextView) findViewById(R.id.login_forgotpassword);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);

        usernametitle = (TextView) findViewById(R.id.login_email_label);
        passwordtitle = (TextView) findViewById(R.id.login_password_label);

        username.setText("df628282-383b-4476-bd49-ef822bd30b61");
        password.setText("ll");

        progressbackground = findViewById(R.id.login_progresswheel_background);
        progress = (ProgressWheel) findViewById(R.id.progress_wheel);


        progressbackground.bringToFront();
        progress.bringToFront();
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        //forgotpassword.setOnClickListener(this);

        setEnglishText();

        SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.APP_PREFERENCE, MODE_PRIVATE);
        if (sPref.getBoolean(Config.WAS_LOGIN, false)) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        progressbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_button:

                if (username.getText().toString().equals("") || password.getText().toString().equals("")) {

                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("Login")
                            .content("Fill User Name and Password!")
                            .positiveText("OK")
                            .positiveColor(R.color.colorPrimary)
                            .positiveColorRes(R.color.colorPrimary)
                            .build();
                    dialog.show();


                } else if (Connection.isOnline(this)) {

                    progress.setVisibility(View.VISIBLE);
                    progressbackground.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

//                    String uniquekey = Build.SERIAL + android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    //                          android.provider.Settings.Secure.ANDROID_ID);


                    RetrofitAPI.getInstance().getService().signIn(username.getText().toString(), password.getText().toString(), new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {


                            SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.APP_PREFERENCE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sPref.edit();

                            //TODO have to remove in real mode
                            //editor.putBoolean(Config.WAS_LOGIN, true).commit();

                            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();


                            progress.setVisibility(View.INVISIBLE);
                            progressbackground.setVisibility(View.INVISIBLE);


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        }

                        @Override
                        public void failure(RetrofitError error) {

                            progress.setVisibility(View.INVISIBLE);
                            progressbackground.setVisibility(View.INVISIBLE);

                                /*if (error.getBody() == null) {
                                    Toast.makeText(LoginActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(LoginActivity.this, errormsg, Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }*/

                            Toast.makeText(LoginActivity.this, "Signin Error!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                    MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
                            .title("")
                            .content("Connection is loss!")
                            .positiveText("OK")
                            .positiveColor(R.color.colorPrimary)
                            .positiveColorRes(R.color.colorPrimary)
                            .build();
                    dialog.show();


                }


                break;

            case R.id.login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;


            /*case R.id.login_forgotpassword:




                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("")
                        .backgroundColorRes(R.color.primary)
                        .customView(R.layout.forgot_email_layout, true)
                        .positiveText("SEND")
                        .positiveColor(R.color.white)
                        .positiveColorRes(R.color.white)
                        .typeface("ciclefina", "ciclegordita")
                        .callback(new MaterialDialog.ButtonCallback() {
                                      @Override
                                      public void onPositive(final MaterialDialog dialog) {
                                          super.onPositive(dialog);

                                          EditText et_email = (EditText) dialog.findViewById(R.id.et_email_forgotpwd);

                                          if (!et_email.getText().toString().equals("")) {

                                              AvaliableJobsAPI.getInstance().getService().forgetPassword(et_email.getText().toString(),
                                                      new Callback<String>() {
                                                          @Override
                                                          public void success(String s, Response response) {
                                                              Toast.makeText(LoginActivity.this, "We will send to your email soon", Toast.LENGTH_SHORT).show();
                                                          }

                                                          @Override
                                                          public void failure(RetrofitError error) {
                                                              if (error.getBody() == null) {
                                                                  Toast.makeText(LoginActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                              } else {

                                                                  String errmsg = error.getBody().toString();
                                                                  String errcode = "";


                                                                  try {
                                                                      JSONObject errobj = new JSONObject(errmsg);

                                                                      errcode = errobj.getJSONObject("err").getString("message");

                                                                      Toast.makeText(LoginActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                                                  } catch (JSONException e) {
                                                                      e.printStackTrace();
                                                                  }



                                                              }
                                                          }
                                                      });

                                          } else {
                                              Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                                          }


                                          dialog.dismiss();
                                      }

                                      @Override
                                      public void onNegative(MaterialDialog dialog) {
                                          super.onNegative(dialog);

                                          dialog.dismiss();

                                      }
                                  }

                        )
                        .build();


                dialog.show();
                EditText et_email = (EditText) dialog.findViewById(R.id.et_email_forgotpwd);
                TextView message = (TextView) dialog.findViewById(R.id.et_email_forgotpwd_message);
                message.setText("Please send your registered email to us. We will send password to your email.");

                break;*/
        }


    }

    private void setEnglishText() {
        login.setText("Login");
        register.setText("Register");
        //forgotpassword.setText("Forgot Password");
        usernametitle.setText("Username");
        passwordtitle.setText("Passsword");
    }


}
