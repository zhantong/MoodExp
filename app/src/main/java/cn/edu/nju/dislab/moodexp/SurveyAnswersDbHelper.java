package cn.edu.nju.dislab.moodexp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by zhantong on 2016/12/26.
 */

public class SurveyAnswersDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public SurveyAnswersDbHelper(String name){
        this(MainApplication.getContext(), name, null, DATABASE_VERSION);
    }
    public static class InfoTable implements BaseColumns{
        static final String TABLE_NAME = "info";
        static final String COLUMN_NAME_KEY = "key";
        static final String COLUMN_NAME_VALUE = "value";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    public static class AnswersTable implements BaseColumns{
        static final String TABLE_NAME = "answers";
        static final String COLUMN_NAME_QUESTION_ID = "qestion_id";
        static final String COLUMN_NAME_ANSWER = "answer";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    private static final String SQL_CREATE_TABLE_INFO=
            "CREATE TABLE "+ InfoTable.TABLE_NAME+" ("+
                    InfoTable.COLUMN_NAME_KEY+" TEXT PRIMARY KEY,"+
                    InfoTable.COLUMN_NAME_VALUE+" TEXT,"+
                    InfoTable.COLUMN_NAME_TIMESTAMP+" INTEGER)";
    private static final String SQL_CREATE_TABLE_ANSWERS=
            "CREATE TABLE "+ AnswersTable.TABLE_NAME+" ("+
                    AnswersTable.COLUMN_NAME_QUESTION_ID+" INTEGER PRIMARY KEY,"+
                    AnswersTable.COLUMN_NAME_ANSWER+" TEXT,"+
                    AnswersTable.COLUMN_NAME_TIMESTAMP+" INTEGER)";
    public SurveyAnswersDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_INFO);
        db.execSQL(SQL_CREATE_TABLE_ANSWERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
