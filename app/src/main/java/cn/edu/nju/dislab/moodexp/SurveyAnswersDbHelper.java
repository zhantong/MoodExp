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
    public static final String DATABASE_NAME = "SurveyAnswers.db";

    public SurveyAnswersDbHelper(){
        this(MainApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static class AnswersTable implements BaseColumns{
        static final String TABLE_NAME = "answers";
        static final String COLUMN_NAME_ANSWER = "answer";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
    private static final String SQL_CREATE_TABLE_ANSWERS=
            "CREATE TABLE "+ AnswersTable.TABLE_NAME+" ("+
                    AnswersTable._ID+" INTEGER PRIMARY KEY,"+
                    AnswersTable.COLUMN_NAME_ANSWER+" TEXT,"+
                    AnswersTable.COLUMN_NAME_TIMESTAMP+" INTEGER)";
    public SurveyAnswersDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ANSWERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
