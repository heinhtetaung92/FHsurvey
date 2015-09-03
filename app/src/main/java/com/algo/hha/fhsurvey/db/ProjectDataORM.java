package com.algo.hha.fhsurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.algo.hha.fhsurvey.model.ProjectData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heinhtetaung on 5/19/15.
 */
public class ProjectDataORM {


    private static final String TABLE_NAME = "projectdatalist";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_PROJECTID = "projectid";
    private static final String COLUMN_PROJECTID_TYPE = "TEXT";

    private static final String COLUMN_PROJECTNAME = "projectname";
    private static final String COLUMN_PROJECTNAME_TYPE = "TEXT";

    private static final String COLUMN_PROJECTNAME_EE = "projectname_EE";
    private static final String COLUMN_PROJECTNAME_EE_TYPE = "TEXT";

    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_PROJECTSTATUS = "projectstatus";
    private static final String COLUMN_PROJECTSTATUS_TYPE = "TEXT";

    private static final String COLUMN_STARTDATE = "startdate";
    private static final String COLUMN_STARTDATE_TYPE = "TEXT";

    private static final String COLUMN_COMPLETEDATE = "completedate";
    private static final String COLUMN_COMPLETEDATE_TYPE = "TEXT";

    private static final String COLUMN_EXPIREDATE = "expiredate";
    private static final String COLUMN_EXPIREDATE_TYPE = "TEXT";

    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_STATUS_TYPE = "TEXT";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + COLUMN_PROJECTID + " " + COLUMN_PROJECTID_TYPE + COMMA_SEP +
            COLUMN_PROJECTNAME + " " + COLUMN_PROJECTNAME_TYPE + COMMA_SEP +
            COLUMN_PROJECTNAME_EE + " " + COLUMN_PROJECTNAME_EE_TYPE + COMMA_SEP +
            COLUMN_DESCRIPTION + " " + COLUMN_DESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_PROJECTSTATUS + " " + COLUMN_PROJECTSTATUS_TYPE + COMMA_SEP +
            COLUMN_STARTDATE + " " + COLUMN_STARTDATE_TYPE + COMMA_SEP +
            COLUMN_COMPLETEDATE + " " + COLUMN_COMPLETEDATE_TYPE + COMMA_SEP +
            COLUMN_EXPIREDATE + " " + COLUMN_EXPIREDATE_TYPE + COMMA_SEP +
            COLUMN_STATUS + " " + COLUMN_STATUS_TYPE + ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    public static int insertProjectDataListtoDatabase(Context context,
                                                 List<ProjectData> projectDataList) {
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
            for(int i=0;i<projectDataList.size();i++) {
                ContentValues values = ProjectDataToContentValues(projectDataList.get(i));
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

    public static long insertProjectDatatoDatabase(Context context,
                                             ProjectData projectData) {
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
            ContentValues values = ProjectDataToContentValues(projectData);
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
    private static ContentValues ProjectDataToContentValues(ProjectData data) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECTID, data.get_projectID());
        values.put(COLUMN_PROJECTNAME, data.get_projectName());
        values.put(COLUMN_PROJECTNAME_EE, data.get_projectName_EE());
        values.put(COLUMN_DESCRIPTION, data.get_description());
        values.put(COLUMN_PROJECTSTATUS, data.get_projectStatus());
        values.put(COLUMN_STARTDATE, data.get_startDate());
        values.put(COLUMN_COMPLETEDATE, data.get_completeDate());
        values.put(COLUMN_EXPIREDATE, data.get_expireDate());
        values.put(COLUMN_STATUS, data.get_status());

        return values;
    }


    public static List<ProjectData> getProjectDatalist(
            Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM "
                + TABLE_NAME, null);
        List<ProjectData> providerList = new ArrayList<ProjectData>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ProjectData dOrg = cursorToObject(cursor);
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
    private static ProjectData cursorToObject(
            Cursor cursor) {
        ProjectData provider = new ProjectData();
        provider.set_projectID(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTID)));
        provider.set_projectName(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTNAME)));
        provider.set_projectName_EE(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTNAME_EE)));
        provider.set_description(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        provider.set_projectStatus(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTSTATUS)));
        provider.set_startDate(cursor.getString(cursor.getColumnIndex(COLUMN_STARTDATE)));
        provider.set_completeDate(cursor.getString(cursor.getColumnIndex(COLUMN_COMPLETEDATE)));
        provider.set_expireDate(cursor.getString(cursor.getColumnIndex(COLUMN_EXPIREDATE)));
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

    public static void deleteDataFromTableWithItemId(Context context, ProjectData item){
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_PROJECTID + "=?", new String[]{item.get_projectID()});
        database.close();
        dbHelper.close();
    }

}
