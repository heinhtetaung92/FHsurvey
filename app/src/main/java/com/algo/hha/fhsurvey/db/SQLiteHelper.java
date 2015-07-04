package com.algo.hha.fhsurvey.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.algo.hha.fhsurvey.model.ProjectFormData;


/**
 * Created by heinhtetaung on 5/2/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseWrapper";

    private static final String DATABASE_NAME = "mytopup";
    private static final int DATABASE_VERSION = 3;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProjectDataORM.SQL_CREATE_TABLE);
        db.execSQL(ProjectFormDataORM.SQL_CREATE_TABLE);
        db.execSQL(QuestionFormDataORM.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ProjectDataORM.SQL_DROP_TABLE);
        db.execSQL(ProjectFormDataORM.SQL_DROP_TABLE);
        db.execSQL(QuestionFormDataORM.SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        if (this.getWritableDatabase() != null)
            this.getWritableDatabase().close();

        super.close();
    }

}
