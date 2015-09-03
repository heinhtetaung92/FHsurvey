package com.algo.hha.fhsurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.algo.hha.fhsurvey.model.QuestionFormData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heinhtetaung on 7/4/15.
 */
public class QuestionFormDataORM {

    private static final String TABLE_NAME = "questionformdata";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_PROJECTID = "projectid";
    private static final String COLUMN_PROJECTID_TYPE = "TEXT";

    private static final String COLUMN_PROJECTNAME = "projectname";
    private static final String COLUMN_PROJECTNAME_TYPE = "TEXT";

    private static final String COLUMN_FORMID = "formid";
    private static final String COLUMN_FORMID_TYPE = "TEXT";

    private static final String COLUMN_FORMDESCRIPTION = "formdescription";
    private static final String COLUMN_FORMDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_FORMINDEX = "formindex";
    private static final String COLUMN_FORMINDEX_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONGROUPID = "questiongroupid";
    private static final String COLUMN_QUESTIONGROUPID_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONGROUPINDEX = "questiongroupindex";
    private static final String COLUMN_QUESTIONGROUPINDEX_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONGROUPDESCRIPTION = "questiongroupdescription";
    private static final String COLUMN_QUESTIONGROUPDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONID = "questionid";
    private static final String COLUMN_QUESTIONID_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONINDEX = "questionindex";
    private static final String COLUMN_QUESTIONINDEX_TYPE = "TEXT";

    private static final String COLUMN_CONDITION = "condition";
    private static final String COLUMN_CONDITION_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONSHORTCODE = "questionshortcode";
    private static final String COLUMN_QUESTIONSHORTCODE_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONDESCRIPTION = "questiondescription";
    private static final String COLUMN_QUESTIONDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_QUESTIONINSTRUCTION = "questioininstruction";
    private static final String COLUMN_QUESTIONINSTRUCTION_TYPE = "TEXT";

    private static final String COLUMN_ANSWERTYPEID = "answertypeid";
    private static final String COLUMN_ANSWERTYPEID_TYPE = "TEXT";

    private static final String COLUMN_ANSWERTYPEDESCRIPTION = "answertypedescription";
    private static final String COLUMN_ANSWERTYPEDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_ANSWERID = "answerid";
    private static final String COLUMN_ANSWERID_TYPE = "TEXT";

    private static final String COLUMN_ANSWERDESCRIPTION = "answerdescription";
    private static final String COLUMN_ANSWERDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_ANSWERINDEX = "answerindex";
    private static final String COLUMN_ANSWERINDEX_TYPE = "TEXT";

    private static final String COLUMN_SKIPPEDTO = "skippedto";
    private static final String COLUMN_SKIPPEDTO_TYPE = "TEXT";

    private static final String COLUMN_ANSWERCOLUMNID = "answercolumnid";
    private static final String COLUMN_ANSWERCOLUMNID_TYPE = "TEXT";

    private static final String COLUMN_COLUMNDESCRIPTION = "columndescription";
    private static final String COLUMN_COLUMNDESCRIPTION_TYPE = "TEXT";

