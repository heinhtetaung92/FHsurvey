package com.algo.hha.fhsurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.algo.hha.fhsurvey.model.ProjectFormData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heinhtetaung on 7/4/15.
 */
public class ProjectFormDataORM {

    private static final String TABLE_NAME = "projectformdatalist";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_FORMID = "formid";
    private static final String COLUMN_FORMID_TYPE = "TEXT";

    private static final String COLUMN_PROJECTID = "projectid";
    private static final String COLUMN_PROJECTID_TYPE = "TEXT";

    private static final String COLUMN_FORMDESCRIPTION = "formdescription";
    private static final String COLUMN_FORMDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_FORMINDEX = "formindex";
    private static final String COLUMN_FORMINDEX_TYPE = "TEXT";

    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_STATUS_TYPE = "TEXT";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + COLUMN_FORMID + " " + COLUMN_FORMID_TYPE + COMMA_SEP +
            COLUMN_PROJECTID + " " + COLUMN_PROJECTID_TYPE + COMMA_SEP +
            COLUMN_FORMDESCRIPTION + " " + COLUMN_FORMDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_FORMINDEX + " " + COLUMN_FORMINDEX_TYPE + COMMA_SEP +
            COLUMN_STATUS + " " + COLUMN_STATUS_TYPE + ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    public static int insertProjectDataListtoDatabase(Context context,
                                                      List<ProjectFormData> ProjectFormDataList) {
        if(context == null){
            Log.e("ProviderItem", "Context is null");
        }

        SQLiteHelper dbHelper = new SQLiteHelper(context);

        if(dbHelper == null){
            Log.e("ProviderItem", "DBHelper is null");
        }
        int count = 0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            deleteDataFromTable(context);
            for(int i=0;i<ProjectFormDataList.size();i++) {
                ContentValues values = ProjectFormDataToContentValues(ProjectFormDataList.get(i));
                long ID = database.insert(TABLE_NAME,
                        "null", values);
                if(ID > 0){
                    count++;
                }
            }
            // database.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            // database.close();
        }
        database.close();
        dbHelper.close();
        return count;
    }

    public static long insertProjectFormDatatoDatabase(Context context,
                                                   ProjectFormData ProjectFormData) {
        if(context == null){
            Log.e("ProviderItem", "Context is null");
        }

        long ID = 0;
        SQLiteHelper dbHelper = new SQLiteHelper(context);

        if(dbHelper == null){
            Log.e("ProviderItem", "DBHelper is null");
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            ContentValues values = ProjectFormDataToContentValues(ProjectFormData);
            ID = database.insert(TABLE_NAME,
                    "null", values);


            // database.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            // database.close();
        }
        database.close();
        dbHelper.close();
        return ID;
    }

    /**
     *
     * @param data
     * @return ContentValues
     *          Accept Project Item and convert to ContentValues to insert into database
     */
    private static ContentValues ProjectFormDataToContentValues(ProjectFormData data) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FORMID, data.get_formID());
        values.put(COLUMN_PROJECTID, data.get_projectID());
        values.put(COLUMN_FORMDESCRIPTION, data.get_formDescription());
        values.put(COLUMN_FORMINDEX, data.get_formIndex());
        values.put(COLUMN_STATUS, data.get_status());

        return values;
    }


    public static List<ProjectFormData> getProjectFormDatalist(
            Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM "
                + TABLE_NAME, null);
        List<ProjectFormData> providerList = new ArrayList<ProjectFormData>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ProjectFormData dOrg = cursorToObject(cursor);
                providerList.add(dOrg);
                cursor.moveToNext();
            }
        }
        cursor.close();
        database.close();
        dbHelper.close();
        return providerList;
    }

    /**
     * Populates a Post object with data from a Cursor
     *
     * @param cursor
     * @return
     */
    private static ProjectFormData cursorToObject(
            Cursor cursor) {
        ProjectFormData provider = new ProjectFormData();
        provider.set_formID(cursor.getString(cursor.getColumnIndex(COLUMN_FORMID)));
        provider.set_projectID(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTID)));
        provider.set_formDescription(cursor.getString(cursor.getColumnIndex(COLUMN_FORMDESCRIPTION)));
        provider.set_formIndex(cursor.getString(cursor.getColumnIndex(COLUMN_FORMINDEX)));
        provider.set_status(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));

        return provider;
    }

    private static void deleteDataFromTable(Context context){
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(TABLE_NAME, null, null);
        database.close();
        dbHelper.close();
    }

    public static void deleteDataFromTableWithItemId(Context context, ProjectFormData item){
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_PROJECTID + "=?", new String[]{item.get_projectID()});
        database.close();
        dbHelper.close();
    }

}
