// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.algo.hha.fhsurvey;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.algo.hha.fhsurvey.api.RetrofitAPI;
import com.algo.hha.fhsurvey.utility.Connection;
import com.algo.hha.fhsurvey.utility.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterActivity extends ActionBarActivity
    implements View.OnClickListener, DatePickerDialog.OnDateSetListener
{
    public class AppsAdapter extends BaseAdapter
    {

        List<String> mImageList;

        public AppsAdapter(List<String> imgl){
            mImageList = imgl;
        }

        public final int getCount()
        {
            return mImageList.size();
        }

        public final Object getItem(int i)
        {
            return mImageList.get(i);
        }

        public final long getItemId(int i)
        {
            return (long)i;
        }

        public View getView(int i, View view, ViewGroup viewgroup)
        {
            ViewHolder vh = new ViewHolder();
            if (view == null)
            {
                vh = new ViewHolder();
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_selectorgrid, null);
                vh.frameLayout = (LinearLayout)view;
                vh.imageView = (ImageView)view.findViewById(R.id.grid_image);
                view.setTag(vh);
            } else
            {
                vh = (ViewHolder)view.getTag();
            }
            Log.i("Image url", mImageList.get(i));

            if (mImageList.get(i).contains("/storage")) {

                //Uri uri = Uri.fromFile(new File(mImageList.get(i)));
                //Log.i("Uri String", uri.toString());
                /*//hack code for lolipop version
                if (Build.VERSION.SDK_INT == 22) {
                    if (uri != null && uri.toString().length() > 0) {
                        try {
                            final String extractUriFrom = uri.toString();
                            String firstExtraction = extractUriFrom.contains("com.google.android.apps.photos.contentprovider") ? extractUriFrom.split("/1/")[1] : extractUriFrom;
                            firstExtraction = firstExtraction.contains("/ACTUAL") ? firstExtraction.replace("/ACTUAL", "").toString() : firstExtraction;

                            String secondExtraction = URLDecoder.decode(firstExtraction, "UTF-8");
                            uri = Uri.parse(secondExtraction);
                        } catch (UnsupportedEncodingException e) {

                        } catch (Exception e) {

                        }
                    }
                }*/

                    Picasso.with(RegisterActivity.this)
                            .load("file://"+mImageList.get(i))
                            .resize(512, 512)
                            .centerCrop()
                            .into(vh.imageView);

            } else {

                Picasso.with(RegisterActivity.this)
                        .load(mImageList.get(i))
                        .into(vh.imageView);
            }

            return view;
        }

        private class ViewHolder{
            LinearLayout frameLayout;
            ImageView imageView;
        }

    }

    Bitmap bitmap;
    EditText et_address;
    EditText et_dateofbirth;
    EditText et_jobtitle;
    EditText et_organization;
    EditText et_pwd;
    EditText et_username;
    RoundedImageView profileImage;
    View progress;
    View progress_background;
    TextView register_button;

    public RegisterActivity()
    {
        bitmap = null;
    }

    private void dismissProgress()
    {
        progress.setVisibility(View.INVISIBLE);
        progress_background.setVisibility(View.INVISIBLE);
    }
    /* String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()


            };*/
    private List<String> getListofImage() {


        List<String> imgList = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media.DATA};

        // content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.i("ListingImages", " query count=" + cur.getCount());

        if (cur.moveToFirst()) {

            int column_index = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            do {
                // Get the field values
                cur.getString(column_index);

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + cur.getString(column_index)
                        + "  date_taken=" + "");
                imgList.add(cur.getString(column_index));
            } while (cur.moveToNext());

        }

        return imgList;
    }
    private void registerToServer(String s, String s1, String s2, String s3, String s4, String s5)
    {
        byte abyte0[] = new byte[0];
        if (bitmap != null)
        {
            abyte0 = getBytesFromBitmap(bitmap);
        }
        RetrofitAPI.getInstance(RegisterActivity.this).getService().registerToServer(s, s1, abyte0.toString(), s2, s3, s4, s5, new Callback<String>(){

            @Override
            public void success(String s, Response response) {
                Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                finish();
                dismissProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(RegisterActivity.this, "Error in register!", Toast.LENGTH_SHORT).show();
                dismissProgress();
            }
        });


    }

    private void showDatePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "Datepickerdialog");
    }

    private void showProgress()
    {
        progress.setVisibility(View.VISIBLE);
        progress_background.setVisibility(View.VISIBLE);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap1)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 70, bytearrayoutputstream);
        return bytearrayoutputstream.toByteArray();
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        default:
            return;

        case R.id.register_button:
            break;
        }
        if (!Connection.isOnline(this))
        {
            (new MaterialDialog.Builder(this)).title("Connection is loss").content("Please check network is on!").positiveText("OK").positiveColorRes(R.color.colorPrimary).build().show();
            return;
        }
        if (TextUtils.isEmpty(et_username.getText().toString()) || TextUtils.isEmpty(et_pwd.getText().toString()) || TextUtils.isEmpty(et_dateofbirth.getText().toString()) || TextUtils.isEmpty(et_organization.getText().toString()) || TextUtils.isEmpty(et_jobtitle.getText().toString()) || TextUtils.isEmpty(et_address.getText().toString()))
        {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        } else
        {
            showProgress();
            registerToServer(et_username.getText().toString(), et_pwd.getText().toString(), et_dateofbirth.getText().toString(), et_organization.getText().toString(), et_jobtitle.getText().toString(), et_address.getText().toString());
            return;
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_register);
        et_username = (EditText)findViewById(R.id.register_et_name);
        et_pwd = (EditText)findViewById(R.id.register_et_pwd);
        et_dateofbirth = (EditText)findViewById(R.id.register_et_dateofbirth);
        et_organization = (EditText)findViewById(R.id.register_et_organization);
        et_jobtitle = (EditText)findViewById(R.id.register_et_jobtitle);
        et_address = (EditText)findViewById(R.id.register_et_address);
        profileImage = (RoundedImageView)findViewById(R.id.register_profileImage);
        register_button = (TextView)findViewById(R.id.register_button);
        progress = findViewById(R.id.register_progress_wheel);
        progress_background = findViewById(R.id.register_progress_wheel_background);
        et_dateofbirth.setFocusable(false);
        et_dateofbirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                showDatePickerDialog();
            }

        });
        register_button.setOnClickListener(this);
        profileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                showImageChoosingDialog();
            }

        });
    }

    public void onDateSet(DatePickerDialog datepickerdialog, int i, int j, int k)
    {
        et_dateofbirth.setText((new StringBuilder()).append(k).append("/").append(j).append("/").append(i).toString());
    }

    public void showImageChoosingDialog()
    {
        final MaterialDialog image_selection_dialog = (new MaterialDialog.Builder(this)).title("Choose Image").customView(R.layout.custom_imagechoosing_dialog, false).build();
        image_selection_dialog.show();
        AppsAdapter appsadapter = new AppsAdapter(getListofImage());
        final GridView mGrid = (GridView)image_selection_dialog.findViewById(R.id.imageChoosing_gridview);
        mGrid.setAdapter(appsadapter);
        appsadapter.notifyDataSetChanged();
        mGrid.setChoiceMode(2);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView adapterview, View view, int i, long l)
            {

                AppsAdapter adp = (AppsAdapter) mGrid.getAdapter();
                Uri imguri = Uri.fromFile(new File(adp.getItem(i).toString()));
                try
                {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
                    Picasso.with(RegisterActivity.this).load(imguri).resize(512, 512).centerCrop().into(profileImage);
                    image_selection_dialog.dismiss();
                }
                // Misplaced declaration of an exception variable
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

        });
    }


}