    private static final String COLUMN_ANSWERCOLUMNINDEX = "answercolumnindex";
    private static final String COLUMN_ANSWERCOLUMNINDEX_TYPE = "TEXT";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + COLUMN_PROJECTID + " " + COLUMN_PROJECTID_TYPE + COMMA_SEP +
            COLUMN_PROJECTNAME + " " + COLUMN_PROJECTNAME_TYPE + COMMA_SEP +
            COLUMN_FORMID + " " + COLUMN_FORMID_TYPE + COMMA_SEP +
            COLUMN_FORMDESCRIPTION + " " + COLUMN_FORMDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_FORMINDEX + " " + COLUMN_FORMINDEX_TYPE + COMMA_SEP +
            COLUMN_QUESTIONGROUPID + " " + COLUMN_QUESTIONGROUPID_TYPE + COMMA_SEP +
            COLUMN_QUESTIONGROUPINDEX + " " + COLUMN_QUESTIONGROUPINDEX_TYPE + COMMA_SEP +
            COLUMN_QUESTIONGROUPDESCRIPTION + " " + COLUMN_QUESTIONGROUPDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_QUESTIONID + " " + COLUMN_QUESTIONID_TYPE + COMMA_SEP +
            COLUMN_QUESTIONINDEX + " " + COLUMN_QUESTIONINDEX_TYPE + COMMA_SEP +
            COLUMN_CONDITION + " " + COLUMN_CONDITION_TYPE + COMMA_SEP +
            COLUMN_QUESTIONSHORTCODE + " " + COLUMN_QUESTIONSHORTCODE_TYPE + COMMA_SEP +
            COLUMN_QUESTIONDESCRIPTION + " " + COLUMN_QUESTIONDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_QUESTIONINSTRUCTION + " " + COLUMN_QUESTIONINSTRUCTION_TYPE + COMMA_SEP +
            COLUMN_ANSWERTYPEID + " " + COLUMN_ANSWERTYPEID_TYPE + COMMA_SEP +
            COLUMN_ANSWERTYPEDESCRIPTION + " " + COLUMN_ANSWERTYPEDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_ANSWERID + " " + COLUMN_ANSWERID_TYPE + COMMA_SEP +
            COLUMN_ANSWERDESCRIPTION + " " + COLUMN_ANSWERDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_ANSWERINDEX + " " + COLUMN_ANSWERINDEX_TYPE + COMMA_SEP +
            COLUMN_SKIPPEDTO + " " + COLUMN_SKIPPEDTO_TYPE + COMMA_SEP +
            COLUMN_ANSWERCOLUMNID + " " + COLUMN_ANSWERCOLUMNID_TYPE + COMMA_SEP +
            COLUMN_COLUMNDESCRIPTION + " " + COLUMN_COLUMNDESCRIPTION_TYPE + COMMA_SEP +
            COLUMN_ANSWERCOLUMNINDEX + " " + COLUMN_ANSWERCOLUMNINDEX_TYPE + ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    public static int insertQuestionFormDataListtoDatabase(Context context,
                                                           List<QuestionFormData> QuestionFormDataList) {
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

            if(QuestionFormDataList.size() > 0)//check data size is more than one
                deleteDataFromTable(context, QuestionFormDataList.get(0).get_FormID());//delete data from table where formid is equal

            for(int i=0;i<QuestionFormDataList.size();i++) {
                ContentValues values = QuestionFormDataToContentValues(QuestionFormDataList.get(i));
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

    public static long insertQuestionFormDatatoDatabase(Context context,
                                                       QuestionFormData QuestionFormData) {
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
            ContentValues values = QuestionFormDataToContentValues(QuestionFormData);
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
    private static ContentValues QuestionFormDataToContentValues(QuestionFormData data) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECTID, data.get_ProjectID());
        values.put(COLUMN_PROJECTNAME, data.get_ProjectName());
        values.put(COLUMN_FORMID, data.get_FormID());
        values.put(COLUMN_FORMDESCRIPTION, data.get_FormDescription());
        values.put(COLUMN_FORMINDEX, data.get_FormIndex());
        values.put(COLUMN_QUESTIONGROUPID, data.get_QuestionGroupID());
        values.put(COLUMN_QUESTIONGROUPINDEX, data.get_QuestionGroupIndex());
        values.put(COLUMN_QUESTIONGROUPDESCRIPTION, data.get_QuestionGroupDescription());
        values.put(COLUMN_QUESTIONID, data.get_QuestionID());
        values.put(COLUMN_QUESTIONINDEX, data.get_QuestionIndex());
        values.put(COLUMN_CONDITION, data.get_Condition());
        values.put(COLUMN_QUESTIONSHORTCODE, data.get_QuestionShortCode());
        values.put(COLUMN_QUESTIONDESCRIPTION, data.get_QuestionDescription());
        values.put(COLUMN_QUESTIONINSTRUCTION, data.get_QuestionInstruction());
        values.put(COLUMN_ANSWERTYPEID, data.get_AnswerTypeID());
        values.put(COLUMN_ANSWERTYPEDESCRIPTION, data.get_AnswerTypeDescription());
        values.put(COLUMN_ANSWERID, data.get_AnswerID());
        values.put(COLUMN_ANSWERDESCRIPTION, data.get_AnswerDescription());
        values.put(COLUMN_ANSWERINDEX, data.get_AnswerIndex());
        values.put(COLUMN_SKIPPEDTO, data.get_SkippedTo());
        values.put(COLUMN_ANSWERCOLUMNID, data.get_AnswerColumnID());
        values.put(COLUMN_COLUMNDESCRIPTION, data.get_ColumnDescription());
        values.put(COLUMN_ANSWERCOLUMNINDEX, data.get_AnswerColumnIndex());

        return values;
    }


    public static List<QuestionFormData> getQuestionFormDatalist(Context context, String form_id) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM "
                + TABLE_NAME + " WHERE " + COLUMN_FORMID + "=?", new String[]{form_id});
        List<QuestionFormData> providerList = new ArrayList<QuestionFormData>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                QuestionFormData dOrg = cursorToObject(cursor);
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
    private static QuestionFormData cursorToObject(
            Cursor cursor) {
        QuestionFormData provider = new QuestionFormData();
        provider.set_ProjectID(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTID)));
        provider.set_ProjectName(cursor.getString(cursor.getColumnIndex(COLUMN_PROJECTNAME)));
        provider.set_FormID(cursor.getString(cursor.getColumnIndex(COLUMN_FORMID)));
        provider.set_FormDescription(cursor.getString(cursor.getColumnIndex(COLUMN_FORMDESCRIPTION)));
        provider.set_FormIndex(cursor.getString(cursor.getColumnIndex(COLUMN_FORMINDEX)));
        provider.set_QuestionGroupID(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONGROUPID)));
        provider.set_QuestionGroupIndex(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONGROUPINDEX)));
        provider.set_QuestionGroupDescription(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONGROUPDESCRIPTION)));
        provider.set_QuestionID(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONID)));
        provider.set_QuestionIndex(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONINDEX)));
        provider.set_Condition(cursor.getString(cursor.getColumnIndex(COLUMN_CONDITION)));
        provider.set_QuestionShortCode(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONSHORTCODE)));
        provider.set_QuestionDescription(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONDESCRIPTION)));
        provider.set_QuestionInstruction(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTIONINSTRUCTION)));
        provider.set_AnswerTypeID(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERTYPEID)));
        provider.set_AnswerTypeDescription(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERTYPEDESCRIPTION)));
        provider.set_AnswerID(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERID)));
        provider.set_AnswerDescription(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERDESCRIPTION)));
        provider.set_AnswerIndex(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERINDEX)));
        provider.set_SkippedTo(cursor.getString(cursor.getColumnIndex(COLUMN_SKIPPEDTO)));
        provider.set_AnswerColumnID(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERCOLUMNID)));
        provider.set_ColumnDescription(cursor.getString(cursor.getColumnIndex(COLUMN_COLUMNDESCRIPTION)));
        provider.set_AnswerColumnIndex(cursor.getString(cursor.getColumnIndex(COLUMN_ANSWERCOLUMNINDEX)));

        return provider;
    }


    private static void deleteDataFromTable(Context context, String form_id){
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_FORMID + "=?", new String[]{form_id});
        database.close();
        dbHelper.close();
    }

    public static void deleteDataFromTableWithItemId(Context context, QuestionFormData item){
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_PROJECTID + "=?", new String[]{item.get_ProjectID()});
        database.close();
        dbHelper.close();
    }

}
