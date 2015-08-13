package com.algo.hha.fhsurvey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

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

        showDialogForChangingRoute();

        progressbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    public void checkFirstOpen()
    {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(Config.APP_PREFERENCE, MODE_PRIVATE);
        if (sharedpreferences.getBoolean(Config.IS_FIRST, true))
        {
            showDialogForChangingRoute();
            sharedpreferences.edit().putBoolean("isFirst", false).commit();
        }
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


                    RetrofitAPI.getInstance(LoginActivity.this).getService().signIn(username.getText().toString(), password.getText().toString(), new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(s);

                            if (!obj.isNull("UserID")) {
                                String userid = obj.getString("UserID");
                                SharedPreferences.Editor sPref = getApplicationContext().getSharedPreferences(Config.APP_PREFERENCE, 0).edit();
                                sPref.putBoolean(Config.WAS_LOGIN, true);
                                sPref.putString(Config.USERID, userid);
                                sPref.putString(Config.USERNAME, username.getText().toString());
                                sPref.commit();
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.INVISIBLE);
                                progressbackground.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {

                            progress.setVisibility(View.INVISIBLE);
                            progressbackground.setVisibility(View.INVISIBLE);

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



        }


    }

    private void setEnglishText() {
        login.setText("Login");
        register.setText("Register");
        //forgotpassword.setText("Forgot Password");
        usernametitle.setText("Username");
        passwordtitle.setText("Passsword");
    }

    public void showDialogForChangingRoute()
    {
        final SharedPreferences sPref = getSharedPreferences(Config.APP_PREFERENCE, MODE_PRIVATE);
        MaterialDialog materialdialog = (new com.afollestad.materialdialogs.MaterialDialog.Builder(this)).title("Server route").customView(R.layout.custom_dialog_changeroute, false).positiveText("Add").positiveColorRes(R.color.pink_500).negativeText("Cancel").negativeColorRes(R.color.grey_500).autoDismiss(false).callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {


            public void onNegative(MaterialDialog materialdialog1)
            {
                materialdialog1.dismiss();
            }

            public void onPositive(MaterialDialog materialdialog1)
            {
                EditText edittext = (EditText)materialdialog1.findViewById(R.id.custom_dialog_changeroute_et);
                android.content.SharedPreferences.Editor editor = sPref.edit();
                editor.putString(Config.SERVER_ROUTE, edittext.getText().toString());
                editor.commit();
                materialdialog1.dismiss();
            }

        }).build();

        ((EditText)materialdialog.findViewById(R.id.custom_dialog_changeroute_et)).setText(sPref.getString(Config.SERVER_ROUTE, "http://fhsurvey.osakaohshomyanmar.com"));
        materialdialog.show();
    }


}
